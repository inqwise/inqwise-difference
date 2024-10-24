package com.inqwise.difference;

import java.util.Comparator;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * A comparator class used to compare two {@link JsonNode} objects.
 * It provides a singleton instance and compares numeric nodes with special handling for equivalent numeric values,
 * while for other types of nodes, it uses standard equality comparison.
 */
public class JsonNodeComparator implements Comparator<JsonNode> {

    private static final JsonNodeComparator INSTANCE = new JsonNodeComparator();

    /**
     * Private constructor to enforce the singleton pattern.
     */
    private JsonNodeComparator() {
    }

    /**
     * Returns the singleton instance of the {@code JsonNodeComparator}.
     *
     * @return the singleton instance of {@code JsonNodeComparator}.
     */
    public static JsonNodeComparator getInstance() {
        return INSTANCE;
    }

    /**
     * Compares two {@link JsonNode} objects. If both nodes are numeric, it uses the
     * {@link JsonNumEquals} class to check for equivalent numeric values.
     * For non-numeric nodes, it checks standard equality.
     *
     * @param o1 the first {@link JsonNode} to compare.
     * @param o2 the second {@link JsonNode} to compare.
     * @return {@code 0} if the nodes are considered equal, otherwise {@code 1}.
     */
    @Override
    public int compare(JsonNode o1, JsonNode o2) {
        if (o1.isNumber() && o2.isNumber()) {
            return JsonNumEquals.getInstance().doEquivalent(o1, o2) ? 0 : 1;
        } else {
            return o1.equals(o2) ? 0 : 1;
        }
    }
}