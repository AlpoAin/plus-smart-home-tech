package ru.yandex.practicum.kafka.telemetry.collector.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.kafka.telemetry.collector.api.model.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.collector.api.model.hub.HubEvent;
import ru.yandex.practicum.kafka.telemetry.collector.service.CollectorService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventsController {

    private final CollectorService service;

    @PostMapping("/sensors")
    public void collectSensor(@Valid @RequestBody SensorEvent event){
        service.sendSensorEvent(event);
    }

    @PostMapping("/hubs")
    public void collectHub(@Valid @RequestBody HubEvent event){
        service.sendHubEvent(event);
    }
}
