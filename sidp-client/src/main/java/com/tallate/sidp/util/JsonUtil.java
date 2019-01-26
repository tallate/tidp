package com.tallate.sidp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class JsonUtil {

    private static final ObjectMapper om = new ObjectMapper();

    public static String write(Object obj) throws JsonProcessingException {
        return om.writeValueAsString(obj);
    }

    public static <T> T read(String json, Class<T> type) throws IOException {
        return om.readValue(json, type);
    }

}
