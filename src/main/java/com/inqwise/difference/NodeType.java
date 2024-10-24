package com.inqwise.difference;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.EnumMap;
import java.util.Map;

/**
 * Represents different types of JSON nodes (such as array, boolean, string, etc.)
 * according to their corresponding JSON tokens. This enum is used to categorize and
 * differentiate between node types in a JSON structure.
 */
enum NodeType {
    /** Represents an array node in a JSON document. */
    ARRAY("array"),
    /** Represents a boolean node (true/false) in a JSON document. */
    BOOLEAN("boolean"),
    /** Represents an integer node in a JSON document. */
    INTEGER("integer"),
    /** Represents a null node in a JSON document. */
    NULL("null"),
    /** Represents a numeric (decimal) node in a JSON document. */
    NUMBER("number"),
    /** Represents an object node in a JSON document. */
    OBJECT("object"),
    /** Represents a string node in a JSON document. */
    STRING("string");

    /** The string representation of this node type, used for display or in JSON schema. */
    private final String name;

    /** A map that associates JSON tokens with their corresponding {@link NodeType}. */
    private static final Map<JsonToken, NodeType> TOKEN_MAP = new EnumMap<>(JsonToken.class);

    static {
        TOKEN_MAP.put(JsonToken.START_ARRAY, ARRAY);
        TOKEN_MAP.put(JsonToken.VALUE_TRUE, BOOLEAN);
        TOKEN_MAP.put(JsonToken.VALUE_FALSE, BOOLEAN);
        TOKEN_MAP.put(JsonToken.VALUE_NUMBER_INT, INTEGER);
        TOKEN_MAP.put(JsonToken.VALUE_NUMBER_FLOAT, NUMBER);
        TOKEN_MAP.put(JsonToken.VALUE_NULL, NULL);
        TOKEN_MAP.put(JsonToken.START_OBJECT, OBJECT);
        TOKEN_MAP.put(JsonToken.VALUE_STRING, STRING);
    }

    /**
     * Constructs a new {@code NodeType} with the specified name.
     *
     * @param name The name representing this type.
     */
    NodeType(final String name) {
        this.name = name;
    }

    /**
     * Returns the string representation of this {@code NodeType}.
     *
     * @return the name of this node type.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Determines the {@code NodeType} of a given {@link JsonNode}.
     *
     * @param node The {@link JsonNode} whose type is to be determined.
     * @return The {@link NodeType} corresponding to the given node.
     * @throws NullPointerException if the token type of the node is not handled.
     */
    public static NodeType getNodeType(final JsonNode node) {
        final JsonToken token = node.asToken();
        final NodeType ret = TOKEN_MAP.get(token);
        if (ret == null) throw new NullPointerException("Unhandled token type: " + token);
        return ret;
    }
}