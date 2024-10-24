package com.inqwise.difference;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.EnumSet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestNodesEmissionTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static EnumSet<DiffFlags> flags;

    @BeforeAll
    public static void setupFlags() {
        flags = DiffFlags.defaults();
        flags.add(DiffFlags.EMIT_TEST_OPERATIONS);
    }

    @Test
    public void testNodeEmittedBeforeReplaceOperation() throws IOException {
        JsonNode source = mapper.readTree("{\"key\":\"original\"}");
        JsonNode target = mapper.readTree("{\"key\":\"replaced\"}");

        JsonNode diff = JsonDiff.asJson(source, target, flags);

        JsonNode testNode = mapper.readTree("{\"op\":\"test\",\"path\":\"/key\",\"value\":\"original\"}");
        assertEquals(2, diff.size(), "Expected diff size to be 2 for replace operation");
        assertEquals(testNode, diff.iterator().next(), "Expected the first operation to be a test operation");
    }

    @Test
    public void testNodeEmittedBeforeCopyOperation() throws IOException {
        JsonNode source = mapper.readTree("{\"key\":\"original\"}");
        JsonNode target = mapper.readTree("{\"key\":\"original\", \"copied\":\"original\"}");

        JsonNode diff = JsonDiff.asJson(source, target, flags);

        JsonNode testNode = mapper.readTree("{\"op\":\"test\",\"path\":\"/key\",\"value\":\"original\"}");
        assertEquals(2, diff.size(), "Expected diff size to be 2 for copy operation");
        assertEquals(testNode, diff.iterator().next(), "Expected the first operation to be a test operation");
    }

    @Test
    public void testNodeEmittedBeforeMoveOperation() throws IOException {
        JsonNode source = mapper.readTree("{\"key\":\"original\"}");
        JsonNode target = mapper.readTree("{\"moved\":\"original\"}");

        JsonNode diff = JsonDiff.asJson(source, target, flags);

        JsonNode testNode = mapper.readTree("{\"op\":\"test\",\"path\":\"/key\",\"value\":\"original\"}");
        assertEquals(2, diff.size(), "Expected diff size to be 2 for move operation");
        assertEquals(testNode, diff.iterator().next(), "Expected the first operation to be a test operation");
    }

    @Test
    public void testNodeEmittedBeforeRemoveOperation() throws IOException {
        JsonNode source = mapper.readTree("{\"key\":\"original\"}");
        JsonNode target = mapper.readTree("{}");

        JsonNode diff = JsonDiff.asJson(source, target, flags);

        JsonNode testNode = mapper.readTree("{\"op\":\"test\",\"path\":\"/key\",\"value\":\"original\"}");
        assertEquals(2, diff.size(), "Expected diff size to be 2 for remove operation");
        assertEquals(testNode, diff.iterator().next(), "Expected the first operation to be a test operation");
    }

    @Test
    public void testNodeEmittedBeforeRemoveFromMiddleOfArray() throws IOException {
        JsonNode source = mapper.readTree("{\"key\":[1,2,3]}");
        JsonNode target = mapper.readTree("{\"key\":[1,3]}");

        JsonNode diff = JsonDiff.asJson(source, target, flags);

        JsonNode testNode = mapper.readTree("{\"op\":\"test\",\"path\":\"/key/1\",\"value\":2}");
        assertEquals(2, diff.size(), "Expected diff size to be 2 for remove from middle of array");
        assertEquals(testNode, diff.iterator().next(), "Expected the first operation to be a test operation");
    }

    @Test
    public void testNodeEmittedBeforeRemoveFromTailOfArray() throws IOException {
        JsonNode source = mapper.readTree("{\"key\":[1,2,3]}");
        JsonNode target = mapper.readTree("{\"key\":[1,2]}");

        JsonNode diff = JsonDiff.asJson(source, target, flags);

        JsonNode testNode = mapper.readTree("{\"op\":\"test\",\"path\":\"/key/2\",\"value\":3}");
        assertEquals(2, diff.size(), "Expected diff size to be 2 for remove from tail of array");
        assertEquals(testNode, diff.iterator().next(), "Expected the first operation to be a test operation");
    }
}