package ru.yandex.practicum.commerce.shoppingstore.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.data.web.PagedModel;

import java.io.IOException;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PagedModelJsonSerializer extends StdSerializer<PagedModel> {

    public PagedModelJsonSerializer() {
        super((Class) PagedModel.class);
    }

    @Override
    public void serialize(PagedModel value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        // чтобы не гадать с методами getContent()/getPage() — берём свойства через BeanWrapper
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(value);
        Object content = bw.getPropertyValue("content");
        Object page = bw.getPropertyValue("page");

        gen.writeStartObject();

        gen.writeFieldName("content");
        provider.defaultSerializeValue(content, gen);

        // алиас под тесты
        gen.writeFieldName("products");
        provider.defaultSerializeValue(content, gen);

        gen.writeFieldName("page");
        provider.defaultSerializeValue(page, gen);

        gen.writeEndObject();
    }
}
