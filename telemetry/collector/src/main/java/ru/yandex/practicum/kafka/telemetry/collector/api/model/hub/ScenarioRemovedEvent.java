package ru.yandex.practicum.kafka.telemetry.collector.api.model.hub;
import lombok.*; @Getter @Setter @ToString(callSuper = true)
public class ScenarioRemovedEvent extends HubEvent {
    private String name;
    @Override public HubEventType getType(){ return HubEventType.SCENARIO_REMOVED; }
}