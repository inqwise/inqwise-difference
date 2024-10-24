package com.inqwise.difference;

import static com.inqwise.difference.CompatibilityFlags.ALLOW_MISSING_TARGET_OBJECT_ON_REPLACE;
import static com.inqwise.difference.CompatibilityFlags.MISSING_VALUES_AS_NULLS;
import static com.inqwise.difference.CompatibilityFlags.REMOVE_NONE_EXISTING_ARRAY_ELEMENT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.IOException;
import java.util.EnumSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CompatibilityTest {

    private ObjectMapper mapper;
    private JsonNode addNodeWithMissingValue;
    private JsonNode replaceNodeWithMissingValue;
    private JsonNode removeNoneExistingArrayElement;
    private JsonNode replaceNode;

    @BeforeEach
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        addNodeWithMissingValue = mapper.readTree("[{\"op\":\"add\",\"path\":\"/a\"}]");
        replaceNodeWithMissingValue = mapper.readTree("[{\"op\":\"replace\",\"path\":\"/a\"}]");
        removeNoneExistingArrayElement = mapper.readTree("[{\"op\": \"remove\",\"path\": \"/b/0\"}]");
        replaceNode = mapper.readTree("[{\"op\":\"replace\",\"path\":\"/a\",\"value\":true}]");
    }

    @Test
    public void withFlagAddShouldTreatMissingValuesAsNulls() throws IOException {
        JsonNode expected = mapper.readTree("{\"a\":null}");
        JsonNode result = JsonPatch.apply(addNodeWithMissingValue, mapper.createObjectNode(), EnumSet.of(MISSING_VALUES_AS_NULLS));
        assertThat(result, equalTo(expected));
    }

    @Test
    public void withFlagAddNodeWithMissingValueShouldValidateCorrectly() {
        JsonPatch.validate(addNodeWithMissingValue, EnumSet.of(MISSING_VALUES_AS_NULLS));
    }

    @Test
    public void withFlagReplaceShouldTreatMissingValuesAsNull() throws IOException {
        JsonNode source = mapper.readTree("{\"a\":\"test\"}");
        JsonNode expected = mapper.readTree("{\"a\":null}");
        JsonNode result = JsonPatch.apply(replaceNodeWithMissingValue, source, EnumSet.of(MISSING_VALUES_AS_NULLS));
        assertThat(result, equalTo(expected));
    }

    @Test
    public void withFlagReplaceNodeWithMissingValueShouldValidateCorrectly() {
        JsonPatch.validate(addNodeWithMissingValue, EnumSet.of(MISSING_VALUES_AS_NULLS));
    }

    @Test
    public void withFlagIgnoreRemoveNoneExistingArrayElement() throws IOException {
        JsonNode source = mapper.readTree("{\"b\": []}");
        JsonNode expected = mapper.readTree("{\"b\": []}");
        JsonNode result = JsonPatch.apply(removeNoneExistingArrayElement, source, EnumSet.of(REMOVE_NONE_EXISTING_ARRAY_ELEMENT));
        assertThat(result, equalTo(expected));
    }

    @Test
    public void withFlagReplaceShouldAddValueWhenMissingInTarget() throws Exception {
        JsonNode expected = mapper.readTree("{\"a\": true}");
        JsonNode result = JsonPatch.apply(replaceNode, mapper.createObjectNode(), EnumSet.of(ALLOW_MISSING_TARGET_OBJECT_ON_REPLACE));
        assertThat(result, equalTo(expected));
    }
}