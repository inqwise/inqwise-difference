package com.inqwise.difference;

/**
 * A utility class that defines constant keys used in the difference computation process.
 * These constants are typically used for identifying various JSON fields when performing
 * operations such as adding, removing, or replacing values in an object.
 * 
 * This class is final and has a private constructor to prevent instantiation.
 */
final class Constants {

    /**
     * Key representing the operation type (e.g., add, remove, replace) in JSON-based difference operations.
     */
    public static final String OP = "op";

    /**
     * Key representing the value in a difference operation.
     */
    public static final String VALUE = "value";

    /**
     * Key representing the path to the element being modified in a difference operation.
     */
    public static final String PATH = "path";

    /**
     * Key representing the source path in operations like move or copy.
     */
    public static final String FROM = "from";

    /**
     * Key representing the original value before a change, typically used in replace operations.
     */
    public static final String FROM_VALUE = "fromValue";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Constants() {}
}