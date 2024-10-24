package com.inqwise.difference;

import java.util.EnumSet;

/**
 * Enum representing various flags that control the behavior of the difference calculation process.
 * These flags can be used to customize how differences between objects are computed, allowing for 
 * more fine-grained control over operations such as removing values, handling move and copy operations, 
 * and more.
 */
public enum DiffFlags {

    /**
     * Flag to omit the value in "remove" operations. When set, the value that is being removed 
     * is not included in the difference output.
     */
    OMIT_VALUE_ON_REMOVE,

    /**
     * Flag to omit "move" operations. When set, the move operation will not be used, 
     * and the changes will be represented using alternative operations.
     */
    OMIT_MOVE_OPERATION,

    /**
     * Flag to omit "copy" operations. When set, the copy operation will not be used, 
     * and the changes will be represented using alternative operations.
     */
    OMIT_COPY_OPERATION,
    
    /**
     * Flag to omit composite array operations. When set, operations on composite arrays 
     * are not included in the difference output.
     */
    OMIT_COMPOSITE_ARRAY,

    /**
     * Flag to include the original value during a "replace" operation. 
     * When set, the difference output will contain the original value that is being replaced.
     */
    ADD_ORIGINAL_VALUE_ON_REPLACE,

    /**
     * Flag to emit "test" operations. When set, test operations will be included in the 
     * difference output to verify conditions before applying changes.
     */
    EMIT_TEST_OPERATIONS;

    /**
     * Returns the default set of flags to be used during difference calculation.
     * By default, {@link #OMIT_VALUE_ON_REMOVE} is included to omit the value from "remove" operations.
     *
     * @return an EnumSet of default flags.
     */
    public static EnumSet<DiffFlags> defaults() {
        return EnumSet.of(OMIT_VALUE_ON_REMOVE);
    }

    /**
     * Returns a set of flags that omits normalization of operations into move and copy.
     * This set includes {@link #OMIT_MOVE_OPERATION} and {@link #OMIT_COPY_OPERATION}.
     *
     * @return an EnumSet that disables move and copy operations.
     */
    public static EnumSet<DiffFlags> dontNormalizeOpIntoMoveAndCopy() {
        return EnumSet.of(OMIT_MOVE_OPERATION, OMIT_COPY_OPERATION);
    }
}