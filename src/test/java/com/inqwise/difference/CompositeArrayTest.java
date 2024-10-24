package com.inqwise.difference;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

class CompositeArrayTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static ArrayNode jsonNode;

    @BeforeAll
    public static void beforeClass() throws IOException {
        String path = "/testdata/diff-composite-array.json";
        try (InputStream resourceAsStream = CompositeArrayTest.class.getResourceAsStream(path)) {
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
            String message = jsonNode.get(i).get("message").asText(); // Changed to asText() for proper text extraction

            EnumSet<DiffFlags> flags = EnumSet.of(DiffFlags.OMIT_COMPOSITE_ARRAY);
            
            JsonNode diff = JsonDiff.asJson(first, second, flags);

            Assertions.assertEquals(patch, diff, message);
        }
    }
}