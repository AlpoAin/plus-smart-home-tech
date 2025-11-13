package ru.yandex.practicum.kafka.telemetry.analyzer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "scenario_conditions")
@IdClass(ScenarioConditionLinkId.class)
@Getter
@Setter
public class ScenarioConditionLink {

    @Id
    @Column(name = "scenario_id")
    private Long scenarioId;

    @Id
    @Column(name = "sensor_id")
    private String sensorId;

    @Id
    @Column(name = "condition_id")
    private Long conditionId;
}
