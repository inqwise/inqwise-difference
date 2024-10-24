package com.inqwise.difference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

import java.util.EnumSet;
import java.util.Iterator;

/**
 * A utility class that applies and validates JSON Patch operations according to 
 * the RFC 6902 standard. The class supports common JSON Patch operations such as 
 * add, remove, replace, move, copy, and test. It also supports compatibility flags 
 * to modify the behavior of patch operations.
 */
public final class JsonPatch {

    /**
     * Private constructor to enforce the static nature of this utility class.
     */
    private JsonPatch() {
    }

    /**
     * Retrieves a required attribute from a JSON patch node.
     *
     * @param jsonNode the JSON node representing the patch operation.
     * @param attr the name of the required attribute.
     * @return the child {@link JsonNode} representing the attribute.
     * @throws InvalidJsonPatchException if the attribute is missing.
     */
    private static JsonNode getPatchAttr(JsonNode jsonNode, String attr) {
        JsonNode child = jsonNode.get(attr);
        if (child == null)
            throw new InvalidJsonPatchException("Invalid JSON Patch payload (missing '" + attr + "' field)");
        return child;
    }

    /**
     * Retrieves an optional attribute from a JSON patch node, or a default value if the attribute is missing.
     *
     * @param jsonNode the JSON node representing the patch operation.
     * @param attr the name of the attribute.
     * @param defaultValue the default {@link JsonNode} to return if the attribute is missing.
     * @return the child {@link JsonNode} or the default value if the attribute is missing.
     */
    private static JsonNode getPatchAttrWithDefault(JsonNode jsonNode, String attr, JsonNode defaultValue) {
        JsonNode child = jsonNode.get(attr);
        return (child == null) ? defaultValue : child;
    }

    /**
     * Processes a JSON patch, applying the operations using a provided {@link JsonPatchProcessor}.
     *
     * @param patch the JSON patch to process.
     * @param processor the {@link JsonPatchProcessor} used to apply the patch operations.
     * @param flags the {@link CompatibilityFlags} to control patch behavior.
     * @throws InvalidJsonPatchException if the patch format is invalid.
     */
    private static void process(JsonNode patch, JsonPatchProcessor processor, EnumSet<CompatibilityFlags> flags)
            throws InvalidJsonPatchException {

        if (!patch.isArray())
            throw new InvalidJsonPatchException("Invalid JSON Patch payload (not an array)");
        Iterator<JsonNode> operations = patch.iterator();
        while (operations.hasNext()) {
            JsonNode jsonNode = operations.next();
            if (!jsonNode.isObject()) throw new InvalidJsonPatchException("Invalid JSON Patch payload (not an object)");
            Operation operation = Operation.fromRfcName(getPatchAttr(jsonNode, Constants.OP).textValue());
            JsonPointer path = JsonPointer.parse(getPatchAttr(jsonNode, Constants.PATH).textValue());

            try {
                switch (operation) {
                    case REMOVE:
                        processor.remove(path);
                        break;
                    case ADD:
                    case REPLACE:
                    case TEST:
                        JsonNode value = flags.contains(CompatibilityFlags.MISSING_VALUES_AS_NULLS)
                            ? getPatchAttrWithDefault(jsonNode, Constants.VALUE, NullNode.getInstance())
                            : getPatchAttr(jsonNode, Constants.VALUE);
                        if (operation == Operation.ADD) processor.add(path, value.deepCopy());
                        if (operation == Operation.REPLACE) processor.replace(path, value.deepCopy());
                        if (operation == Operation.TEST) processor.test(path, value.deepCopy());
                        break;
                    case MOVE:
                        JsonPointer fromPath = JsonPointer.parse(getPatchAttr(jsonNode, Constants.FROM).textValue());
                        processor.move(fromPath, path);
                        break;
                    case COPY:
                        JsonPointer fromCopyPath = JsonPointer.parse(getPatchAttr(jsonNode, Constants.FROM).textValue());
                        processor.copy(fromCopyPath, path);
                        break;
                }
            } catch (JsonPointerEvaluationException e) {
                throw new JsonPatchApplicationException(e.getMessage(), operation, e.getPath());
            }
        }
    }

    /**
     * Validates the structure and format of a JSON patch without applying it.
     *
     * @param patch the JSON patch to validate.
     * @param flags the {@link CompatibilityFlags} to control validation behavior.
     * @throws InvalidJsonPatchException if the patch is invalid.
     */
    public static void validate(JsonNode patch, EnumSet<CompatibilityFlags> flags) throws InvalidJsonPatchException {
        process(patch, NoopProcessor.INSTANCE, flags);
    }

    /**
     * Validates the structure and format of a JSON patch without applying it.
     *
     * @param patch the JSON patch to validate.
     * @throws InvalidJsonPatchException if the patch is invalid.
     */
    public static void validate(JsonNode patch) throws InvalidJsonPatchException {
        validate(patch, CompatibilityFlags.defaults());
    }

    /**
     * Applies a JSON patch to the provided source JSON node and returns a new JSON node.
     * This method creates a deep copy of the source before applying the patch.
     *
     * @param patch the JSON patch to apply.
     * @param source the source JSON node to patch.
     * @param flags the {@link CompatibilityFlags} to control patch behavior.
     * @return the resulting patched {@link JsonNode}.
     * @throws JsonPatchApplicationException if the patch application fails.
     */
    public static JsonNode apply(JsonNode patch, JsonNode source, EnumSet<CompatibilityFlags> flags) throws JsonPatchApplicationException {
        CopyingApplyProcessor processor = new CopyingApplyProcessor(source, flags);
        process(patch, processor, flags);
        return processor.result();
    }

    /**
     * Applies a JSON patch to the provided source JSON node and returns a new JSON node.
     *
     * @param patch the JSON patch to apply.
     * @param source the source JSON node to patch.
     * @return the resulting patched {@link JsonNode}.
     * @throws JsonPatchApplicationException if the patch application fails.
     */
    public static JsonNode apply(JsonNode patch, JsonNode source) throws JsonPatchApplicationException {
        return apply(patch, source, CompatibilityFlags.defaults());
    }

    /**
     * Applies a JSON patch to the provided source JSON node in place, modifying the original node.
     *
     * @param patch the JSON patch to apply.
     * @param source the source JSON node to patch.
     */
    public static void applyInPlace(JsonNode patch, JsonNode source) {
        applyInPlace(patch, source, CompatibilityFlags.defaults());
    }

    /**
     * Applies a JSON patch to the provided source JSON node in place, modifying the original node.
     *
     * @param patch the JSON patch to apply.
     * @param source the source JSON node to patch.
     * @param flags the {@link CompatibilityFlags} to control patch behavior.
     */
    public static void applyInPlace(JsonNode patch, JsonNode source, EnumSet<CompatibilityFlags> flags) {
        InPlaceApplyProcessor processor = new InPlaceApplyProcessor(source, flags);
        process(patch, processor, flags);
    }
}