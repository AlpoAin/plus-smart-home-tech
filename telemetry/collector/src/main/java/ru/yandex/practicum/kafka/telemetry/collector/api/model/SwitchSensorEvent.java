package ru.yandex.practicum.kafka.telemetry.collector.api.model;
import lombok.*; @Getter @Setter @ToString(callSuper = true)
public class SwitchSensorEvent extends SensorEvent {
    private Boolean state;
    @Override public SensorEventType getType(){ return SensorEventType.SWITCH_SENSOR_EVENT; }
}