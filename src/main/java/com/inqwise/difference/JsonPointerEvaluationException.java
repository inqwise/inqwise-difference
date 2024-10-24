package com.inqwise.difference;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Exception thrown when the evaluation of a JSON Pointer against a JSON document fails.
 * This exception provides details about the path in the JSON document where the failure occurred,
 * as well as the target node that caused the evaluation to fail.
 */
public class JsonPointerEvaluationException extends Exception {
    private static final long serialVersionUID = -5690582864571393271L;
	private final JsonPointer path;
    private final JsonNode target;

    /**
     * Constructs a new {@code JsonPointerEvaluationException} with a specified message, path, and target node.
     *
     * @param message The detail message explaining the reason for the exception.
     * @param path The {@link JsonPointer} where the evaluation failed.
     * @param target The {@link JsonNode} that caused the failure during the evaluation.
     */
    public JsonPointerEvaluationException(String message, JsonPointer path, JsonNode target) {
        super(message);
        this.path = path;
        this.target = target;
    }

    /**
     * Returns the {@link JsonPointer} that caused the evaluation to fail.
     *
     * @return The path in the JSON document where the failure occurred.
     */
    public JsonPointer getPath() {
        return path;
    }

    /**
     * Returns the {@link JsonNode} that was being evaluated when the exception was thrown.
     *
     * @return The target node that caused the failure.
     */
    public JsonNode getTarget() {
        return target;
    }
}