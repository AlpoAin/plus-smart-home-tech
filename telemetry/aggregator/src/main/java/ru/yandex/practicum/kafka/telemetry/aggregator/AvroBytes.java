package ru.yandex.practicum.kafka.telemetry.aggregator;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;

import java.io.ByteArrayOutputStream;

public final class AvroBytes {
    private AvroBytes() {}

    public static <T extends SpecificRecordBase> byte[] toBytes(T record) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            var writer = new SpecificDatumWriter<T>(record.getSchema());
            BinaryEncoder enc = EncoderFactory.get().binaryEncoder(out, null);
            writer.write(record, enc);
            enc.flush();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Avro serialization failed", e);
        }
    }
}
