package com.inqwise.difference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A utility class containing internal helper methods for working with JSON data structures and sequences.
 * These methods are used to manipulate and compare {@link JsonNode} lists, including converting arrays
 * and finding the longest common subsequence between two lists.
 */
class InternalUtils {

    /**
     * Converts an {@link ArrayNode} into a {@link List} of {@link JsonNode} elements.
     * Each element in the input array is added to the resulting list in the same order.
     *
     * @param input the {@link ArrayNode} to convert.
     * @return a {@link List} of {@link JsonNode} elements.
     */
    static List<JsonNode> toList(ArrayNode input) {
        int size = input.size();
        List<JsonNode> toReturn = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            toReturn.add(input.get(i));
        }
        return toReturn;
    }

    /**
     * Computes the longest common subsequence (LCS) between two lists of {@link JsonNode}.
     * The LCS is the longest sequence that appears in both lists in the same order, though not necessarily contiguously.
     *
     * @param a the first list of {@link JsonNode}.
     * @param b the second list of {@link JsonNode}.
     * @return a {@link List} of {@link JsonNode} representing the longest common subsequence.
     * @throws NullPointerException if either of the input lists is null.
     */
    static List<JsonNode> longestCommonSubsequence(final List<JsonNode> a, final List<JsonNode> b) {
        if (a == null || b == null) {
            throw new NullPointerException("List must not be null for longestCommonSubsequence");
        }

        List<JsonNode> toReturn = new LinkedList<>();

        int aSize = a.size();
        int bSize = b.size();
        int temp[][] = new int[aSize + 1][bSize + 1];

        // Build the LCS length table
        for (int i = 1; i <= aSize; i++) {
            for (int j = 1; j <= bSize; j++) {
                if (i == 0 || j == 0) {
                    temp[i][j] = 0;
                } else if (a.get(i - 1).equals(b.get(j - 1))) {
                    temp[i][j] = temp[i - 1][j - 1] + 1;
                } else {
                    temp[i][j] = Math.max(temp[i][j - 1], temp[i - 1][j]);
                }
            }
        }

        // Backtrack to find the LCS
        int i = aSize, j = bSize;
        while (i > 0 && j > 0) {
            if (a.get(i - 1).equals(b.get(j - 1))) {
                toReturn.add(a.get(i - 1));
                i--;
                j--;
            } else if (temp[i - 1][j] > temp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        // Reverse the list to get the correct order
        Collections.reverse(toReturn);
        return toReturn;
    }
}