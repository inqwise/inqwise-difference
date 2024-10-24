package com.inqwise.difference;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class TestUtils {

    // Singleton ObjectMapper instance for consistent JSON handling
    public static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

    // Private constructor to prevent instantiation of this utility class
    private TestUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    /**
     * Loads a resource from the given path as a JSON node.
     *
     * @param path the path to the resource.
     * @return the loaded JSON node.
     * @throws IOException if the resource cannot be loaded or parsed.
     */
    public static JsonNode loadResourceAsJsonNode(String path) throws IOException {
        String testData = loadFromResources(path);
        if (testData == null) {
            throw new IOException("Failed to load resource from path: " + path);
        }
        return DEFAULT_MAPPER.readTree(testData);
    }

    /**
     * Loads the contents of a resource file as a string.
     *
     * @param path the path to the resource.
     * @return the contents of the resource as a string.
     * @throws IOException if the resource cannot be found or read.
     */
    public static String loadFromResources(String path) throws IOException {
        try (InputStream resourceAsStream = PatchTestCase.class.getResourceAsStream(path)) {
            if (resourceAsStream == null) {
                throw new IOException("Resource not found: " + path);
            }
            return IOUtils.toString(resourceAsStream, "UTF-8");
        }
    }
}