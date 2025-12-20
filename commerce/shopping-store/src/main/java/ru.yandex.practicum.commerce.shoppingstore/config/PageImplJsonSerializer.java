package ru.yandex.practicum.commerce.shoppingstore.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.data.domain.PageImpl;

import java.io.IOException;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PageImplJsonSerializer extends StdSerializer<PageImpl> {

    public PageImplJsonSerializer() {
        super((Class) PageImpl.class);
    }

    @Override
    public void serialize(PageImpl value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeFieldName("content");
        provider.defaultSerializeValue(value.getContent(), gen);

        // алиас под тесты
        gen.writeFieldName("products");
        provider.defaultSerializeValue(value.getContent(), gen);

        gen.writeObjectFieldStart("page");
        gen.writeNumberField("size", value.getSize());
        gen.writeNumberField("number", value.getNumber());
        gen.writeNumberField("totalElements", value.getTotalElements());
        gen.writeNumberField("totalPages", value.getTotalPages());
        gen.writeEndObject();

        gen.writeEndObject();
    }
}
