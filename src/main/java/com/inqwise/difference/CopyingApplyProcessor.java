package com.inqwise.difference;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.EnumSet;

/**
 * A processor that applies differences to a target object by creating a deep copy of the target before modification.
 * This ensures that the original target is not modified in place, making it suitable for scenarios where 
 * immutability or snapshotting of the original state is required.
 * 
 * The processor uses {@link CompatibilityFlags} to control how differences are applied.
 */
class CopyingApplyProcessor extends InPlaceApplyProcessor {

    /**
     * Constructs a {@code CopyingApplyProcessor} with a deep copy of the target object and default compatibility flags.
     *
     * @param target the target {@link JsonNode} to which differences will be applied. A deep copy of this node is created.
     */
    CopyingApplyProcessor(JsonNode target) {
        this(target, CompatibilityFlags.defaults());
    }

    /**
     * Constructs a {@code CopyingApplyProcessor} with a deep copy of the target object and the provided compatibility flags.
     *
     * @param target the target {@link JsonNode} to which differences will be applied. A deep copy of this node is created.
     * @param flags  the {@link CompatibilityFlags} controlling how differences are applied.
     */
    CopyingApplyProcessor(JsonNode target, EnumSet<CompatibilityFlags> flags) {
        super(target.deepCopy(), flags);
    }
}