package com.inqwise.difference;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Unit test for JSON diff functionality
 */
public class JsonDiffTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static ArrayNode jsonNode;

    @BeforeAll
    public static void beforeClass() throws IOException {
        String path = "/testdata/sample.json";
        try (InputStream resourceAsStream = JsonDiffTest.class.getResourceAsStream(path)) {
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
            JsonNode actualPatch = JsonDiff.asJson(first, second);
            JsonNode secondPrime = JsonPatch.apply(actualPatch, first);
            Assertions.assertEquals(second, secondPrime, "JSON Patch not symmetrical [index=" + i + ", first=" + first + "]");
        }
    }

    @Test
    public void testGeneratedJsonDiff() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            JsonNode first = TestDataGenerator.generate(random.nextInt(10));
            JsonNode second = TestDataGenerator.generate(random.nextInt(10));
            JsonNode actualPatch = JsonDiff.asJson(first, second);
            JsonNode secondPrime = JsonPatch.apply(actualPatch, first);
            Assertions.assertEquals(second, secondPrime, "Generated JSON diff did not produce expected result");
        }
    }

    @Test
    public void testRenderedRemoveOperationOmitsValueByDefault() {
        ObjectNode source = objectMapper.createObjectNode();
        ObjectNode target = objectMapper.createObjectNode();
        source.put("field", "value");

        JsonNode diff = JsonDiff.asJson(source, target);

        Assertions.assertEquals(Operation.REMOVE.rfcName(), diff.get(0).get("op").textValue());
        Assertions.assertEquals("/field", diff.get(0).get("path").textValue());
        Assertions.assertNull(diff.get(0).get("value"), "Expected 'value' to be omitted by default");
    }

    @Test
    public void testRenderedRemoveOperationRetainsValueIfOmitDiffFlagNotSet() {
        ObjectNode source = objectMapper.createObjectNode();
        ObjectNode target = objectMapper.createObjectNode();
        source.put("field", "value");

        EnumSet<DiffFlags> flags = DiffFlags.defaults().clone();
        Assertions.assertTrue(flags.remove(DiffFlags.OMIT_VALUE_ON_REMOVE), "Expected OMIT_VALUE_ON_REMOVE to be set by default");
        JsonNode diff = JsonDiff.asJson(source, target, flags);

        Assertions.assertEquals(Operation.REMOVE.rfcName(), diff.get(0).get("op").textValue());
        Assertions.assertEquals("/field", diff.get(0).get("path").textValue());
        Assertions.assertEquals("value", diff.get(0).get("value").textValue(), "Expected 'value' to be retained when OMIT_VALUE_ON_REMOVE is not set");
    }

    @Test
    public void testRenderedOperationsExceptMoveAndCopy() throws IOException {
        JsonNode source = objectMapper.readTree("{\"age\": 10}");
        JsonNode target = objectMapper.readTree("{\"height\": 10}");

        EnumSet<DiffFlags> flags = DiffFlags.dontNormalizeOpIntoMoveAndCopy().clone();

        JsonNode diff = JsonDiff.asJson(source, target, flags);

        for (JsonNode d : diff) {
            Assertions.assertNotEquals(Operation.MOVE.rfcName(), d.get("op").textValue(), "Unexpected MOVE operation found");
            Assertions.assertNotEquals(Operation.COPY.rfcName(), d.get("op").textValue(), "Unexpected COPY operation found");
        }

        JsonNode targetPrime = JsonPatch.apply(diff, source);
        Assertions.assertEquals(target, targetPrime, "Applying diff did not produce expected result");
    }

    @Test
    public void testPath() throws IOException {
        JsonNode source = objectMapper.readTree("{\"profiles\":{\"abc\":[],\"def\":[{\"hello\":\"world\"}]}}");
        JsonNode patch = objectMapper.readTree("[{\"op\":\"copy\",\"from\":\"/profiles/def/0\", \"path\":\"/profiles/def/0\"},{\"op\":\"replace\",\"path\":\"/profiles/def/0/hello\",\"value\":\"world2\"}]");

        JsonNode target = JsonPatch.apply(patch, source);
        JsonNode expected = objectMapper.readTree("{\"profiles\":{\"abc\":[],\"def\":[{\"hello\":\"world2\"},{\"hello\":\"world\"}]}}");
        Assertions.assertEquals(expected, target, "Patch application did not produce the expected result");
    }
}