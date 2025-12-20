package ru.yandex.practicum.commerce.shoppingstore.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.data.domain.PageImpl;

import java.io.IOException;

public class PageImplJsonSerializer extends StdSerializer<PageImpl> {

    public PageImplJsonSerializer() {
        super(PageImpl.class);
    }

    @Override
    public void serialize(PageImpl value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        // content
        gen.writeFieldName("content");
        provider.defaultSerializeValue(value.getContent(), gen);

        // алиас под тесты: response.products[0]
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
