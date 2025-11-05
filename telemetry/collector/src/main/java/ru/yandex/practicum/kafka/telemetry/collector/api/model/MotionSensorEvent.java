package ru.yandex.practicum.kafka.telemetry.collector.api.model;

import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
public class MotionSensorEvent extends SensorEvent {

    private Integer linkQuality;

    private Boolean motion;

    private Integer voltage;

    @Override
    public SensorEventType getType(){
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}