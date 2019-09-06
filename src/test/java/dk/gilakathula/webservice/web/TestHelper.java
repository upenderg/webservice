package dk.gilakathula.webservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TestHelper {
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static <T> T create(String jsonFileName, Class<T> clazz) throws IOException {
        try (final InputStream src = TestHelper.class.getClassLoader().getResourceAsStream(jsonFileName)) {
            return objectMapper.readValue(src, clazz);
        }
    }

    public static <T> List<T> createListOf(String jsonFileName, Class<T> clazz) throws IOException {
        try (final InputStream src = TestHelper.class.getClassLoader().getResourceAsStream(jsonFileName)) {
            CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return objectMapper.readValue(src, collectionType);
        }
    }
}
