package ru.yandex.practicum.kafka.telemetry.collector.api.model.hub;
import lombok.*; @Getter @Setter @ToString(callSuper = true)
public class DeviceRemovedEvent extends HubEvent {
    private String id;
    @Override public HubEventType getType(){ return HubEventType.DEVICE_REMOVED; }
}