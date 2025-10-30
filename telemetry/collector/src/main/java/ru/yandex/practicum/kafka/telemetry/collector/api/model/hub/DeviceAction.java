package ru.yandex.practicum.kafka.telemetry.collector.api.model.hub;
import lombok.*; @Getter @Setter @ToString
public class DeviceAction {
    private String sensorId;
    public enum ActionType { ACTIVATE, DEACTIVATE, INVERSE, SET_VALUE }
    private ActionType type;
    private Integer value;
}