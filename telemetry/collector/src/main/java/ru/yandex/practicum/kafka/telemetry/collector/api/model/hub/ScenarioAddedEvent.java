package ru.yandex.practicum.kafka.telemetry.collector.api.model.hub;

import lombok.*;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {

    private String name;

    private List<ScenarioCondition> conditions;

    private List<DeviceAction> actions;

    @Override
    public HubEventType getType(){
        return HubEventType.SCENARIO_ADDED;
    }
}