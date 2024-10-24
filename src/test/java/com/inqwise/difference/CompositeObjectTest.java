package com.inqwise.difference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

class CompositeObjectTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static ArrayNode jsonNode;

    @BeforeAll
    public static void beforeClass() throws IOException {
        String path = "/testdata/diff-composite-object.json";
        try (InputStream resourceAsStream = CompositeObjectTest.class.getResourceAsStream(path)) {
            if (resourceAsStream == null) {
                throw new IOException("Resource not found: " + path);
            }
            String testData = IOUtils.toString(resourceAsStream, "UTF-8");
            jsonNode = (ArrayNode) objectMapper.readTree(testData);
        }
    }

    @Test
    public void testSampleJsonDiff() {
        for (int i = 0; i < jsonNode.size(); i++) {
            JsonNode first = jsonNode.get(i).get("first");
            JsonNode second = jsonNode.get(i).get("second");
            JsonNode patch = jsonNode.get(i).get("patch");
            String message = jsonNode.get(i).get("message").asText(); // Using asText() for proper text extraction
            ArrayNode compositeObjectsNode = (ArrayNode) jsonNode.get(i).get("composite_objects");

            // Extracting the composite object paths into a List
            List<String> compositeObjects = new ArrayList<>();
            compositeObjectsNode.forEach(node -> compositeObjects.add(node.asText()));

            // Calculate the JSON diff with the specified composite objects
            JsonNode diff = JsonDiff.asJson(first, second, compositeObjects);

            // Assertion to verify the computed diff matches the expected patch
            Assertions.assertEquals(patch, diff, message);
        }
    }
}