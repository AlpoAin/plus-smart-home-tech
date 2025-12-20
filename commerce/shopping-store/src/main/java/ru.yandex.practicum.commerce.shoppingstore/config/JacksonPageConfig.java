package ru.yandex.practicum.commerce.shoppingstore.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedModel;

@Configuration
public class JacksonPageConfig {

    @Bean
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Module pageAliasesModule() {
        SimpleModule module = new SimpleModule("pageAliasesModule");

        module.addSerializer((Class) PageImpl.class, new PageImplJsonSerializer());
        module.addSerializer((Class) PagedModel.class, new PagedModelJsonSerializer());

        return module;
    }
}
