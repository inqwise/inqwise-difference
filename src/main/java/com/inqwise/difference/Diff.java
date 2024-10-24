package com.inqwise.difference;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents a single difference between two objects during a comparison process.
 * Each {@code Diff} contains an operation (e.g., add, remove, replace), a path indicating where 
 * the difference occurs, and optionally, values or source values depending on the operation.
 * This class supports various difference operations and is used to generate differences 
 * that can be applied to objects.
 */
class Diff {
    private final Operation operation;
    private final JsonPointer path;
    private final JsonNode value;
    private JsonPointer toPath; // Only used in move operation.
    private final JsonNode srcValue; // Only used in replace operation.

    /**
     * Constructs a {@code Diff} for operations that require a path and value, such as add or replace.
     *
     * @param operation the type of operation (add, remove, replace, etc.).
     * @param path      the path where the difference occurs.
     * @param value     the value associated with the difference.
     */
    Diff(Operation operation, JsonPointer path, JsonNode value) {
        this.operation = operation;
        this.path = path;
        this.value = value;
        this.srcValue = null;
    }

    /**
     * Constructs a {@code Diff} for move or copy operations, which require both a source and destination path.
     *
     * @param operation the type of operation (move, copy).
     * @param fromPath  the source path.
     * @param toPath    the destination path.
     */
    Diff(Operation operation, JsonPointer fromPath, JsonPointer toPath) {
        this.operation = operation;
        this.path = fromPath;
        this.toPath = toPath;
        this.value = null;
        this.srcValue = null;
    }

    /**
     * Constructs a {@code Diff} for replace operations, which require both a source and target value.
     *
     * @param operation the type of operation (replace).
     * @param path      the path where the difference occurs.
     * @param srcValue  the original value before replacement.
     * @param value     the new value after replacement.
     */
    Diff(Operation operation, JsonPointer path, JsonNode srcValue, JsonNode value) {
        this.operation = operation;
        this.path = path;
        this.value = value;
        this.srcValue = srcValue;
    }

    /**
     * Returns the operation associated with this difference.
     *
     * @return the operation type.
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * Returns the path where this difference occurs.
     *
     * @return the JSON pointer path.
     */
    public JsonPointer getPath() {
        return path;
    }

    /**
     * Returns the value associated with this difference, if applicable.
     *
     * @return the value of the difference.
     */
    public JsonNode getValue() {
        return value;
    }

    /**
     * Generates a {@code Diff} for replace operations with a path and target value.
     *
     * @param replace the replace operation type.
     * @param path    the path where the replace operation occurs.
     * @param target  the new value to replace with.
     * @return a new {@code Diff} representing the replace operation.
     */
    public static Diff generateDiff(Operation replace, JsonPointer path, JsonNode target) {
        return new Diff(replace, path, target);
    }

    /**
     * Generates a {@code Diff} for replace operations with a path, source value, and target value.
     *
     * @param replace the replace operation type.
     * @param path    the path where the replace operation occurs.
     * @param source  the original value before replacement.
     * @param target  the new value to replace with.
     * @return a new {@code Diff} representing the replace operation.
     */
    public static Diff generateDiff(Operation replace, JsonPointer path, JsonNode source, JsonNode target) {
        return new Diff(replace, path, source, target);
    }

    /**
     * Returns the destination path, used only for move or copy operations.
     *
     * @return the destination path.
     */
    JsonPointer getToPath() {
        return toPath;
    }

    /**
     * Returns the source value, used only for replace operations.
     *
     * @return the original value before replacement.
     */
    public JsonNode getSrcValue() {
        return srcValue;
    }
}