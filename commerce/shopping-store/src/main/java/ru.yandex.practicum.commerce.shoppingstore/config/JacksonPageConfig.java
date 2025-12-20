package ru.yandex.practicum.commerce.shoppingstore.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedModel;

@Configuration
public class JacksonPageConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer shoppingStorePageCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule("shoppingStorePageModule");
            module.addSerializer(PageImpl.class, new PageImplJsonSerializer());
            module.addSerializer(PagedModel.class, new PagedModelJsonSerializer());
            builder.modules(module);
        };
    }
}
