package com.inqwise.difference;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PatchTestCase {

    private final boolean operation;
    private final JsonNode node;
    private final String sourceFile;

    private PatchTestCase(boolean operation, JsonNode node, String sourceFile) {
        this.operation = operation;
        this.node = node;
        this.sourceFile = sourceFile;
    }

    public boolean isOperation() {
        return operation;
    }

    public JsonNode getNode() {
        return node;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public static Collection<PatchTestCase> load(String fileName) throws IOException {
        String path = "/testdata/" + fileName + ".json";
        JsonNode tree = TestUtils.loadResourceAsJsonNode(path);

        if (tree == null) {
            throw new IOException("Failed to load JSON data from: " + path);
        }

        List<PatchTestCase> result = new ArrayList<>();
        result.addAll(extractTestCases(tree, "errors", path, false));
        result.addAll(extractTestCases(tree, "ops", path, true));
        
        return result;
    }

    private static List<PatchTestCase> extractTestCases(JsonNode tree, String nodeName, String path, boolean isOperation) {
        List<PatchTestCase> testCases = new ArrayList<>();
        JsonNode nodeArray = tree.get(nodeName);
        
        if (nodeArray != null) {
            for (var node : nodeArray) {
                if (isEnabled(node)) {
                    testCases.add(new PatchTestCase(isOperation, node, path));
                }
            }
        }
        return testCases;
    }

    private static boolean isEnabled(JsonNode node) {
        JsonNode disabled = node.get("disabled");
        return (disabled == null || !disabled.booleanValue());
    }
}