package com.inqwise.difference;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * A class that facilitates comparison between two objects while allowing the user to specify 
 * certain fields to be ignored (silent fields) or to be treated as composite fields during the comparison.
 * The class utilizes a builder pattern to allow for flexible construction and supports object 
 * comparison by delegating to the {@link Differences} class.
 */
public class Differentiator {
    
    private List<String> silentFields;
    private List<String> compositeFields;

    /**
     * Private constructor that initializes the Differentiator with silent and composite fields.
     * @param builder the builder object containing the configuration for the Differentiator.
     */
    private Differentiator(Builder builder) {
        this.silentFields = builder.silentFields;
        this.compositeFields = builder.compositeFields;
    }

    /**
     * Returns a new {@link Differentiator} object with the updated silent fields.
     * @param silentFields the list of fields to be ignored during comparison.
     * @return a new {@code Differentiator} with the updated silent fields.
     */
    public Differentiator silentFields(List<String> silentFields) {
        return builderFrom(this).withSilentFields(silentFields).build();
    }

    /**
     * Returns a new {@link Differentiator} object with the updated composite fields.
     * @param compositeFields the list of composite fields that require special handling.
     * @return a new {@code Differentiator} with the updated composite fields.
     */
    public Differentiator compositeFields(List<String> compositeFields) {
        return builderFrom(this).withCompositeFields(compositeFields).build();
    }

    /**
     * Compares two objects and returns the {@link Differences} between them, 
     * taking into account the configured silent and composite fields.
     * @param obj1 the first object to compare.
     * @param obj2 the second object to compare.
     * @return the {@code Differences} between the two objects.
     */
    public Differences between(Object obj1, Object obj2) {
        return Differences.between(obj1, obj2, silentFields, compositeFields);
    }

    /**
     * Creates a builder to build a new {@link Differentiator}.
     * @return a new {@link Builder} instance to create a Differentiator.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a builder to build a {@link Differentiator} initialized with an existing Differentiator's fields.
     * @param differentiator an existing Differentiator instance to initialize the builder.
     * @return a new {@link Builder} instance initialized with the given Differentiator's fields.
     */
    public static Builder builderFrom(Differentiator differentiator) {
        return new Builder(differentiator);
    }

    /**
     * A builder class to construct instances of {@link Differentiator}.
     * It allows for configuration of silent and composite fields before building the final Differentiator instance.
     */
    public static final class Builder {
        private List<String> silentFields;
        private List<String> compositeFields;

        /**
         * Private constructor for the builder.
         */
        private Builder() {
        }

        /**
         * Private constructor that initializes the builder with an existing Differentiator.
         * @param differentiator the Differentiator to copy fields from.
         */
        private Builder(Differentiator differentiator) {
            this.silentFields = differentiator.silentFields;
            this.compositeFields = differentiator.compositeFields;
        }

        /**
         * Sets the silent fields to be used during the comparison.
         * @param silentFields a list of fields to be ignored.
         * @return the current {@code Builder} instance for method chaining.
         */
        public Builder withSilentFields(@Nonnull List<String> silentFields) {
            this.silentFields = silentFields;
            return this;
        }

        /**
         * Sets the composite fields that require special handling during the comparison.
         * @param compositeFields a list of composite fields.
         * @return the current {@code Builder} instance for method chaining.
         */
        public Builder withCompositeFields(@Nonnull List<String> compositeFields) {
            this.compositeFields = compositeFields;
            return this;
        }

        /**
         * Builds and returns a new {@link Differentiator} instance with the configured fields.
         * @return a new {@link Differentiator} instance.
         */
        public Differentiator build() {
            return new Differentiator(this);
        }
    }
}