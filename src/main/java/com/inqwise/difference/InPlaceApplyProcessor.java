package com.inqwise.difference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.EnumSet;

/**
 * A class that processes JSON patch operations in place, modifying the target {@link JsonNode}.
 * This class applies operations such as add, remove, replace, move, and copy directly to the target node
 * without creating a deep copy, thereby modifying the original JSON structure.
 * It also supports compatibility flags to customize behavior during patch application.
 */
class InPlaceApplyProcessor implements JsonPatchProcessor {

    private JsonNode target;
    private EnumSet<CompatibilityFlags> flags;

    /**
     * Constructor that initializes the processor with the target JSON node and default compatibility flags.
     * 
     * @param target the target {@link JsonNode} to apply the patch operations to.
     */
    InPlaceApplyProcessor(JsonNode target) {
        this(target, CompatibilityFlags.defaults());
    }

    /**
     * Constructor that initializes the processor with the target JSON node and the provided compatibility flags.
     * 
     * @param target the target {@link JsonNode} to apply the patch operations to.
     * @param flags  the {@link CompatibilityFlags} to control patch application behavior.
     */
    InPlaceApplyProcessor(JsonNode target, EnumSet<CompatibilityFlags> flags) {
        this.target = target;
        this.flags = flags;
    }

    /**
     * Returns the modified result after applying all the patch operations.
     * 
     * @return the modified {@link JsonNode}.
     */
    public JsonNode result() {
        return target;
    }

    @Override
    public void move(JsonPointer fromPath, JsonPointer toPath) throws JsonPointerEvaluationException {
        JsonNode valueNode = fromPath.evaluate(target);
        remove(fromPath);
        set(toPath, valueNode, Operation.MOVE);
    }

    @Override
    public void copy(JsonPointer fromPath, JsonPointer toPath) throws JsonPointerEvaluationException {
        JsonNode valueNode = fromPath.evaluate(target);
        JsonNode valueToCopy = valueNode != null ? valueNode.deepCopy() : null;
        set(toPath, valueToCopy, Operation.COPY);
    }

    /**
     * Helper method to format a {@link JsonNode} value for logging or exception messages.
     * 
     * @param value the {@code JsonNode} value to format.
     * @return a string representation of the node, showing its type or value.
     */
    private static String show(JsonNode value) {
        if (value == null || value.isNull())
            return "null";
        else if (value.isArray())
            return "array";
        else if (value.isObject())
            return "object";
        else
            return "value " + value.toString(); // Caveat: numeric may differ from source (e.g. trailing zeros)
    }

    @Override
    public void test(JsonPointer path, JsonNode value) throws JsonPointerEvaluationException {
        JsonNode valueNode = path.evaluate(target);
        
        boolean isEquals;
        if (valueNode.isNumber() && value.isNumber()) {
            isEquals = JsonNumEquals.getInstance().doEquivalent(valueNode, value);
        } else {
            isEquals = valueNode.equals(value);
        }
        
        if (!isEquals) {
            throw new JsonPatchApplicationException(
                "Expected " + show(value) + " but found " + show(valueNode), Operation.TEST, path
            );
        }
    }

    @Override
    public void add(JsonPointer path, JsonNode value) throws JsonPointerEvaluationException {
        set(path, value, Operation.ADD);
    }

    @Override
    public void replace(JsonPointer path, JsonNode value) throws JsonPointerEvaluationException {
        if (path.isRoot()) {
            target = value;
            return;
        }

        JsonNode parentNode = path.getParent().evaluate(target);
        JsonPointer.RefToken token = path.last();
        if (parentNode.isObject()) {
            if (!flags.contains(CompatibilityFlags.ALLOW_MISSING_TARGET_OBJECT_ON_REPLACE) &&
                    !parentNode.has(token.getField())) {
                throw new JsonPatchApplicationException(
                    "Missing field \"" + token.getField() + "\"", Operation.REPLACE, path.getParent());
            }
            ((ObjectNode) parentNode).replace(token.getField(), value);
        } else if (parentNode.isArray()) {
            if (token.getIndex() >= parentNode.size()) {
                throw new JsonPatchApplicationException(
                    "Array index " + token.getIndex() + " out of bounds", Operation.REPLACE, path.getParent());
            }
            ((ArrayNode) parentNode).set(token.getIndex(), value);
        } else {
            throw new JsonPatchApplicationException(
                "Can't reference past scalar value", Operation.REPLACE, path.getParent());
        }
    }

    @Override
    public void remove(JsonPointer path) throws JsonPointerEvaluationException {
        if (path.isRoot()) {
            throw new JsonPatchApplicationException("Cannot remove document root", Operation.REMOVE, path);
        }

        JsonNode parentNode = path.getParent().evaluate(target);
        JsonPointer.RefToken token = path.last();
        if (parentNode.isObject()) {
            ((ObjectNode) parentNode).remove(token.getField());
        } else if (parentNode.isArray()) {
            if (!flags.contains(CompatibilityFlags.REMOVE_NONE_EXISTING_ARRAY_ELEMENT) &&
                    token.getIndex() >= parentNode.size()) {
                throw new JsonPatchApplicationException(
                    "Array index " + token.getIndex() + " out of bounds", Operation.REMOVE, path.getParent());
            }
            ((ArrayNode) parentNode).remove(token.getIndex());
        } else {
            throw new JsonPatchApplicationException(
                "Cannot reference past scalar value", Operation.REMOVE, path.getParent());
        }
    }

    /**
     * Sets a value at the specified path in the target {@link JsonNode}, creating or modifying nodes as needed.
     * 
     * @param path   the {@link JsonPointer} indicating where to set the value.
     * @param value  the value to set.
     * @param forOp  the {@link Operation} being performed (for logging or exception purposes).
     * @throws JsonPointerEvaluationException if the path cannot be evaluated.
     */
    private void set(JsonPointer path, JsonNode value, Operation forOp) throws JsonPointerEvaluationException {
        if (path.isRoot()) {
            target = value;
        } else {
            JsonNode parentNode = path.getParent().evaluate(target);
            if (!parentNode.isContainerNode()) {
                throw new JsonPatchApplicationException("Cannot reference past scalar value", forOp, path.getParent());
            } else if (parentNode.isArray()) {
                addToArray(path, value, parentNode);
            } else {
                addToObject(path, parentNode, value);
            }
        }
    }

    /**
     * Adds a value to an object node at the specified path.
     * 
     * @param path   the {@link JsonPointer} specifying the key to add.
     * @param node   the target object node to modify.
     * @param value  the value to add.
     */
    private void addToObject(JsonPointer path, JsonNode node, JsonNode value) {
        final ObjectNode target = (ObjectNode) node;
        String key = path.last().getField();
        target.set(key, value);
    }

    /**
     * Adds a value to an array node at the specified path.
     * 
     * @param path        the {@link JsonPointer} specifying the index to add at.
     * @param value       the value to add to the array.
     * @param parentNode  the target array node.
     */
    private void addToArray(JsonPointer path, JsonNode value, JsonNode parentNode) {
        final ArrayNode target = (ArrayNode) parentNode;
        int idx = path.last().getIndex();

        if (idx == JsonPointer.LAST_INDEX) {
            target.add(value);
        } else {
            if (idx > target.size()) {
                throw new JsonPatchApplicationException(
                    "Array index " + idx + " out of bounds", Operation.ADD, path.getParent());
            }
            target.insert(idx, value);
        }
    }
}