package ru.yandex.practicum.kafka.telemetry.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.aggregator.config.KafkaConfig;
import ru.yandex.practicum.kafka.telemetry.aggregator.serialization.SensorEventDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static ru.yandex.practicum.kafka.telemetry.aggregator.AvroBytes.toBytes;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    @Value("${app.kafka.group-id}")
    private String groupId;

    @Value("${app.topics.sensors}")
    private String sensorsTopic;

    @Value("${app.topics.snapshots}")
    private String snapshotsTopic;

    private final KafkaConfig kafkaConfig;

    private final Map<String, SensorsSnapshotAvro> snapshots = new ConcurrentHashMap<>();

    private KafkaConsumer<String, SensorEventAvro> consumer;
    private KafkaProducer<String, byte[]> producer;

    public void start() {
        log.info("Starting Aggregator: sensors={}, snapshots={}, bootstrap={}",
                sensorsTopic, snapshotsTopic, kafkaConfig.getBootstrapServers());

        var cProps = kafkaConfig.consumerProps(groupId, SensorEventDeserializer.class);
        cProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        consumer = new KafkaConsumer<>(cProps);
        consumer.subscribe(List.of(sensorsTopic));

        var pProps = kafkaConfig.producerProps();
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
                        log.debug("Snapshot updated for hubId={}, sensors={}",
                                snapshot.getHubId(), snapshot.getSensorsState().size());
                    });
                });

                consumer.commitAsync();
            }

        } catch (WakeupException ignored) {

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

    private Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        final String hubId = event.getHubId().toString();
        final String sensorId = event.getId().toString();

        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(hubId, h -> {
            var s = new SensorsSnapshotAvro();
            s.setHubId(h);
            s.setTimestamp(event.getTimestamp());
            s.setSensorsState(new HashMap<>());
            return s;
        });

        SensorStateAvro oldState = snapshot.getSensorsState().get(sensorId);

        var newState = new SensorStateAvro();
        newState.setTimestamp(event.getTimestamp());
        newState.setData(event.getPayload());

        if (oldState != null) {
            boolean older = oldState.getTimestamp().isAfter(newState.getTimestamp());
            boolean sameData = Objects.equals(oldState.getData(), newState.getData());

            if (older || sameData) {
                return Optional.empty();
            }
        }

        snapshot.getSensorsState().put(sensorId, newState);
        snapshot.setTimestamp(event.getTimestamp());

        return Optional.of(snapshot);
    }
}