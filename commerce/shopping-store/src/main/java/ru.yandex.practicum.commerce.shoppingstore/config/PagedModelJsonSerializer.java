package ru.yandex.practicum.commerce.shoppingstore.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.data.web.PagedModel;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PagedModelJsonSerializer extends StdSerializer<PagedModel> {

    public PagedModelJsonSerializer() {
        super(PagedModel.class);
    }

    @Override
    public void serialize(PagedModel value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        List<?> content = extractContent(value);

        gen.writeStartObject();

        gen.writeFieldName("content");
        provider.defaultSerializeValue(content, gen);

        gen.writeFieldName("products");
        provider.defaultSerializeValue(content, gen);

        writePage(value, gen);

        gen.writeEndObject();
    }

    private static List<?> extractContent(Object value) {
        Object content = invoke(value, "getContent", "content");
        if (content == null) return List.of();

        if (content instanceof List) return (List<?>) content;
        if (content instanceof Collection) return new ArrayList<>((Collection<?>) content);

        return List.of();
    }

    private static void writePage(Object value, JsonGenerator gen) throws IOException {
        Object meta = invoke(value, "getPage", "page", "getMetadata", "metadata");

        Long size = number(meta, value, "getSize", "size");
        Long number = number(meta, value, "getNumber", "number");
        Long totalElements = number(meta, value, "getTotalElements", "totalElements");
        Long totalPages = number(meta, value, "getTotalPages", "totalPages");

        gen.writeObjectFieldStart("page");
        if (size != null) gen.writeNumberField("size", size);
        if (number != null) gen.writeNumberField("number", number);
        if (totalElements != null) gen.writeNumberField("totalElements", totalElements);
        if (totalPages != null) gen.writeNumberField("totalPages", totalPages);
        gen.writeEndObject();
    }

    private static Long number(Object meta, Object root, String getter, String recordAccessor) {
        Object v = invoke(meta, getter, recordAccessor);
        if (v == null) v = invoke(root, getter, recordAccessor);
        return (v instanceof Number) ? ((Number) v).longValue() : null;
    }

    private static Object invoke(Object target, String... methodNames) {
        if (target == null) return null;
        for (String name : methodNames) {
            try {
                Method m = target.getClass().getMethod(name);
                return m.invoke(target);
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
