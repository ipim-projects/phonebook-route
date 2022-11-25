package ru.ipim.phonebook.phonebookroute.routes;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

// Конвериацмя json
public final class Json {
    private Json() {
    }

    private static final ObjectMapper json = createDefaultMapper();

    // результат - строка с json
    public static final String toJsonString(Object source) {
        if (source == null) {
            return null;
        }
        try {
            return json.writeValueAsString(source);
        } catch (final JsonProcessingException e) {
            return e.toString();
        }
    }

    // маппер для вывода 
    public static final ObjectMapper createDefaultMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        return mapper;
    }
}
