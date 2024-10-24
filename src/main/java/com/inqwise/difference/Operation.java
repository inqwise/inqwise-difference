package com.inqwise.difference;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing the possible operations in a JSON Patch as defined by 
 * <a href="https://tools.ietf.org/html/rfc6902">RFC 6902</a>.
 * 
 * <p>The supported operations are:
 * <ul>
 *   <li>ADD - Adds a value at the specified location.</li>
 *   <li>REMOVE - Removes the value at the specified location.</li>
 *   <li>REPLACE - Replaces the value at the specified location.</li>
 *   <li>MOVE - Moves the value from one location to another.</li>
 *   <li>COPY - Copies the value from one location to another.</li>
 *   <li>TEST - Tests if a value at the specified location matches the provided value.</li>
 * </ul>
 * 
 * <p>This enum also provides methods to get the operation from its RFC name
 * and to retrieve the RFC-compliant name of an operation.</p>
 */
enum Operation {
    /** Represents the "add" operation in JSON Patch. */
    ADD("add"),
    /** Represents the "remove" operation in JSON Patch. */
    REMOVE("remove"),
    /** Represents the "replace" operation in JSON Patch. */
    REPLACE("replace"),
    /** Represents the "move" operation in JSON Patch. */
    MOVE("move"),
    /** Represents the "copy" operation in JSON Patch. */
    COPY("copy"),
    /** Represents the "test" operation in JSON Patch. */
    TEST("test");

    /** A map to associate RFC names with their corresponding {@link Operation} enum. */
    private final static Map<String, Operation> OPS = createImmutableMap();

    /**
     * The RFC-compliant name of the operation.
     */
    private String rfcName;

    /**
     * Constructs a new {@code Operation} with the specified RFC-compliant name.
     *
     * @param rfcName The RFC-compliant name of the operation.
     */
    Operation(String rfcName) {
        this.rfcName = rfcName;
    }

    /**
     * Creates an immutable map associating RFC names with their corresponding {@link Operation}.
     *
     * @return An unmodifiable map associating RFC names with {@link Operation}.
     */
    private static Map<String, Operation> createImmutableMap() {
        Map<String, Operation> map = new HashMap<>();
        map.put(ADD.rfcName, ADD);
        map.put(REMOVE.rfcName, REMOVE);
        map.put(REPLACE.rfcName, REPLACE);
        map.put(MOVE.rfcName, MOVE);
        map.put(COPY.rfcName, COPY);
        map.put(TEST.rfcName, TEST);
        return Collections.unmodifiableMap(map);
    }

    /**
     * Returns the {@code Operation} associated with the specified RFC name.
     *
     * @param rfcName The RFC-compliant name of the operation.
     * @return The corresponding {@link Operation}.
     * @throws InvalidJsonPatchException If the RFC name is null or unsupported.
     */
    public static Operation fromRfcName(String rfcName) throws InvalidJsonPatchException {
        if (rfcName == null) throw new InvalidJsonPatchException("rfcName cannot be null");
        Operation op = OPS.get(rfcName.toLowerCase());
        if (op == null) throw new InvalidJsonPatchException("unknown / unsupported operation " + rfcName);
        return op;
    }

    /**
     * Returns the RFC-compliant name of this operation.
     *
     * @return The RFC-compliant name of the operation.
     */
    public String rfcName() {
        return this.rfcName;
    }
}