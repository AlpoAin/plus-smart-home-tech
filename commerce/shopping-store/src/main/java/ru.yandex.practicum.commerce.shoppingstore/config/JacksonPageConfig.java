package ru.yandex.practicum.commerce.shoppingstore.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;

import java.io.IOException;

@Configuration
public class JacksonPageConfig {

    @Bean
    public Module pageImplModule() {
        SimpleModule module = new SimpleModule("pageImplModule");
        module.addSerializer(PageImpl.class, new PageImplJsonSerializer());
        return module;
    }

    static class PageImplJsonSerializer extends JsonSerializer<PageImpl> {
        @Override
        public void serialize(PageImpl value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();

            gen.writeObjectField("content", value.getContent());

            gen.writeObjectFieldStart("page");
            gen.writeNumberField("size", value.getSize());
            gen.writeNumberField("number", value.getNumber());
            gen.writeNumberField("totalElements", value.getTotalElements());
            gen.writeNumberField("totalPages", value.getTotalPages());
            gen.writeEndObject();

            gen.writeEndObject();
        }
    }
}
