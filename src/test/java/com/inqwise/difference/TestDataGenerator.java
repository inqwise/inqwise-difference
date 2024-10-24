package com.inqwise.difference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class for generating test data in JSON format.
 */
public class TestDataGenerator {
    
    private static final List<String> NAMES = Arrays.asList("summers", "winters", "autumn", "spring", "rainy");
    private static final List<Integer> AGES = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    private static final List<String> GENDERS = Arrays.asList("male", "female");
    private static final List<String> COUNTRIES = Arrays.asList("india", "aus", "nz", "sl", "rsa", "wi", "eng", "bang", "pak");
    private static final List<String> FRIENDS = Arrays.asList(
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j"
    );

    /**
     * Generates a JSON array with a specified number of random objects.
     *
     * @param count the number of JSON objects to generate.
     * @return the generated JSON array node.
     */
    public static JsonNode generate(int count) {
        ArrayNode jsonArray = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < count; i++) {
            jsonArray.add(generateSingleObject());
        }
        return jsonArray;
    }

    /**
     * Generates a single random JSON object.
     *
     * @return the generated JSON object node.
     */
    private static ObjectNode generateSingleObject() {
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();

        // Fill JSON object with random values
        objectNode.put("name", getRandomElement(NAMES));
        objectNode.put("age", getRandomElement(AGES));
        objectNode.put("gender", getRandomElement(GENDERS));

        // Create random subsets for countries and friends
        objectNode.set("country", getRandomArray(COUNTRIES));
        objectNode.set("friends", getRandomArray(FRIENDS));

        return objectNode;
    }

    /**
     * Generates an ArrayNode from a subset of a list of strings.
     *
     * @param items the original list of strings.
     * @return the generated JSON array node.
     */
    private static ArrayNode getRandomArray(List<String> items) {
        int start = ThreadLocalRandom.current().nextInt(items.size() / 2);
        int end = start + ThreadLocalRandom.current().nextInt(items.size() / 2);
        return getArrayNode(items.subList(start, Math.min(end, items.size())));
    }

    /**
     * Converts a list of strings into a JSON array node.
     *
     * @param args the list of strings.
     * @return the resulting JSON array node.
     */
    private static ArrayNode getArrayNode(List<String> args) {
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (String arg : args) {
            arrayNode.add(arg);
        }
        return arrayNode;
    }

    /**
     * Returns a random element from a given list.
     *
     * @param list the list to pick from.
     * @param <T>  the type of elements in the list.
     * @return a randomly selected element from the list.
     */
    private static <T> T getRandomElement(List<T> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}