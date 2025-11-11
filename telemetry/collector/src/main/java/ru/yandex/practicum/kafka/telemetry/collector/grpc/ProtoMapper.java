package ru.yandex.practicum.kafka.telemetry.collector.grpc;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.collector.api.model.*;
import ru.yandex.practicum.kafka.telemetry.collector.api.model.hub.*;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;


import java.time.Instant;
import java.util.stream.Collectors;

@Component
public class ProtoMapper {

    // ---- SensorEventProto -> api.model.SensorEvent ----
    public SensorEvent toApi(SensorEventProto proto) {
        final Instant ts = (proto.hasTimestamp())
                ? Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos())
                : Instant.now();

        switch (proto.getPayloadCase()) {
            case MOTION_SENSOR: {
                var src = proto.getMotionSensor();
                var dst = new MotionSensorEvent();
                dst.setId(proto.getId());
                dst.setHubId(proto.getHubId());
                dst.setTimestamp(ts);
                dst.setLinkQuality(src.getLinkQuality());
                dst.setMotion(src.getMotion());
                dst.setVoltage(src.getVoltage());
                return dst;
            }
            case TEMPERATURE_SENSOR: {
                TemperatureSensorProto src = proto.getTemperatureSensor();
                var dst = new TemperatureSensorEvent();
                dst.setId(proto.getId());
                dst.setHubId(proto.getHubId());
                dst.setTimestamp(ts);
                dst.setTemperatureC(src.getTemperatureC());
                dst.setTemperatureF(src.getTemperatureF());
                return dst;
            }
            case LIGHT_SENSOR: {
                LightSensorProto src = proto.getLightSensor();
                var dst = new LightSensorEvent();
                dst.setId(proto.getId());
                dst.setHubId(proto.getHubId());
                dst.setTimestamp(ts);
                dst.setLinkQuality(src.getLinkQuality());
                dst.setLuminosity(src.getLuminosity());
                return dst;
            }
            case CLIMATE_SENSOR: {
                ClimateSensorProto src = proto.getClimateSensor();
                var dst = new ClimateSensorEvent();
                dst.setId(proto.getId());
                dst.setHubId(proto.getHubId());
                dst.setTimestamp(ts);
                dst.setTemperatureC(src.getTemperatureC());
                dst.setHumidity(src.getHumidity());
                dst.setCo2Level(src.getCo2Level());
                return dst;
            }
            case SWITCH_SENSOR: {
                SwitchSensorProto src = proto.getSwitchSensor();
                var dst = new SwitchSensorEvent();
                dst.setId(proto.getId());
                dst.setHubId(proto.getHubId());
                dst.setTimestamp(ts);
                dst.setState(src.getState());
                return dst;
            }
            case PAYLOAD_NOT_SET:
            default:
                throw new IllegalArgumentException("SensorEventProto payload is empty or unsupported: " + proto.getPayloadCase());
        }
    }

    // ---- HubEventProto -> api.model.hub.HubEvent ----
    public ru.yandex.practicum.kafka.telemetry.collector.api.model.hub.HubEvent toApi(HubEventProto proto) {
        final Instant ts = (proto.hasTimestamp())
                ? Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos())
                : Instant.now();

        switch (proto.getPayloadCase()) {
            case DEVICE_ADDED: {
                DeviceAddedEventProto p = proto.getDeviceAdded();
                var dst = new DeviceAddedEvent();
                dst.setHubId(proto.getHubId());
                dst.setTimestamp(ts);
                dst.setId(p.getId());
                dst.setDeviceType(mapDeviceType(p.getType()));
                return dst;
            }
            case DEVICE_REMOVED: {
                DeviceRemovedEventProto p = proto.getDeviceRemoved();
                var dst = new DeviceRemovedEvent();
                dst.setHubId(proto.getHubId());
                dst.setTimestamp(ts);
                dst.setId(p.getId());
                return dst;
            }
            case SCENARIO_ADDED: {
                ScenarioAddedEventProto p = proto.getScenarioAdded();
                var dst = new ScenarioAddedEvent();
                dst.setHubId(proto.getHubId());
                dst.setTimestamp(ts);
                dst.setName(p.getName());
                dst.setConditions(p.getConditionList().stream().map(this::mapCondition).collect(Collectors.toList()));
                dst.setActions(p.getActionList().stream().map(this::mapAction).collect(Collectors.toList()));
                return dst;
            }
            case SCENARIO_REMOVED: {
                ScenarioRemovedEventProto p = proto.getScenarioRemoved();
                var dst = new ScenarioRemovedEvent();
                dst.setHubId(proto.getHubId());
                dst.setTimestamp(ts);
                dst.setName(p.getName());
                return dst;
            }
            case PAYLOAD_NOT_SET:
            default:
                throw new IllegalArgumentException("HubEventProto payload is empty or unsupported: " + proto.getPayloadCase());
        }
    }

    private ScenarioCondition mapCondition(ScenarioConditionProto p) {
        var c = new ScenarioCondition();
        c.setSensorId(p.getSensorId());
        c.setType(mapConditionType(p.getType()));
        c.setOperation(mapOperation(p.getOperation()));

        // value — oneof: bool_value | int_value
        switch (p.getValueCase()) {
            case BOOL_VALUE -> c.setValue(p.getBoolValue() ? 1 : 0);
            case INT_VALUE  -> c.setValue(p.getIntValue());
            case VALUE_NOT_SET -> c.setValue(null);
            default -> c.setValue(null);
        }
        return c;
    }

    private DeviceAction mapAction(DeviceActionProto p) {
        var a = new DeviceAction();
        a.setSensorId(p.getSensorId());
        a.setType(mapActionType(p.getType()));
        a.setValue(p.hasValue() ? p.getValue() : null); // здесь именно optional int32
        return a;
    }

    // ---- enum mappers ----
    private DeviceType mapDeviceType(ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto t) {
        return switch (t) {
            case MOTION_SENSOR      -> DeviceType.MOTION_SENSOR;
            case TEMPERATURE_SENSOR -> DeviceType.TEMPERATURE_SENSOR;
            case LIGHT_SENSOR       -> DeviceType.LIGHT_SENSOR;
            case CLIMATE_SENSOR     -> DeviceType.CLIMATE_SENSOR;
            case SWITCH_SENSOR      -> DeviceType.SWITCH_SENSOR;
            default -> throw new IllegalArgumentException("Unsupported DeviceTypeProto: " + t);
        };
    }

    private ConditionType mapConditionType(ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto t) {
        return switch (t) {
            case MOTION      -> ConditionType.MOTION;
            case LUMINOSITY  -> ConditionType.LUMINOSITY;
            case SWITCH      -> ConditionType.SWITCH;
            case TEMPERATURE -> ConditionType.TEMPERATURE;
            case CO2LEVEL    -> ConditionType.CO2LEVEL;
            case HUMIDITY    -> ConditionType.HUMIDITY;
            default -> throw new IllegalArgumentException("Unsupported ConditionTypeProto: " + t);
        };
    }

    private ConditionOperation mapOperation(ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto op) {
        return switch (op) {
            case EQUALS       -> ConditionOperation.EQUALS;
            case GREATER_THAN -> ConditionOperation.GREATER_THAN;
            case LOWER_THAN   -> ConditionOperation.LOWER_THAN;
            default -> throw new IllegalArgumentException("Unsupported ConditionOperationProto: " + op);
        };
    }

    private DeviceAction.ActionType mapActionType(ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto t) {
        return switch (t) {
            case ACTIVATE   -> DeviceAction.ActionType.ACTIVATE;
            case DEACTIVATE -> DeviceAction.ActionType.DEACTIVATE;
            case INVERSE    -> DeviceAction.ActionType.INVERSE;
            case SET_VALUE  -> DeviceAction.ActionType.SET_VALUE;
            default -> throw new IllegalArgumentException("Unsupported ActionTypeProto: " + t);
        };
    }
}
