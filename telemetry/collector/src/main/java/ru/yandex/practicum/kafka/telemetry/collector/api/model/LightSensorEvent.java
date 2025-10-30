package ru.yandex.practicum.kafka.telemetry.collector.api.model;
import lombok.*; @Getter @Setter @ToString(callSuper = true)
public class LightSensorEvent extends SensorEvent {
    private Integer linkQuality; private Integer luminosity;
    @Override public SensorEventType getType(){ return SensorEventType.LIGHT_SENSOR_EVENT; }
}