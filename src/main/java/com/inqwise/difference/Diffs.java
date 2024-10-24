package com.inqwise.difference;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents a collection of {@link Diff} objects, which store differences between two objects.
 * The {@code Diffs} class provides functionality to retrieve these differences as JSON nodes,
 * and allows customization through {@link DiffFlags} and handling of composite objects.
 * It uses a builder pattern for flexible construction.
 */
public class Diffs implements Iterable<Diff> {

    private List<Diff> diffs;
    private EnumSet<DiffFlags> flags;
    private List<String> compositeObjects;

    /**
     * Private constructor that initializes a {@code Diffs} object with the specified builder.
     * @param builder the builder containing the configuration for the {@code Diffs} instance.
     */
    private Diffs(Builder builder) {
        this.diffs = builder.diffs;
        this.flags = builder.flags;
        this.compositeObjects = builder.compositeObjects;
    }

    /**
     * Returns the list of differences as a JSON array.
     * Each difference is converted into a JSON node according to the specified flags.
     * @return an {@link ArrayNode} representing the differences in JSON format.
     */
    public ArrayNode getJsonNodes() {
        JsonNodeFactory FACTORY = JsonNodeFactory.instance;
        final ArrayNode patch = FACTORY.arrayNode();
        for (Diff diff : diffs) {
            ObjectNode jsonNode = getJsonNode(FACTORY, diff, flags);
            patch.add(jsonNode);
        }
        return patch;
    }

    /**
     * Converts a single {@link Diff} object into a JSON node based on the specified flags.
     * @param FACTORY the {@link JsonNodeFactory} used to create JSON nodes.
     * @param diff the {@code Diff} to convert into a JSON node.
     * @param flags the {@link DiffFlags} controlling how the difference is represented.
     * @return an {@link ObjectNode} representing the {@code Diff}.
     */
    private static ObjectNode getJsonNode(JsonNodeFactory FACTORY, Diff diff, EnumSet<DiffFlags> flags) {
        ObjectNode jsonNode = FACTORY.objectNode();
        jsonNode.put(Constants.OP, diff.getOperation().rfcName());

        switch (diff.getOperation()) {
            case MOVE:
            case COPY:
                jsonNode.put(Constants.FROM, diff.getPath().toString());    // required {from} only in case of Move Operation
                jsonNode.put(Constants.PATH, diff.getToPath().toString());  // destination Path
                break;

            case REMOVE:
                jsonNode.put(Constants.PATH, diff.getPath().toString());
                if (!flags.contains(DiffFlags.OMIT_VALUE_ON_REMOVE)) {
                    jsonNode.set(Constants.VALUE, diff.getValue());
                }
                break;

            case REPLACE:
                if (flags.contains(DiffFlags.ADD_ORIGINAL_VALUE_ON_REPLACE)) {
                    jsonNode.set(Constants.FROM_VALUE, diff.getSrcValue());
                }
            case ADD:
            case TEST:
                jsonNode.put(Constants.PATH, diff.getPath().toString());
                jsonNode.set(Constants.VALUE, diff.getValue());
                break;

            default:
                throw new IllegalArgumentException("Unknown operation specified: " + diff.getOperation());
        }

        return jsonNode;
    }

    /**
     * Returns the list of {@link Diff} objects stored in this {@code Diffs} instance.
     * @return the list of differences.
     */
    public List<Diff> getDiffs() {
        return diffs;
    }

    /**
     * Returns the {@link DiffFlags} used in this {@code Diffs} instance.
     * @return the set of flags controlling how differences are computed and represented.
     */
    public EnumSet<DiffFlags> getFlags() {
        return flags;
    }

    /**
     * Returns the list of composite objects that are handled specially in this {@code Diffs} instance.
     * @return the list of composite objects.
     */
    public List<String> getCompositeObjects() {
        return compositeObjects;
    }

    /**
     * Returns an iterator over the {@link Diff} objects in this instance.
     * @return an iterator over the list of differences.
     */
    @Override
    public Iterator<Diff> iterator() {
        return diffs.iterator();
    }

    /**
     * Creates a builder to build a new {@code Diffs} instance.
     * @return a new {@link Builder} instance for constructing a {@code Diffs} object.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a builder to build a {@code Diffs} object initialized with an existing {@code Diffs} instance.
     * @param diffs the existing {@code Diffs} instance to initialize the builder with.
     * @return a new {@link Builder} instance.
     */
    public static Builder builderFrom(Diffs diffs) {
        return new Builder(diffs);
    }

    /**
     * A builder class to construct instances of {@link Diffs}.
     * It allows for configuration of the list of differences, flags, and composite objects before
     * creating the final {@code Diffs} instance.
     */
    public static final class Builder {
        private List<Diff> diffs;
        private EnumSet<DiffFlags> flags;
        private List<String> compositeObjects;

        /**
         * Private constructor for the builder.
         */
        private Builder() {
        }

        /**
         * Private constructor that initializes the builder with an existing {@code Diffs} instance.
         * @param diffs the existing {@code Diffs} object to copy fields from.
         */
        private Builder(Diffs diffs) {
            this.diffs = diffs.diffs;
            this.flags = diffs.flags;
            this.compositeObjects = diffs.compositeObjects;
        }

        /**
         * Sets the list of {@link Diff} objects for the {@code Diffs} instance being built.
         * @param diffs the list of differences.
         * @return the current {@code Builder} instance for method chaining.
         */
        public Builder withDiffs(List<Diff> diffs) {
            this.diffs = diffs;
            return this;
        }

        /**
         * Sets the {@link DiffFlags} for the {@code Diffs} instance being built.
         * @param flags the set of flags controlling how differences are computed and represented.
         * @return the current {@code Builder} instance for method chaining.
         */
        public Builder withFlags(EnumSet<DiffFlags> flags) {
            this.flags = flags;
            return this;
        }

        /**
         * Sets the list of composite objects for the {@code Diffs} instance being built.
         * Composite objects require special handling during difference computation.
         * @param compositeObjects the list of composite objects.
         * @return the current {@code Builder} instance for method chaining.
         */
        public Builder withCompositeObjects(List<String> compositeObjects) {
            this.compositeObjects = compositeObjects;
            return this;
        }

        /**
         * Builds and returns a new {@link Diffs} instance with the configured settings.
         * @return a new {@code Diffs} instance.
         */
        public Diffs build() {
            return new Diffs(this);
        }
    }
}