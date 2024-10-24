
package com.inqwise.difference;

import java.util.EnumSet;

/**
 * Enum representing various flags that control compatibility behavior during difference calculation.
 * These flags influence how the differences between objects are handled, allowing customization of 
 * specific behaviors, such as treating missing values as nulls or allowing missing target objects 
 * during a replace operation.
 */
public enum CompatibilityFlags {
    
    /**
     * Flag indicating that missing values should be treated as nulls during the comparison.
     */
    MISSING_VALUES_AS_NULLS,
    
    /**
     * Flag indicating that elements in arrays that do not exist in the target should be removed.
     */
    REMOVE_NONE_EXISTING_ARRAY_ELEMENT,
    
    /**
     * Flag allowing the replacement of objects even when the target object is missing.
     */
    ALLOW_MISSING_TARGET_OBJECT_ON_REPLACE;

    /**
     * Provides a default set of compatibility flags, which in this case is an empty set.
     *
     * @return an empty EnumSet of CompatibilityFlags.
     */
    public static EnumSet<CompatibilityFlags> defaults() {
        return EnumSet.noneOf(CompatibilityFlags.class);
    }
}
