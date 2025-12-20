package ru.yandex.practicum.commerce.shoppingstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.yandex.practicum.interaction.dto.QuantityState;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToQuantityStateConverter());
    }

    private static class StringToQuantityStateConverter implements Converter<String, QuantityState> {
        @Override
        public QuantityState convert(String source) {
            return QuantityState.valueOf(source.toUpperCase());
        }
    }
}
