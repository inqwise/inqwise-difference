package com.inqwise.difference;

/**
 * Exception thrown when an invalid JSON patch is encountered.
 * This class extends {@link JsonPatchApplicationException} and represents an error that occurs
 * specifically due to the invalid structure or content of a JSON patch operation.
 */
public class InvalidJsonPatchException extends JsonPatchApplicationException {

    private static final long serialVersionUID = 4202006211384166418L;

	/**
     * Constructs a new {@code InvalidJsonPatchException} with the specified error message.
     *
     * @param message the detail message explaining the reason for the exception.
     */
    public InvalidJsonPatchException(String message) {
        super(message, null, null);
    }
}