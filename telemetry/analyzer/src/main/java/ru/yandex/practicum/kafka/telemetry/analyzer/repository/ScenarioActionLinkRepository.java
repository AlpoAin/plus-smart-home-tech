package ru.yandex.practicum.kafka.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.kafka.telemetry.analyzer.model.ScenarioActionLink;
import ru.yandex.practicum.kafka.telemetry.analyzer.model.ScenarioActionLinkId;

import java.util.List;

public interface ScenarioActionLinkRepository extends JpaRepository<ScenarioActionLink, ScenarioActionLinkId> {

    List<ScenarioActionLink> findByScenarioId(Long scenarioId);

    void deleteByScenarioId(Long scenarioId);

    void deleteBySensorId(String sensorId);
}
