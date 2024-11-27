package com.victorchimenton.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.victorchimenton.model.Category;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <T> List<T> loadJson(String path, String nodeName, Class<T> valueType) {
        try (InputStream input = JsonUtil.class.getClassLoader().getResourceAsStream(path)) {
            JsonNode root = MAPPER.readTree(input);
            JsonNode node = root.get(nodeName);
            List<T> list = new ArrayList<>();

            if (node != null) {
                node.forEach(item -> list.add(MAPPER.convertValue(item, valueType)));
            }

            return list;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar JSON: " + path, e);
        }
    }
}
