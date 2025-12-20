package ru.yandex.practicum.commerce.shoppingstore.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.data.domain.Sort;

import java.io.IOException;

public class PageModule extends SimpleModule {

    public PageModule() {
        super("PageModule");
        addSerializer(Sort.class, new SortSerializer());
    }

    private static class SortSerializer extends JsonSerializer<Sort> {
        @Override
        public void serialize(Sort sort, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartArray();
            for (Sort.Order order : sort) {
                gen.writeStartObject();
                gen.writeStringField("direction", order.getDirection().name());
                gen.writeStringField("property", order.getProperty());
                gen.writeBooleanField("ignoreCase", order.isIgnoreCase());
                gen.writeStringField("nullHandling", order.getNullHandling().name());
                gen.writeBooleanField("ascending", order.isAscending());
                gen.writeBooleanField("descending", order.isDescending());
                gen.writeEndObject();
            }
            gen.writeEndArray();
        }
    }
}
