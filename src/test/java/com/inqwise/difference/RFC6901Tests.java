package com.inqwise.difference;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

class RFC6901Tests {

    @Test
    void testRFC6901Compliance() throws IOException {
        // Load the test data from the specified JSON file
        JsonNode rfcData = TestUtils.loadResourceAsJsonNode("/rfc6901/data.json");
        
        // Validate that the data was successfully loaded
        if (rfcData == null) {
            throw new IOException("Failed to load RFC 6901 test data from /rfc6901/data.json");
        }

        // Extract the "testData" node from the loaded data
        JsonNode testData = rfcData.get("testData");

        // Create an empty JSON object
        ObjectNode emptyJson = TestUtils.DEFAULT_MAPPER.createObjectNode();

        // Generate a JSON diff (patch) to transform emptyJson into testData
        JsonNode patch = JsonDiff.asJson(emptyJson, testData);

        // Apply the generated patch to the empty JSON object
        JsonNode result = JsonPatch.apply(patch, emptyJson);

        // Assert that the transformed JSON matches the expected testData
        assertEquals(testData, result, "The JSON patch application did not produce the expected result.");
    }
}