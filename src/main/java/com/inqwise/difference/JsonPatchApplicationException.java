package com.inqwise.difference;

/**
 * A custom exception class used to handle errors during the application of a JSON Patch.
 * This exception is thrown when an issue arises while processing a JSON Patch operation 
 * (e.g., add, remove, replace, etc.). It contains details about the operation that failed 
 * and the specific path where the failure occurred.
 */
public class JsonPatchApplicationException extends RuntimeException {
    private static final long serialVersionUID = 8890857792476475382L;
	private Operation operation;
    private JsonPointer path;

    /**
     * Constructs a new {@code JsonPatchApplicationException} with a detailed message, 
     * the operation that failed, and the path where the failure occurred.
     *
     * @param message the detail message explaining the reason for the exception.
     * @param operation the JSON Patch operation that caused the exception.
     * @param path the {@link JsonPointer} representing the path in the JSON document where the failure occurred.
     */
    public JsonPatchApplicationException(String message, Operation operation, JsonPointer path) {
        super(message);
        this.operation = operation;
        this.path = path;
    }

    /**
     * Provides a string representation of the exception, including the failed operation, 
     * the failure message, and the path where the failure occurred.
     *
     * @return a string representation of the exception.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (operation != null) sb.append('[').append(operation).append(" Operation] ");
        sb.append(getMessage());
        if (path != null) sb.append(" at ").append(path.isRoot() ? "root" : path);
        return sb.toString();
    }
}