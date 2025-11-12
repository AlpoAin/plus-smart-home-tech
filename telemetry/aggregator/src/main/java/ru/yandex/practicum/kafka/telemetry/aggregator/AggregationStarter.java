package ru.yandex.practicum.kafka.telemetry.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.aggregator.serialization.SensorEventDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static ru.yandex.practicum.kafka.telemetry.aggregator.AvroBytes.toBytes;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    @Value("${spring.kafka.bootstrap-servers}") private String bootstrapServers;
    @Value("${app.kafka.group-id}")           private String groupId;
    @Value("${app.topics.sensors}")           private String sensorsTopic;
    @Value("${app.topics.snapshots}")         private String snapshotsTopic;

    // Состояния: hubId -> snapshot
    private final Map<String, SensorsSnapshotAvro> snapshots = new ConcurrentHashMap<>();

    private KafkaConsumer<String, SensorEventAvro> consumer;
    private KafkaProducer<String, byte[]> producer;

    public void start() {
        log.info("Starting Aggregator: sensors={}, snapshots={}, bootstrap={}", sensorsTopic, snapshotsTopic, bootstrapServers);

        // --- init consumer
        var cProps = new Properties();
        cProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        cProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        cProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        cProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class.getName());
        cProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        cProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumer = new KafkaConsumer<>(cProps);
        consumer.subscribe(List.of(sensorsTopic));

        // --- init producer
        var pProps = new Properties();
        pProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        pProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        pProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        pProps.put(ProducerConfig.ACKS_CONFIG, "all");
        pProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        producer = new KafkaProducer<>(pProps);

        try {
            while (true) {
                var records = consumer.poll(Duration.ofSeconds(1));
                if (records.isEmpty()) continue;

                records.forEach(rec -> {
                    SensorEventAvro event = rec.value();
                    if (event == null) return;

                    updateState(event).ifPresent(snapshot -> {
                        byte[] payload = toBytes(snapshot);
                        producer.send(new org.apache.kafka.clients.producer.ProducerRecord<>(
                                snapshotsTopic,
                                snapshot.getHubId().toString(), // key
                                payload
                        ));
                        log.debug("Snapshot updated for hubId={}, sensors={}", snapshot.getHubId(), snapshot.getSensorsState().size());
                    });
                });

                // фиксируем смещения только после успешной обработки
                consumer.commitAsync();
            }

        } catch (WakeupException ignored) {
            // ignore
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                log.info("Flushing producer and committing sync...");
                producer.flush();
                consumer.commitSync();
            } finally {
                try {
                    log.info("Closing consumer...");
                    consumer.close();
                } finally {
                    log.info("Closing producer...");
                    producer.close();
                }
            }
        }
    }

    /**
     * Обновляет снапшот соответствующего хаба. Пишем снапшот только если он действительно изменился.
     */
    private Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        final String hubId = event.getHubId().toString();
        final String sensorId = event.getId().toString();

        // достаём/создаём снапшот
        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(hubId, h -> {
            var s = new SensorsSnapshotAvro();
            s.setHubId(h);
            s.setTimestamp(event.getTimestamp());
            s.setSensorsState(new HashMap<>());
            return s;
        });

        // текущее состояние датчика в снапшоте
        SensorStateAvro oldState = snapshot.getSensorsState().get(sensorId);

        // готовим новое состояние из события
        var newState = new SensorStateAvro();
        newState.setTimestamp(event.getTimestamp());
        newState.setData(event.getPayload()); // union-объект (класс одного из *SensorAvro)

        // дедупликация: если старый ts новее или данные те же — ничего не делаем
        if (oldState != null) {
            boolean older = oldState.getTimestamp().isAfter(newState.getTimestamp());
            boolean sameData = Objects.equals(oldState.getData(), newState.getData());

            if (older || sameData) {
                return Optional.empty();
            }
        }

        // записываем свежие данные датчика
        snapshot.getSensorsState().put(sensorId, newState);
        // timestamp снимка — момент последнего обновления
        snapshot.setTimestamp(event.getTimestamp());

        return Optional.of(snapshot);
    }
}
