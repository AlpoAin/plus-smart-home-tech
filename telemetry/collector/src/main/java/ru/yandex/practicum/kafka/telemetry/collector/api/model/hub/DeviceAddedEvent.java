package ru.yandex.practicum.kafka.telemetry.collector.api.model.hub;

import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAddedEvent extends HubEvent {

    private String id;

    private DeviceType deviceType;

    @Override
    public HubEventType getType(){
        return HubEventType.DEVICE_ADDED;
    }
}