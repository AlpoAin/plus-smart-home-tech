package ru.yandex.practicum.kafka.telemetry.collector.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.kafka.telemetry.collector.api.model.*;
import ru.yandex.practicum.kafka.telemetry.collector.api.model.hub.*;
import ru.yandex.practicum.kafka.telemetry.event.*; // avro classes

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CollectorService {

    private final KafkaTemplate<String, byte[]> producer;

    @Value("${app.topics.sensors}") private String sensorsTopic;
    @Value("${app.topics.hubs}") private String hubsTopic;

    public void sendSensorEvent(SensorEvent e) {
        SensorEventAvro avro = mapSensor(e);
        byte[] payload = toBytes(avro);
        producer.send(sensorsTopic, e.getId(), payload);
    }

    public void sendHubEvent(HubEvent e) {
        HubEventAvro avro = mapHub(e);
        byte[] payload = toBytes(avro);
        producer.send(hubsTopic, e.getHubId(), payload);
    }

    private SensorEventAvro mapSensor(SensorEvent e){
        Object payload;
        if (e instanceof LightSensorEvent le) {
            payload = LightSensorAvro.newBuilder()
                    .setLinkQuality(nz(le.getLinkQuality())).setLuminosity(nz(le.getLuminosity())).build();
        } else if (e instanceof MotionSensorEvent me) {
            payload = MotionSensorAvro.newBuilder()
                    .setLinkQuality(nz(me.getLinkQuality())).setMotion(Boolean.TRUE.equals(me.getMotion()))
                    .setVoltage(nz(me.getVoltage())).build();
        } else if (e instanceof ClimateSensorEvent ce) {
            payload = ClimateSensorAvro.newBuilder()
                    .setTemperatureC(nz(ce.getTemperatureC())).setHumidity(nz(ce.getHumidity()))
                    .setCo2Level(nz(ce.getCo2Level())).build();
        } else if (e instanceof SwitchSensorEvent se) {
            payload = SwitchSensorAvro.newBuilder().setState(Boolean.TRUE.equals(se.getState())).build();
        } else if (e instanceof TemperatureSensorEvent te) {
            payload = TemperatureSensorAvro.newBuilder()
                    .setId(te.getId()).setHubId(te.getHubId())
                    .setTimestamp(ts(e.getTimestamp()))
                    .setTemperatureC(nz(te.getTemperatureC())).setTemperatureF(nz(te.getTemperatureF()))
                    .build();
        } else throw new IllegalArgumentException("Unknown sensor event " + e);

        return SensorEventAvro.newBuilder()
                .setId(e.getId()).setHubId(e.getHubId())
                .setTimestamp(ts(e.getTimestamp()))
                .setPayload(payload).build();
    }

    private HubEventAvro mapHub(HubEvent e){
        Object payload;
        if (e instanceof DeviceAddedEvent da) {
            payload = DeviceAddedEventAvro.newBuilder()
                    .setId(da.getId()).setType(map(da.getDeviceType())).build();
        } else if (e instanceof DeviceRemovedEvent dr) {
            payload = DeviceRemovedEventAvro.newBuilder().setId(dr.getId()).build();
        } else if (e instanceof ScenarioRemovedEvent sr) {
            payload = ScenarioRemovedEventAvro.newBuilder().setName(sr.getName()).build();
        } else if (e instanceof ScenarioAddedEvent sa) {
            payload = ScenarioAddedEventAvro.newBuilder()
                    .setName(sa.getName())
                    .setConditions(sa.getConditions().stream().map(c ->
                            ScenarioConditionAvro.newBuilder()
                                    .setSensorId(c.getSensorId())
                                    .setType(map(c.getType()))
                                    .setOperation(map(c.getOperation()))
                                    .setValue(nullOrIntOrBool(c.getValue()))
                                    .build()
                    ).toList())
                    .setActions(sa.getActions().stream().map(a ->
                            DeviceActionAvro.newBuilder()
                                    .setSensorId(a.getSensorId())
                                    .setType(map(a.getType()))
                                    .setValue(a.getValue())
                                    .build()
                    ).toList())
                    .build();
        } else throw new IllegalArgumentException("Unknown hub event " + e);

        return HubEventAvro.newBuilder()
                .setHubId(e.getHubId())
                .setTimestamp(ts(e.getTimestamp()))
                .setPayload(payload)
                .build();
    }

    private ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro map(DeviceType t){
        return ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro.valueOf(t.name());
    }
    private ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro map(DeviceAction.ActionType t){
        return ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro.valueOf(t.name());
    }
    private ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro map(ConditionType t){
        return ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro.valueOf(t.name());
    }
    private ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro map(ConditionOperation t){
        return ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro.valueOf(t.name());
    }

    private Integer nz(Integer v){ return v==null?0:v; }
    private Instant ts(Instant i) {
        return (i == null ? Instant.now() : i);
    }

    private Object nullOrIntOrBool(Integer v){ return v==null?null:v; }

    private <T extends org.apache.avro.specific.SpecificRecordBase> byte[] toBytes(T record){
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()){
            DatumWriter<T> writer = new SpecificDatumWriter<>(record.getSchema());
            BinaryEncoder enc = EncoderFactory.get().binaryEncoder(out, null);
            writer.write(record, enc); enc.flush(); return out.toByteArray();
        } catch (Exception ex){ throw new RuntimeException(ex); }
    }
}
