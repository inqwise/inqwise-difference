package com.inqwise.difference;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A utility class that provides methods for comparing {@link JsonNode} objects, 
 * specifically focusing on numeric values to ensure equality across different 
 * numeric formats (e.g., integer and decimal). It also supports comparing arrays 
 * and objects within JSON structures.
 */
public final class JsonNumEquals {

    private static final JsonNumEquals INSTANCE = new JsonNumEquals();

    /**
     * Private constructor to enforce singleton pattern.
     */
    private JsonNumEquals() {
    }

    /**
     * Returns the singleton instance of {@code JsonNumEquals}.
     *
     * @return the singleton instance of {@code JsonNumEquals}.
     */
    public static JsonNumEquals getInstance() {
        return INSTANCE;
    }

    /**
     * Compares two {@link JsonNode} objects for equivalence, with special handling 
     * for numeric nodes. This method ensures that numerically equivalent values 
     * (e.g., "5" and "5.0") are considered equal.
     *
     * @param a the first {@code JsonNode} to compare.
     * @param b the second {@code JsonNode} to compare.
     * @return {@code true} if the nodes are equivalent, {@code false} otherwise.
     */
    protected boolean doEquivalent(final JsonNode a, final JsonNode b) {
        if (a.isNumber() && b.isNumber())
            return numEquals(a, b);

        final NodeType typeA = NodeType.getNodeType(a);
        final NodeType typeB = NodeType.getNodeType(b);

        if (typeA != typeB)
            return false;

        if (!a.isContainerNode())
            return a.equals(b);

        if (a.size() != b.size())
            return false;

        return typeA == NodeType.ARRAY ? arrayEquals(a, b) : objectEquals(a, b);
    }

    /**
     * Calculates the hash code for a {@link JsonNode} object. This method ensures 
     * that equivalent numeric values (e.g., "5" and "5.0") produce the same hash code.
     *
     * @param t the {@code JsonNode} for which to calculate the hash code.
     * @return the hash code of the {@code JsonNode}.
     */
    protected int doHash(final JsonNode t) {
        if (t.isNumber())
            return Double.valueOf(t.doubleValue()).hashCode();

        if (!t.isContainerNode())
            return t.hashCode();

        int ret = 0;

        if (t.size() == 0)
            return ret;

        if (t.isArray()) {
            for (final JsonNode element : t)
                ret = 31 * ret + doHash(element);
            return ret;
        }

        final Iterator<Map.Entry<String, JsonNode>> iterator = t.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterator.next();
            ret = 31 * ret + (entry.getKey().hashCode() ^ doHash(entry.getValue()));
        }

        return ret;
    }

    /**
     * Compares two numeric {@link JsonNode} objects for equality, ensuring that 
     * equivalent numeric values are considered equal (e.g., "5" and "5.0").
     *
     * @param a the first numeric {@code JsonNode}.
     * @param b the second numeric {@code JsonNode}.
     * @return {@code true} if the numeric values are equivalent, {@code false} otherwise.
     */
    private static boolean numEquals(final JsonNode a, final JsonNode b) {
        if (a.isIntegralNumber() && b.isIntegralNumber())
            return a.equals(b);

        return a.decimalValue().compareTo(b.decimalValue()) == 0;
    }

    /**
     * Compares two JSON arrays for equality by checking each element in the array.
     *
     * @param a the first {@code JsonNode} array.
     * @param b the second {@code JsonNode} array.
     * @return {@code true} if the arrays are equivalent, {@code false} otherwise.
     */
    private boolean arrayEquals(final JsonNode a, final JsonNode b) {
        final int size = a.size();
        for (int i = 0; i < size; i++)
            if (!doEquivalent(a.get(i), b.get(i)))
                return false;
        return true;
    }

    /**
     * Compares two JSON objects for equality by checking each field in the object.
     * This method ensures that objects with the same fields and values are considered equal.
     *
     * @param a the first {@code JsonNode} object.
     * @param b the second {@code JsonNode} object.
     * @return {@code true} if the objects are equivalent, {@code false} otherwise.
     */
    private boolean objectEquals(final JsonNode a, final JsonNode b) {
        var aIterator = a.fieldNames();
        final Set<String> keys = Stream.generate(aIterator::next).takeWhile(i -> aIterator.hasNext()).collect(Collectors.toSet());

        var bIterator = b.fieldNames();
        final Set<String> set = Stream.generate(bIterator::next).takeWhile(i -> bIterator.hasNext()).collect(Collectors.toSet());
        if (!set.equals(keys))
            return false;

        for (final String key : keys)
            if (!doEquivalent(a.get(key), b.get(key)))
                return false;

        return true;
    }
}