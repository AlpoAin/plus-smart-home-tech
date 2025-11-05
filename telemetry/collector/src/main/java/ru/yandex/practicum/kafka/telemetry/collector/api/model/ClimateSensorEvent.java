package ru.yandex.practicum.kafka.telemetry.collector.api.model;

import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
public class ClimateSensorEvent extends SensorEvent {
    private Integer temperatureC;

    private Integer humidity;

    private Integer co2Level;

    @Override
    public SensorEventType getType(){
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}