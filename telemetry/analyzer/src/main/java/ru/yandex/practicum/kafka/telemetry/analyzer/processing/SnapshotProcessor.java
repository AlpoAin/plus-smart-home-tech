package ru.yandex.practicum.kafka.telemetry.analyzer.processing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.analyzer.config.KafkaConfig;
import ru.yandex.practicum.kafka.telemetry.analyzer.serialization.SensorsSnapshotDeserializer;
import ru.yandex.practicum.kafka.telemetry.analyzer.service.ScenarioEngine;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    @Value("${app.kafka.snapshots-group-id}")
    private String groupId;

    @Value("${app.topics.snapshots}")
    private String snapshotsTopic;

    private final KafkaConfig kafkaConfig;

    private final ScenarioEngine scenarioEngine;

    public void start() {
        log.info("Starting SnapshotProcessor on topic {}, bootstrap={}",
                snapshotsTopic, kafkaConfig.getBootstrapServers());

        Properties props = kafkaConfig.consumerProps(groupId, SensorsSnapshotDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        try (KafkaConsumer<String, SensorsSnapshotAvro> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(List.of(snapshotsTopic));

            while (true) {
                var records = consumer.poll(Duration.ofSeconds(1));
                if (records.isEmpty()) {
                    continue;
                }

                records.forEach(rec -> {
                    SensorsSnapshotAvro snapshot = rec.value();
                    if (snapshot != null) {
                        scenarioEngine.processSnapshot(snapshot);
                    }
                });

                consumer.commitAsync();
            }
        } catch (WakeupException e) {
            log.info("SnapshotProcessor wakeup");
        } catch (Exception e) {
            log.error("Error in SnapshotProcessor", e);
        }
    }
}