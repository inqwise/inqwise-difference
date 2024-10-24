package com.inqwise.difference;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * A no-operation (noop) JSON patch processor that performs no actions.
 * This class is primarily used for testing and validation purposes, where 
 * the intention is to simulate patch processing without modifying any data.
 * 
 * <p>This implementation provides empty method bodies for all operations,
 * including add, remove, replace, move, copy, and test.</p>
 */
public class NoopProcessor implements JsonPatchProcessor {

    /** Singleton instance of {@link NoopProcessor}. */
    static final NoopProcessor INSTANCE;

    static {
        INSTANCE = new NoopProcessor();
    }

    /**
     * Does nothing for the remove operation.
     *
     * @param path The path where the remove operation is supposed to happen.
     */
    @Override public void remove(JsonPointer path) {}

    /**
     * Does nothing for the replace operation.
     *
     * @param path The path where the replace operation is supposed to happen.
     * @param value The value that is supposed to replace the existing value at the path.
     */
    @Override public void replace(JsonPointer path, JsonNode value) {}

    /**
     * Does nothing for the add operation.
     *
     * @param path The path where the add operation is supposed to happen.
     * @param value The value that is supposed to be added at the path.
     */
    @Override public void add(JsonPointer path, JsonNode value) {}

    /**
     * Does nothing for the move operation.
     *
     * @param fromPath The path from which the value is supposed to be moved.
     * @param toPath The path to which the value is supposed to be moved.
     */
    @Override public void move(JsonPointer fromPath, JsonPointer toPath) {}

    /**
     * Does nothing for the copy operation.
     *
     * @param fromPath The path from which the value is supposed to be copied.
     * @param toPath The path to which the value is supposed to be copied.
     */
    @Override public void copy(JsonPointer fromPath, JsonPointer toPath) {}

    /**
     * Does nothing for the test operation.
     *
     * @param path The path where the value is supposed to be tested.
     * @param value The value that is supposed to be tested at the path.
     */
    @Override public void test(JsonPointer path, JsonNode value) {}
}