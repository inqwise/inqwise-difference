package com.inqwise.difference;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import io.vertx.core.json.JsonObject;

/**
 * The {@code Differences} class represents a collection of differences between two JSON objects.
 * It provides functionalities to compute the differences, parse them, and apply them to a target object.
 * This class leverages JSON Patch operations as defined in <a href="https://tools.ietf.org/html/rfc6902">RFC 6902</a>.
 */
public class Differences implements Iterable<Differences.Difference> {
    private static final Logger logger = LogManager.getLogger(Differences.class);

    /** The key used for serialization or identification purposes. */
    public static final String Key = "diff";
    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        var modules = ObjectMapper.findModules();
        if (modules != null && modules.size() > 0) {
            mapper.registerModules(modules);
        }
    }

    /** An {@link ObjectReader} for reading a list of {@link Difference} objects from JSON. */
    private static final ObjectReader listOfDifferencesReader = mapper.readerFor(new TypeReference<List<Difference>>() {});

    /** The list of {@link Difference} objects representing the differences. */
    private List<Difference> list;

    /**
     * Enum representing the JSON Patch operations.
     */
    public enum Operation {
        /** The "add" operation. */
        add,
        /** The "remove" operation. */
        remove,
        /** The "replace" operation. */
        replace,
        /** The "move" operation. */
        move,
        /** The "copy" operation. */
        copy,
        /** The "test" operation. */
        test
    }

    /**
     * Represents a single difference between two JSON nodes.
     */
    public static class Difference {
        private Operation operation;
        private String path;
        private Object value;
        private Object fromValue;
        private String from;

        /**
         * Gets the JSON Patch operation of this difference.
         *
         * @return The operation.
         */
        @JsonGetter(value = "op")
        public Operation getOperation() {
            return operation;
        }

        /**
         * Sets the JSON Patch operation of this difference.
         *
         * @param operation The operation to set.
         */
        @JsonSetter(value = "op")
        public void setOperation(Operation operation) {
            this.operation = operation;
        }

        /**
         * Gets the JSON Pointer path where the operation applies.
         *
         * @return The path.
         */
        public String getPath() {
            return path;
        }

        /**
         * Sets the JSON Pointer path where the operation applies.
         *
         * @param path The path to set.
         */
        public void setPath(String path) {
            this.path = path;
        }

        /**
         * Gets the value involved in the operation.
         *
         * @return The value.
         */
        public Object getValue() {
            return value;
        }

        /**
         * Sets the value involved in the operation.
         *
         * @param value The value to set.
         */
        public void setValue(Object value) {
            this.value = value;
        }

        /**
         * Gets the original value before the operation, used in replace operations.
         *
         * @return The original value.
         */
        public Object getFromValue() {
            return fromValue;
        }

        /**
         * Sets the original value before the operation.
         *
         * @param fromValue The original value to set.
         */
        public void setFromValue(Object fromValue) {
            this.fromValue = fromValue;
        }

        /**
         * Gets the source path for move or copy operations.
         *
         * @return The source path.
         */
        public String getFrom() {
            return from;
        }

        /**
         * Sets the source path for move or copy operations.
         *
         * @param from The source path to set.
         */
        public void setFrom(String from) {
            this.from = from;
        }

        /**
         * Returns a string representation of the difference.
         *
         * @return A string representation.
         */
        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("op", operation)
                    .add("path", path)
                    .toString();
        }
    }

    /**
     * Default constructor for deserialization purposes.
     */
    public Differences() {
        new JsonMapper();
    }

    /**
     * Constructs a {@code Differences} object with a given list of differences.
     *
     * @param list The list of differences.
     * @throws NullPointerException if the list is null.
     */
    public Differences(List<Difference> list) {
        this.list = Objects.requireNonNull(list, "Difference list cannot be null");
    }

    /**
     * Constructs a {@code Differences} object from a JSON Patch node.
     *
     * @param patch The JSON Patch node.
     * @throws IllegalArgumentException if the patch cannot be read.
     */
    public Differences(JsonNode patch) {
        JsonPatch.validate(patch);
        try {
            list = listOfDifferencesReader.readValue(patch);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Failed to read patch: '%s'", patch), e);
        }
    }

    /**
     * Parses a string into a {@code Differences} object.
     *
     * @param str The string to parse.
     * @return The {@code Differences} object.
     * @throws IllegalArgumentException if parsing fails.
     */
    public static Differences parse(String str) {
        try {
            return new Differences(listOfDifferencesReader.readTree(str));
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to parse the string into differences", e);
        }
    }

    /**
     * Computes the differences between two objects, excluding specified silent fields.
     *
     * @param obj1         The first object.
     * @param obj2         The second object.
     * @param silentFields The fields to exclude from the comparison.
     * @return The {@code Differences} object representing the differences.
     */
    public static Differences between(Object obj1, Object obj2, List<String> silentFields) {
        return between(obj1, obj2, silentFields, null);
    }

    /**
     * Computes the differences between two objects, excluding silent fields and handling composite fields.
     *
     * @param obj1            The first object.
     * @param obj2            The second object.
     * @param silentFields    The fields to exclude from the comparison.
     * @param compositeFields The composite fields to handle specially.
     * @return The {@code Differences} object representing the differences.
     */
    public static Differences between(Object obj1, Object obj2, List<String> silentFields, List<String> compositeFields) {
        logger.trace("between({}, {}, {}, {})", obj1, obj2, silentFields, compositeFields);

        JsonNode node1 = convertObjectToJsonNode(obj1);
        JsonNode node2 = convertObjectToJsonNode(obj2);

        if (silentFields != null) {
            silentFields.forEach(field -> removeSilentFields(field, node1, node2));
        }

        EnumSet<DiffFlags> flags = EnumSet.of(
                DiffFlags.ADD_ORIGINAL_VALUE_ON_REPLACE,
                DiffFlags.OMIT_MOVE_OPERATION,
                DiffFlags.OMIT_COPY_OPERATION,
                DiffFlags.OMIT_COMPOSITE_ARRAY
        );
        JsonNode diff = JsonDiff.asJson(node1, node2, flags, transformCompositeFields(compositeFields));

        return new Differences(diff);
    }

    /**
     * Removes specified fields from the given JSON nodes.
     *
     * @param field The field to remove.
     * @param nodes The JSON nodes from which to remove the field.
     */
    private static void removeSilentFields(String field, JsonNode... nodes) {
        logger.debug("removeSilentFields({}, {})", field, nodes);
        String[] fieldTokens = field.split("\\.");

        for (JsonNode node : nodes) {
            if (node instanceof ObjectNode) {
                removeField(fieldTokens, (ContainerNode<?>) node, false);
            }
        }
    }

    /**
     * Converts an object to a {@link JsonNode}.
     *
     * @param obj The object to convert.
     * @return The resulting {@link JsonNode}.
     * @throws IllegalArgumentException if conversion fails.
     */
    private static JsonNode convertObjectToJsonNode(Object obj) {
        try {
            if (obj instanceof JsonObject) {
                return mapper.readTree(obj.toString());
            } else {
                return mapper.valueToTree(obj);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting object to JsonNode", e);
        }
    }

    /**
     * Normalizes a field string into a JSON Pointer format.
     *
     * @param field The field string.
     * @return The normalized field string.
     */
    private static String normalizeField(String field) {
        return field.startsWith("/") ? field : "/" + field.replace(".", "/");
    }

    /**
     * Transforms a list of composite fields into JSON Pointer format.
     *
     * @param compositeFields The list of composite fields.
     * @return A list of normalized fields.
     */
    private static List<String> transformCompositeFields(List<String> compositeFields) {
        return ListUtils.emptyIfNull(compositeFields).stream().map(Differences::normalizeField).toList();
    }

    /**
     * Recursively removes a field from a container node, supporting wildcards.
     *
     * @param tokens   The field tokens representing the path.
     * @param node     The container node.
     * @param wildcard Whether wildcard matching is enabled.
     */
    private static void removeField(String[] tokens, ContainerNode<?> node, boolean wildcard) {
        logger.trace("removeField({}, {}, {})", Arrays.toString(tokens), node, wildcard);
        if (tokens.length == 0 || node == null) {
            return; // Base case: no tokens to process or node is null
        }

        String fieldName = tokens[0];

        if ("**".equals(fieldName)) {
            // If the field name is a wildcard '**', proceed with the remaining tokens and enable wildcard matching
            removeField(Arrays.copyOfRange(tokens, 1, tokens.length), node, true);
        } else {
            logger.trace("Processing fieldName: '{}', tokens left: {}, wildcard: {}", fieldName, tokens.length, wildcard);

            if (tokens.length == 1 && node.has(fieldName)) {
                logger.trace("Removing field '{}' from node: {}", fieldName, node);
                if (node instanceof ObjectNode) {
                    ((ObjectNode) node).remove(fieldName);
                }
            }

            // If node is an array and wildcard is enabled, process each element
            if (node.isArray() && wildcard) {
                node.elements().forEachRemaining(element -> {
                    if (element.isContainerNode()) {
                        logger.trace("Element is a ContainerNode: {}", element);
                        removeField(tokens, (ContainerNode<?>) element, wildcard);
                    }
                });
            } else if (node.isObject()) {
                // If node is an object, iterate through its fields
                node.fields().forEachRemaining(entry -> {
                    JsonNode childNode = entry.getValue();
                    String key = entry.getKey();

                    if (childNode.isContainerNode()) {
                        logger.trace("Child node at key '{}' is a ContainerNode: {}", key, childNode);
                        if (key.equals(fieldName)) {
                            logger.trace("Key '{}' matches fieldName '{}'. Recursing into child node.", key, fieldName);
                            // Field matches; recurse with the remaining tokens
                            removeField(Arrays.copyOfRange(tokens, 1, tokens.length), (ContainerNode<?>) childNode, wildcard);
                        } else if (wildcard) {
                            // Wildcard is enabled; recurse into child node without consuming tokens
                            removeField(tokens, (ContainerNode<?>) childNode, wildcard);
                        }
                    }
                });
            }
        }
    }

    /**
     * Returns an iterator over the differences.
     *
     * @return An iterator.
     */
    @Override
    public Iterator<Difference> iterator() {
        return list.iterator();
    }

    /**
     * Checks if there are no differences.
     *
     * @return {@code true} if there are no differences, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Returns the number of differences.
     *
     * @return The size of the differences list.
     */
    public int size() {
        return list.size();
    }

    /**
     * Returns a sequential {@code Stream} of the differences.
     *
     * @return A stream of differences.
     */
    public Stream<Difference> stream() {
        return list.stream();
    }

    /**
     * Applies the differences to the given object and returns the resulting object.
     *
     * @param <T> The type of the object.
     * @param obj The object to which the differences are applied.
     * @return The object after applying the differences.
     * @throws IllegalArgumentException if the application fails.
     */
    @SuppressWarnings("unchecked")
    public <T> T applyTo(T obj) {
        logger.trace("applyTo({})", obj);
        Preconditions.checkNotNull(obj, "Target object cannot be null");

        JsonNode source = convertObjectToJsonNode(obj);
        JsonNode patch = mapper.valueToTree(list);

        JsonNode target = JsonPatch.apply(patch, source);
        try {
            return (T) mapper.treeToValue(target, obj.getClass());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to apply patch", e);
        }
    }

    /**
     * Returns a JSON string representation of the differences.
     *
     * @return A JSON string.
     */
    @Override
    public String toString() {
        return Optional.ofNullable(mapper.valueToTree(list)).map(Object::toString).orElse(null);
    }
}