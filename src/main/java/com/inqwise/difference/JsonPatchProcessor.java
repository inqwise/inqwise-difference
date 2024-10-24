package com.inqwise.difference;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Interface representing a processor that applies JSON Patch operations on a JSON document.
 * Implementations of this interface define how to handle various JSON Patch operations such as
 * add, remove, replace, move, copy, and test.
 */
interface JsonPatchProcessor {

    /**
     * Removes the value at the specified path in the JSON document.
     *
     * @param path the {@link JsonPointer} representing the location of the value to remove.
     * @throws JsonPointerEvaluationException if there is an error evaluating the path.
     */
    void remove(JsonPointer path) throws JsonPointerEvaluationException;

    /**
     * Replaces the value at the specified path in the JSON document with the provided value.
     *
     * @param path the {@link JsonPointer} representing the location of the value to replace.
     * @param value the new {@link JsonNode} value to set at the specified path.
     * @throws JsonPointerEvaluationException if there is an error evaluating the path.
     */
    void replace(JsonPointer path, JsonNode value) throws JsonPointerEvaluationException;

    /**
     * Adds the specified value at the given path in the JSON document.
     *
     * @param path the {@link JsonPointer} representing the location to add the value.
     * @param value the {@link JsonNode} value to add.
     * @throws JsonPointerEvaluationException if there is an error evaluating the path.
     */
    void add(JsonPointer path, JsonNode value) throws JsonPointerEvaluationException;

    /**
     * Moves the value from one path to another in the JSON document.
     *
     * @param fromPath the {@link JsonPointer} representing the source location of the value.
     * @param toPath the {@link JsonPointer} representing the destination location for the value.
     * @throws JsonPointerEvaluationException if there is an error evaluating the paths.
     */
    void move(JsonPointer fromPath, JsonPointer toPath) throws JsonPointerEvaluationException;

    /**
     * Copies the value from one path to another in the JSON document.
     *
     * @param fromPath the {@link JsonPointer} representing the source location of the value.
     * @param toPath the {@link JsonPointer} representing the destination location for the value.
     * @throws JsonPointerEvaluationException if there is an error evaluating the paths.
     */
    void copy(JsonPointer fromPath, JsonPointer toPath) throws JsonPointerEvaluationException;

    /**
     * Tests whether the value at the specified path matches the provided value.
     *
     * @param path the {@link JsonPointer} representing the location of the value to test.
     * @param value the expected {@link JsonNode} value to compare against.
     * @throws JsonPointerEvaluationException if the test fails or if there is an error evaluating the path.
     */
    void test(JsonPointer path, JsonNode value) throws JsonPointerEvaluationException;
}