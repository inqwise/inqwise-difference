package com.inqwise.difference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.EnumSet;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ApiTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void applyInPlaceMutatesSource() throws Exception {
        JsonNode patch = readTree("[{ \"op\": \"add\", \"path\": \"/b\", \"value\": \"b-value\" }]");
        ObjectNode source = newObjectNode();
        ObjectNode beforeApplication = source.deepCopy();
        JsonPatch.apply(patch, source);
        assertThat(source, is(beforeApplication));
    }

    @Test
    public void applyDoesNotMutateSource() throws Exception {
        JsonNode patch = readTree("[{ \"op\": \"add\", \"path\": \"/b\", \"value\": \"b-value\" }]");
        ObjectNode source = newObjectNode();
        JsonPatch.applyInPlace(patch, source);
        assertThat(source.findValue("b").asText(), is("b-value"));
    }

    @Test
    public void applyDoesNotMutateSource2() throws Exception {
        JsonNode patch = readTree("[{ \"op\": \"add\", \"path\": \"/b\", \"value\": \"b-value\" }]");
        ObjectNode source = newObjectNode();
        ObjectNode beforeApplication = source.deepCopy();
        JsonPatch.apply(patch, source);
        assertThat(source, is(beforeApplication));
    }

    @Test
    public void applyInPlaceMutatesSourceWithCompatibilityFlags() throws Exception {
        JsonNode patch = readTree("[{ \"op\": \"add\", \"path\": \"/b\" }]");
        ObjectNode source = newObjectNode();
        JsonPatch.applyInPlace(patch, source, EnumSet.of(CompatibilityFlags.MISSING_VALUES_AS_NULLS));
        assertTrue(source.findValue("b").isNull());
    }

    @Test
    public void applyingNonArrayPatchShouldThrowAnException() throws IOException {
        JsonNode invalid = objectMapper.readTree("{\"not\": \"a patch\"}");
        JsonNode to = readTree("{\"a\":1}");
        assertThrows(InvalidJsonPatchException.class, () -> JsonPatch.apply(invalid, to));
    }

    @Test
    public void applyingAnInvalidArrayShouldThrowAnException() throws IOException {
        JsonNode invalid = readTree("[1, 2, 3, 4, 5]");
        JsonNode to = readTree("{\"a\":1}");
        assertThrows(InvalidJsonPatchException.class, () -> JsonPatch.apply(invalid, to));
    }

    @Test
    public void applyingAPatchWithAnInvalidOperationShouldThrowAnException() throws IOException {
        JsonNode invalid = readTree("[{\"op\": \"what\"}]");
        JsonNode to = readTree("{\"a\":1}");
        assertThrows(InvalidJsonPatchException.class, () -> JsonPatch.apply(invalid, to));
    }

    @Test
    public void validatingNonArrayPatchShouldThrowAnException() throws IOException {
        JsonNode invalid = readTree("{\"not\": \"a patch\"}");
        assertThrows(InvalidJsonPatchException.class, () -> JsonPatch.validate(invalid));
    }

    @Test
    public void validatingAnInvalidArrayShouldThrowAnException() throws IOException {
        JsonNode invalid = readTree("[1, 2, 3, 4, 5]");
        assertThrows(InvalidJsonPatchException.class, () -> JsonPatch.validate(invalid));
    }

    @Test
    public void validatingAPatchWithAnInvalidOperationShouldThrowAnException() throws IOException {
        JsonNode invalid = readTree("[{\"op\": \"what\"}]");
        assertThrows(InvalidJsonPatchException.class, () -> JsonPatch.validate(invalid));
    }

    private static JsonNode readTree(String jsonString) throws IOException {
        return objectMapper.readTree(jsonString);
    }

    private ObjectNode newObjectNode() {
        return objectMapper.createObjectNode();
    }
}