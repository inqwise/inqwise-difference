package com.inqwise.difference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class JsonDiffTest2 {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static ArrayNode jsonNode;

    @BeforeAll
    public static void beforeAll() throws IOException {
        String path = "/testdata/diff.json";
        try (InputStream resourceAsStream = JsonDiffTest2.class.getResourceAsStream(path)) {
            if (resourceAsStream == null) {
                throw new IOException("Resource not found: " + path);
            }
            String testData = IOUtils.toString(resourceAsStream, "UTF-8");
            jsonNode = (ArrayNode) objectMapper.readTree(testData);
        }
    }

    @Test
    public void testPatchAppliedCleanly() {
        for (int i = 0; i < jsonNode.size(); i++) {
            JsonNode first = jsonNode.get(i).get("first");
            JsonNode second = jsonNode.get(i).get("second");
            JsonNode patch = jsonNode.get(i).get("patch");
            String message = jsonNode.get(i).get("message").asText(); // Use asText() for better text extraction

            // Apply the patch to the first node
            JsonNode secondPrime = JsonPatch.apply(patch, first);

            // Use Hamcrest assertThat for detailed comparison
            assertThat(message, secondPrime, equalTo(second));
        }
    }
}