package ru.yandex.practicum.kafka.telemetry.collector.api.model;
import lombok.*; @Getter @Setter @ToString(callSuper = true)
public class TemperatureSensorEvent extends SensorEvent {
    private Integer temperatureC; private Integer temperatureF;
    @Override public SensorEventType getType(){ return SensorEventType.TEMPERATURE_SENSOR_EVENT; }
}