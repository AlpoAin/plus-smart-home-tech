package ru.yandex.practicum.kafka.telemetry.analyzer.processing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.analyzer.model.*;
import ru.yandex.practicum.kafka.telemetry.analyzer.repository.*;
import ru.yandex.practicum.kafka.telemetry.analyzer.serialization.HubEventDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.hubs-group-id}")
    private String groupId;

    @Value("${app.topics.hubs}")
    private String hubsTopic;

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final ScenarioConditionLinkRepository scenarioConditionLinkRepository;
    private final ScenarioActionLinkRepository scenarioActionLinkRepository;

    @Override
    public void run() {
        log.info("Starting HubEventProcessor on topic {}", hubsTopic);

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true"); // автофикс - повторная обработка допустима
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, HubEventAvro> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(List.of(hubsTopic));

            while (true) {
                var records = consumer.poll(Duration.ofSeconds(1));
                if (records.isEmpty()) {
                    continue;
                }

                records.forEach(rec -> handleEvent(rec.value()));
            }
        } catch (Exception e) {
            log.error("Error in HubEventProcessor", e);
        }
    }

    private void handleEvent(HubEventAvro event) {
        if (event == null || event.getPayload() == null) {
            return;
        }
        String hubId = event.getHubId();
        Object payload = event.getPayload();

        if (payload instanceof DeviceAddedEventAvro da) {
            handleDeviceAdded(hubId, da);
        } else if (payload instanceof DeviceRemovedEventAvro dr) {
            handleDeviceRemoved(hubId, dr);
        } else if (payload instanceof ScenarioAddedEventAvro sa) {
            handleScenarioAdded(hubId, sa);
        } else if (payload instanceof ScenarioRemovedEventAvro sr) {
            handleScenarioRemoved(hubId, sr);
        }
    }

    private void handleDeviceAdded(String hubId, DeviceAddedEventAvro da) {
        sensorRepository.findByIdAndHubId(da.getId(), hubId)
                .or(() -> {
                    Sensor s = new Sensor();
                    s.setId(da.getId());
                    s.setHubId(hubId);
                    return Optional.of(sensorRepository.save(s));
                });
        log.info("Device added: hub={}, sensor={}", hubId, da.getId());
    }

    private void handleDeviceRemoved(String hubId, DeviceRemovedEventAvro dr) {
        String sensorId = dr.getId();
        scenarioConditionLinkRepository.deleteBySensorId(sensorId);
        scenarioActionLinkRepository.deleteBySensorId(sensorId);
        sensorRepository.findByIdAndHubId(sensorId, hubId)
                .ifPresent(sensorRepository::delete);
        log.info("Device removed: hub={}, sensor={}", hubId, sensorId);
    }

    private void handleScenarioAdded(String hubId, ScenarioAddedEventAvro sa) {
        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, sa.getName())
                .orElseGet(() -> {
                    Scenario s = new Scenario();
                    s.setHubId(hubId);
                    s.setName(sa.getName());
                    return s;
                });

        // если есть старые связи - удаляем
        if (scenario.getId() != null) {
            scenarioConditionLinkRepository.deleteByScenarioId(scenario.getId());
            scenarioActionLinkRepository.deleteByScenarioId(scenario.getId());
        }

        scenario = scenarioRepository.save(scenario);

        // условия
        for (ScenarioConditionAvro cAvro : sa.getConditions()) {
            ensureSensorExists(hubId, cAvro.getSensorId());

            Condition cond = new Condition();
            cond.setType(ConditionType.valueOf(cAvro.getType().name()));
            cond.setOperation(ConditionOperation.valueOf(cAvro.getOperation().name()));
            cond.setValue(convertConditionValue(cAvro.getValue()));

            cond = conditionRepository.save(cond);

            ScenarioConditionLink link = new ScenarioConditionLink();
            link.setScenarioId(scenario.getId());
            link.setSensorId(cAvro.getSensorId());
            link.setConditionId(cond.getId());
            scenarioConditionLinkRepository.save(link);
        }

        // действия
        for (DeviceActionAvro aAvro : sa.getActions()) {
            ensureSensorExists(hubId, aAvro.getSensorId());

            Action action = new Action();
            action.setType(ActionType.valueOf(aAvro.getType().name()));
            action.setValue(aAvro.getValue() == null ? null : (Integer) aAvro.getValue());
            action = actionRepository.save(action);

            ScenarioActionLink link = new ScenarioActionLink();
            link.setScenarioId(scenario.getId());
            link.setSensorId(aAvro.getSensorId());
            link.setActionId(action.getId());
            scenarioActionLinkRepository.save(link);
        }

        log.info("Scenario added/updated: hub={}, name={}", hubId, sa.getName());
    }

    private Integer convertConditionValue(Object value) {
        if (value == null) return null;
        if (value instanceof Integer i) return i;
        if (value instanceof Boolean b) return b ? 1 : 0;
        return null;
    }

    private void ensureSensorExists(String hubId, String sensorId) {
        sensorRepository.findByIdAndHubId(sensorId, hubId)
                .or(() -> {
                    Sensor s = new Sensor();
                    s.setId(sensorId);
                    s.setHubId(hubId);
                    return Optional.of(sensorRepository.save(s));
                });
    }

    private void handleScenarioRemoved(String hubId, ScenarioRemovedEventAvro sr) {
        scenarioRepository.findByHubIdAndName(hubId, sr.getName())
                .ifPresent(scenario -> {
                    Long id = scenario.getId();
                    scenarioConditionLinkRepository.deleteByScenarioId(id);
                    scenarioActionLinkRepository.deleteByScenarioId(id);
                    scenarioRepository.delete(scenario);
                });
        log.info("Scenario removed: hub={}, name={}", hubId, sr.getName());
    }
}
