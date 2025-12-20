package ru.yandex.practicum.kafka.telemetry.collector.api.model.hub;

import lombok.*;

@Getter
@Setter
@ToString
public class ScenarioCondition {

    private String sensorId;

    private ConditionType type;

    private ConditionOperation operation;

    private Integer value;
}