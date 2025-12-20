package ru.yandex.practicum.kafka.telemetry.analyzer.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.analyzer.model.*;
import ru.yandex.practicum.kafka.telemetry.analyzer.repository.*;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioEngine {

    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final ScenarioConditionLinkRepository scenarioConditionLinkRepository;
    private final ScenarioActionLinkRepository scenarioActionLinkRepository;

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    /**
     * Главная точка — обработка одного снапшота.
     */
    public void processSnapshot(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
        if (scenarios.isEmpty()) {
            return;
        }

        for (Scenario scenario : scenarios) {
            if (checkScenarioConditions(scenario, snapshot)) {
                executeActions(scenario, snapshot);
            }
        }
    }

    /**
     * Проверяет, выполнены ли ВСЕ условия сценария для данного снапшота.
     */
    private boolean checkScenarioConditions(Scenario scenario, SensorsSnapshotAvro snapshot) {
        List<ScenarioConditionLink> links =
                scenarioConditionLinkRepository.findByScenarioId(scenario.getId());

        for (ScenarioConditionLink link : links) {
            Optional<Condition> opt = conditionRepository.findById(link.getConditionId());
            if (opt.isEmpty()) {
                return false;
            }
            Condition cond = opt.get();
            Integer sensorVal = extractValue(snapshot, link.getSensorId(), cond.getType());
            if (sensorVal == null || cond.getValue() == null) {
                return false;
            }
            if (!compare(sensorVal, cond.getValue(), cond.getOperation())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Выполняет действия сценария — отправляет gRPC-запросы в Hub Router.
     */
    private void executeActions(Scenario scenario, SensorsSnapshotAvro snapshot) {
        List<ScenarioActionLink> links =
                scenarioActionLinkRepository.findByScenarioId(scenario.getId());
        Instant ts = snapshot.getTimestamp();

        for (ScenarioActionLink link : links) {
            Optional<Action> opt = actionRepository.findById(link.getActionId());
            if (opt.isEmpty()) {
                continue;
            }
            Action action = opt.get();

            DeviceActionProto.Builder actionBuilder = DeviceActionProto.newBuilder()
                    .setSensorId(link.getSensorId())
                    .setType(mapActionType(action.getType()));

            if (action.getValue() != null) {
                actionBuilder.setValue(action.getValue());
            }

            DeviceActionRequest request = DeviceActionRequest.newBuilder()
                    .setHubId(snapshot.getHubId())
                    .setScenarioName(scenario.getName())
                    .setAction(actionBuilder.build())
                    .setTimestamp(toProtoTimestamp(ts))
                    .build();

            hubRouterClient.handleDeviceAction(request);

            log.info("Sent action: hub={}, scenario={}, sensor={}, type={}, value={}",
                    snapshot.getHubId(), scenario.getName(), link.getSensorId(),
                    action.getType(), action.getValue());
        }
    }

    private Timestamp toProtoTimestamp(Instant ts) {
        return Timestamp.newBuilder()
                .setSeconds(ts.getEpochSecond())
                .setNanos(ts.getNano())
                .build();
    }

    private boolean compare(int sensorVal, int expected, ConditionOperation op) {
        return switch (op) {
            case EQUALS       -> sensorVal == expected;
            case GREATER_THAN -> sensorVal > expected;
            case LOWER_THAN   -> sensorVal < expected;
        };
    }

    /**
     * Достаём числовое значение датчика из Avro-снимка по sensorId и типу условия.
     */
    private Integer extractValue(SensorsSnapshotAvro snapshot,
                                 String sensorId,
                                 ConditionType type) {
        SensorStateAvro state = snapshot.getSensorsState().get(sensorId);
        if (state == null || state.getData() == null) {
            return null;
        }

        Object data = state.getData();

        return switch (type) {
            case MOTION -> {
                if (data instanceof MotionSensorAvro d) {
                    yield d.getMotion() ? 1 : 0;
                }
                yield null;
            }
            case LUMINOSITY -> {
                if (data instanceof LightSensorAvro d) {
                    yield d.getLuminosity();
                }
                yield null;
            }
            case SWITCH -> {
                if (data instanceof SwitchSensorAvro d) {
                    yield d.getState() ? 1 : 0;
                }
                yield null;
            }
            case TEMPERATURE -> {
                if (data instanceof TemperatureSensorAvro d) {
                    yield d.getTemperatureC();
                } else if (data instanceof ClimateSensorAvro d) {
                    yield d.getTemperatureC();
                }
                yield null;
            }
            case CO2LEVEL -> {
                if (data instanceof ClimateSensorAvro d) {
                    yield d.getCo2Level();
                }
                yield null;
            }
            case HUMIDITY -> {
                if (data instanceof ClimateSensorAvro d) {
                    yield d.getHumidity();
                }
                yield null;
            }
        };
    }

    private ActionTypeProto mapActionType(ActionType type) {
        return ActionTypeProto.valueOf(type.name());
    }
}
