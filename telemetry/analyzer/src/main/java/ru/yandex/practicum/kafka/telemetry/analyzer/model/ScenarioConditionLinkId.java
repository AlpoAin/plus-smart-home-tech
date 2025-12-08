package ru.yandex.practicum.kafka.telemetry.analyzer.model;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class ScenarioConditionLinkId implements Serializable {
    private Long scenarioId;
    private String sensorId;
    private Long conditionId;
}
