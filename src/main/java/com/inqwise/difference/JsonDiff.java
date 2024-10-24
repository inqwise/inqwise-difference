package com.inqwise.difference;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The {@code JsonDiff} class is responsible for generating a JSON Patch (RFC 6902)
 * that represents the differences between two JSON documents. It provides methods
 * to compare two JSON nodes and produce a list of operations (add, remove, replace,
 * move, copy, test) that transform the source JSON into the target JSON.
 */
public final class JsonDiff {

    /** The set of flags controlling the behavior of the diff algorithm. */
    private final EnumSet<DiffFlags> flags;

    /** The set of JSON Pointers representing composite objects in the JSON structure. */
    private final Set<JsonPointer> compositeObjects;

    /** The set of effective composite objects encountered during comparison. */
    private final Set<JsonPointer> effectiveCompositeObjects;

    /**
     * Private constructor to initialize the {@code JsonDiff} instance with specific flags
     * and composite objects.
     *
     * @param flags            The set of flags to control the diff behavior.
     * @param compositeObjects The collection of JSON Pointers representing composite objects.
     */
    private JsonDiff(EnumSet<DiffFlags> flags, Collection<JsonPointer> compositeObjects) {
        this.flags = flags.clone();
        this.compositeObjects = Set.copyOf(compositeObjects);
        this.effectiveCompositeObjects = new HashSet<>();
    }

    /**
     * Generates a JSON Patch representing the differences between the source and target JSON nodes.
     *
     * @param source The source JSON node.
     * @param target The target JSON node.
     * @return A {@link JsonNode} representing the JSON Patch.
     */
    public static JsonNode asJson(final JsonNode source, final JsonNode target) {
        return asJson(source, target, DiffFlags.defaults());
    }

    /**
     * Generates a JSON Patch representing the differences between the source and target JSON nodes,
     * using the specified diff flags.
     *
     * @param source The source JSON node.
     * @param target The target JSON node.
     * @param flags  The set of {@link DiffFlags} to control the diff behavior.
     * @return A {@link JsonNode} representing the JSON Patch.
     */
    public static JsonNode asJson(final JsonNode source, final JsonNode target, EnumSet<DiffFlags> flags) {
        return asJson(source, target, flags, List.of());
    }

    /**
     * Generates a JSON Patch representing the differences between the source and target JSON nodes,
     * using the specified composite objects.
     *
     * @param source           The source JSON node.
     * @param target           The target JSON node.
     * @param compositeObjects A collection of strings representing composite object paths.
     * @return A {@link JsonNode} representing the JSON Patch.
     */
    public static JsonNode asJson(final JsonNode source, final JsonNode target, Collection<String> compositeObjects) {
        return asJson(source, target, DiffFlags.defaults(), compositeObjects);
    }

    /**
     * Generates a JSON Patch representing the differences between the source and target JSON nodes,
     * using the specified diff flags and composite objects.
     *
     * @param source           The source JSON node.
     * @param target           The target JSON node.
     * @param flags            The set of {@link DiffFlags} to control the diff behavior.
     * @param compositeObjects A collection of strings representing composite object paths.
     * @return A {@link JsonNode} representing the JSON Patch.
     */
    public static JsonNode asJson(final JsonNode source, final JsonNode target, EnumSet<DiffFlags> flags, Collection<String> compositeObjects) {
        return compare(source, target, flags, compositeObjects).getJsonNodes();
    }

    /**
     * Compares the source and target JSON nodes and returns a {@link Diffs} object containing the differences.
     *
     * @param source           The source JSON node.
     * @param target           The target JSON node.
     * @param flags            The set of {@link DiffFlags} to control the diff behavior.
     * @param compositeObjects A collection of strings representing composite object paths.
     * @return A {@link Diffs} object containing the list of differences.
     */
    public static Diffs compare(final JsonNode source, final JsonNode target, EnumSet<DiffFlags> flags, Collection<String> compositeObjects) {
        return new JsonDiff(flags, compositeObjects.stream().map(JsonPointer::parse).collect(Collectors.toList()))
                .compare(source, target);
    }

    /**
     * Retrieves the matching value path from the map of unchanged values.
     *
     * @param unchangedValues The map of unchanged values and their corresponding paths.
     * @param value           The value to find in the map.
     * @return The {@link JsonPointer} path where the value is located, or {@code null} if not found.
     */
    private static JsonPointer getMatchingValuePath(Map<JsonNode, JsonPointer> unchangedValues, JsonNode value) {
        return unchangedValues.get(value);
    }

    /**
     * Introduces COPY operations in the list of diffs where applicable.
     *
     * @param source The source JSON node.
     * @param target The target JSON node.
     * @param diffs  The list of diffs to modify.
     */
    private void introduceCopyOperation(JsonNode source, JsonNode target, List<Diff> diffs) {
        Map<JsonNode, JsonPointer> unchangedValues = getUnchangedPart(source, target);

        for (int i = 0; i < diffs.size(); i++) {
            Diff diff = diffs.get(i);
            if (Operation.ADD != diff.getOperation()) continue;

            JsonPointer matchingValuePath = getMatchingValuePath(unchangedValues, diff.getValue());
            if (matchingValuePath != null && isAllowed(matchingValuePath, diff.getPath())) {
                // Matching value found; replace add with copy
                if (flags.contains(DiffFlags.EMIT_TEST_OPERATIONS)) {
                    // Prepend test node
                    diffs.add(i, new Diff(Operation.TEST, matchingValuePath, diff.getValue()));
                    i++;
                }
                diffs.set(i, new Diff(Operation.COPY, matchingValuePath, diff.getPath()));
            }
        }
    }

    /**
     * Checks if a string represents a number.
     *
     * @param str The string to check.
     * @return {@code true} if the string represents a number, {@code false} otherwise.
     */
    private static boolean isNumber(String str) {
        int size = str.length();

        for (int i = 0; i < size; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }

        return size > 0;
    }

    /**
     * Determines if a COPY operation is allowed between the source and destination paths.
     *
     * @param source      The source {@link JsonPointer}.
     * @param destination The destination {@link JsonPointer}.
     * @return {@code true} if the operation is allowed, {@code false} otherwise.
     */
    private static boolean isAllowed(JsonPointer source, JsonPointer destination) {
        boolean isSame = source.equals(destination);
        int i = 0;
        int j = 0;

        while (i < source.size() && j < destination.size()) {
            JsonPointer.RefToken srcValue = source.get(i);
            JsonPointer.RefToken dstValue = destination.get(j);
            String srcStr = srcValue.toString();
            String dstStr = dstValue.toString();
            if (isNumber(srcStr) && isNumber(dstStr)) {
                if (srcStr.compareTo(dstStr) > 0) {
                    return false;
                }
            }
            i++;
            j++;
        }
        return !isSame;
    }

    /**
     * Retrieves a map of unchanged parts between the source and target JSON nodes.
     *
     * @param source The source JSON node.
     * @param target The target JSON node.
     * @return A map of unchanged values and their corresponding {@link JsonPointer} paths.
     */
    private static Map<JsonNode, JsonPointer> getUnchangedPart(JsonNode source, JsonNode target) {
        Map<JsonNode, JsonPointer> unchangedValues = new HashMap<>();
        computeUnchangedValues(unchangedValues, JsonPointer.ROOT, source, target);
        return unchangedValues;
    }

    /**
     * Recursively computes unchanged values between source and target JSON nodes and populates the map.
     *
     * @param unchangedValues The map to populate with unchanged values.
     * @param path            The current {@link JsonPointer} path.
     * @param source          The source JSON node.
     * @param target          The target JSON node.
     */
    private static void computeUnchangedValues(Map<JsonNode, JsonPointer> unchangedValues, JsonPointer path, JsonNode source, JsonNode target) {
        if (source.equals(JsonNodeComparator.getInstance(), target)) {
            if (!unchangedValues.containsKey(target)) {
                unchangedValues.put(target, path);
            }
            return;
        }

        final NodeType firstType = NodeType.getNodeType(source);
        final NodeType secondType = NodeType.getNodeType(target);

        if (firstType == secondType) {
            switch (firstType) {
                case OBJECT:
                    computeObject(unchangedValues, path, source, target);
                    break;
                case ARRAY:
                    computeArray(unchangedValues, path, source, target);
                    break;
                default:
                    // Do nothing for other types
            }
        }
    }

    /**
     * Recursively computes unchanged values in an array between source and target JSON nodes.
     *
     * @param unchangedValues The map to populate with unchanged values.
     * @param path            The current {@link JsonPointer} path.
     * @param source          The source JSON array node.
     * @param target          The target JSON array node.
     */
    private static void computeArray(Map<JsonNode, JsonPointer> unchangedValues, JsonPointer path, JsonNode source, JsonNode target) {
        final int size = Math.min(source.size(), target.size());

        for (int i = 0; i < size; i++) {
            JsonPointer currPath = path.append(i);
            computeUnchangedValues(unchangedValues, currPath, source.get(i), target.get(i));
        }
    }

    /**
     * Recursively computes unchanged values in an object between source and target JSON nodes.
     *
     * @param unchangedValues The map to populate with unchanged values.
     * @param path            The current {@link JsonPointer} path.
     * @param source          The source JSON object node.
     * @param target          The target JSON object node.
     */
    private static void computeObject(Map<JsonNode, JsonPointer> unchangedValues, JsonPointer path, JsonNode source, JsonNode target) {
        final Iterator<String> fieldNames = source.fieldNames();
        while (fieldNames.hasNext()) {
            String name = fieldNames.next();
            if (target.has(name)) {
                JsonPointer currPath = path.append(name);
                computeUnchangedValues(unchangedValues, currPath, source.get(name), target.get(name));
            }
        }
    }

    /**
     * Introduces MOVE operations by merging REMOVE and ADD operations with the same value.
     *
     * @param diffs The list of diffs to modify.
     */
    private void introduceMoveOperation(List<Diff> diffs) {
        for (int i = 0; i < diffs.size(); i++) {
            Diff diff1 = diffs.get(i);

            // Only consider REMOVE or ADD operations
            if (!(Operation.REMOVE == diff1.getOperation() || Operation.ADD == diff1.getOperation())) {
                continue;
            }

            for (int j = i + 1; j < diffs.size(); j++) {
                Diff diff2 = diffs.get(j);
                if (!diff1.getValue().equals(JsonNodeComparator.getInstance(), diff2.getValue())) {
                    continue;
                }

                Diff moveDiff = null;
                if (Operation.REMOVE == diff1.getOperation() && Operation.ADD == diff2.getOperation()) {
                    JsonPointer relativePath = computeRelativePath(diff2.getPath(), i + 1, j - 1, diffs);
                    moveDiff = new Diff(Operation.MOVE, diff1.getPath(), relativePath);
                } else if (Operation.ADD == diff1.getOperation() && Operation.REMOVE == diff2.getOperation()) {
                    JsonPointer relativePath = computeRelativePath(diff2.getPath(), i, j - 1, diffs);
                    moveDiff = new Diff(Operation.MOVE, relativePath, diff1.getPath());
                }
                if (moveDiff != null) {
                    diffs.remove(j);
                    diffs.set(i, moveDiff);
                    break;
                }
            }
        }
    }

    /**
     * Computes the relative path for MOVE operations within arrays.
     *
     * @param path     The original {@link JsonPointer} path.
     * @param startIdx The starting index in the diffs list.
     * @param endIdx   The ending index in the diffs list.
     * @param diffs    The list of diffs.
     * @return A new {@link JsonPointer} representing the adjusted path.
     */
    private static JsonPointer computeRelativePath(JsonPointer path, int startIdx, int endIdx, List<Diff> diffs) {
        List<Integer> counters = new ArrayList<>(path.size());
        for (int i = 0; i < path.size(); i++) {
            counters.add(0);
        }

        for (int i = startIdx; i <= endIdx; i++) {
            Diff diff = diffs.get(i);
            // Adjust relative path according to ADD and REMOVE operations
            if (Operation.ADD == diff.getOperation() || Operation.REMOVE == diff.getOperation()) {
                updatePath(path, diff, counters);
            }
        }
        return updatePathWithCounters(counters, path);
    }

    /**
     * Updates the path counters based on the diffs.
     *
     * @param counters The list of counters to update.
     * @param path     The original {@link JsonPointer} path.
     * @return A new {@link JsonPointer} with updated counters.
     */
    private static JsonPointer updatePathWithCounters(List<Integer> counters, JsonPointer path) {
        List<JsonPointer.RefToken> tokens = path.decompose();
        for (int i = 0; i < counters.size(); i++) {
            int value = counters.get(i);
            if (value != 0) {
                int currValue = tokens.get(i).getIndex();
                tokens.set(i, new JsonPointer.RefToken(Integer.toString(currValue + value)));
            }
        }
        return new JsonPointer(tokens);
    }

    /**
     * Updates the counters for the path based on the current diff.
     *
     * @param path     The original {@link JsonPointer} path.
     * @param diff     The current {@link Diff}.
     * @param counters The list of counters to update.
     */
    private static void updatePath(JsonPointer path, Diff diff, List<Integer> counters) {
        // Find the longest common prefix ending at an array
        if (diff.getPath().size() <= path.size()) {
            int idx = -1;
            for (int i = 0; i < diff.getPath().size() - 1; i++) {
                if (diff.getPath().get(i).equals(path.get(i))) {
                    idx = i;
                } else {
                    break;
                }
            }
            if (idx == diff.getPath().size() - 2) {
                if (diff.getPath().get(diff.getPath().size() - 1).isArrayIndex()) {
                    updateCounters(diff, diff.getPath().size() - 1, counters);
                }
            }
        }
    }

    /**
     * Adjusts the counters based on the operation type.
     *
     * @param diff     The current {@link Diff}.
     * @param idx      The index in the path.
     * @param counters The list of counters to update.
     */
    private static void updateCounters(Diff diff, int idx, List<Integer> counters) {
        if (Operation.ADD == diff.getOperation()) {
            counters.set(idx, counters.get(idx) - 1);
        } else if (Operation.REMOVE == diff.getOperation()) {
            counters.set(idx, counters.get(idx) + 1);
        }
    }

    /**
     * Converts the list of diffs into a JSON Patch {@link ArrayNode}.
     *
     * @param diffs The list of diffs.
     * @return An {@link ArrayNode} representing the JSON Patch.
     */
    public ArrayNode getJsonNodes(List<Diff> diffs) {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        final ArrayNode patch = factory.arrayNode();
        for (Diff diff : diffs) {
            ObjectNode jsonNode = getJsonNode(factory, diff, flags);
            patch.add(jsonNode);
        }
        return patch;
    }

    /**
     * Converts a single {@link Diff} into a JSON Patch {@link ObjectNode}.
     *
     * @param factory The {@link JsonNodeFactory} instance.
     * @param diff    The {@link Diff} to convert.
     * @param flags   The set of {@link DiffFlags} controlling the output.
     * @return An {@link ObjectNode} representing the diff.
     */
    private static ObjectNode getJsonNode(JsonNodeFactory factory, Diff diff, EnumSet<DiffFlags> flags) {
        ObjectNode jsonNode = factory.objectNode();
        jsonNode.put(Constants.OP, diff.getOperation().rfcName());

        switch (diff.getOperation()) {
            case MOVE:
            case COPY:
                jsonNode.put(Constants.FROM, diff.getPath().toString());
                jsonNode.put(Constants.PATH, diff.getToPath().toString());
                break;
            case REMOVE:
                jsonNode.put(Constants.PATH, diff.getPath().toString());
                if (!flags.contains(DiffFlags.OMIT_VALUE_ON_REMOVE))
                    jsonNode.set(Constants.VALUE, diff.getValue());
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
     * Compares the source and target JSON nodes and returns a {@link Diffs} object containing the differences.
     *
     * @param source The source JSON node.
     * @param target The target JSON node.
     * @return A {@link Diffs} object containing the differences.
     */
    private Diffs compare(JsonNode source, JsonNode target) {
        List<Diff> diffs = generateDiffs(JsonPointer.ROOT, source, target);

        if (!flags.contains(DiffFlags.OMIT_MOVE_OPERATION))
            introduceMoveOperation(diffs);

        if (!flags.contains(DiffFlags.OMIT_COPY_OPERATION))
            introduceCopyOperation(source, target, diffs);

        return Diffs.builder().withDiffs(diffs)
                .withFlags(flags)
                .withCompositeObjects(compositeObjects.stream()
                        .map(JsonPointer::toString)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Recursively generates diffs between the source and target JSON nodes.
     *
     * @param path   The current {@link JsonPointer} path.
     * @param source The source JSON node.
     * @param target The target JSON node.
     * @return A list of {@link Diff} objects representing the differences.
     */
    private List<Diff> generateDiffs(JsonPointer path, JsonNode source, JsonNode target) {
        List<Diff> diffs = new ArrayList<>();

        final NodeType sourceType = NodeType.getNodeType(source);
        final NodeType targetType = NodeType.getNodeType(target);

        final boolean isCompositeObject = compositeObjects.contains(path)
                || ((sourceType == NodeType.ARRAY && targetType == NodeType.ARRAY)
                && flags.contains(DiffFlags.OMIT_COMPOSITE_ARRAY));

        if (isCompositeObject) effectiveCompositeObjects.add(path);

        if (!source.equals(JsonNodeComparator.getInstance(), target)) {
            if (sourceType == NodeType.ARRAY && targetType == NodeType.ARRAY) {
                if (flags.contains(DiffFlags.OMIT_COMPOSITE_ARRAY)) {
                    effectiveCompositeObjects.add(path);
                }
                compareArray(path, source, target, diffs, isCompositeObject);
            } else if (sourceType == NodeType.OBJECT && targetType == NodeType.OBJECT) {
                compareObjects(path, source, target, diffs, isCompositeObject);
            } else {
                if (flags.contains(DiffFlags.EMIT_TEST_OPERATIONS))
                    diffs.add(new Diff(Operation.TEST, path, source));
                diffs.add(Diff.generateDiff(Operation.REPLACE, path, source, target));
            }
        }

        if (!diffs.isEmpty() && isCompositeObject) {
            return List.of(Diff.generateDiff(Operation.REPLACE, path, source, target));
        }

        return diffs;
    }

    /**
     * Compares two JSON arrays and generates diffs representing the differences.
     *
     * @param path              The current {@link JsonPointer} path.
     * @param source            The source JSON array node.
     * @param target            The target JSON array node.
     * @param diffs             The list of diffs to populate.
     * @param isCompositeObject Whether the current path is a composite object.
     */
    private void compareArray(JsonPointer path, JsonNode source, JsonNode target, List<Diff> diffs, boolean isCompositeObject) {
        List<JsonNode> lcs = getLCS(source, target);
        int srcIdx = 0;
        int targetIdx = 0;
        int lcsIdx = 0;
        int srcSize = source.size();
        int targetSize = target.size();
        int lcsSize = lcs.size();

        int pos = 0;

        while (lcsIdx < lcsSize && (diffs.isEmpty() || !isCompositeObject)) {
            JsonNode lcsNode = lcs.get(lcsIdx);
            JsonNode srcNode = source.get(srcIdx);
            JsonNode targetNode = target.get(targetIdx);

            if (lcsNode.equals(JsonNodeComparator.getInstance(), srcNode) && lcsNode.equals(JsonNodeComparator.getInstance(), targetNode)) {
                srcIdx++;
                targetIdx++;
                lcsIdx++;
                pos++;
            } else {
                if (lcsNode.equals(JsonNodeComparator.getInstance(), srcNode)) {
                    // Addition
                    JsonPointer currPath = path.append(pos);
                    diffs.add(Diff.generateDiff(Operation.ADD, currPath, targetNode));
                    pos++;
                    targetIdx++;
                } else if (lcsNode.equals(JsonNodeComparator.getInstance(), targetNode)) {
                    // Removal
                    JsonPointer currPath = path.append(pos);
                    if (flags.contains(DiffFlags.EMIT_TEST_OPERATIONS))
                        diffs.add(new Diff(Operation.TEST, currPath, srcNode));
                    diffs.add(Diff.generateDiff(Operation.REMOVE, currPath, srcNode));
                    srcIdx++;
                } else {
                    // Both are unequal to lcs node
                    JsonPointer currPath = path.append(pos);
                    diffs.addAll(generateDiffs(currPath, srcNode, targetNode));
                    srcIdx++;
                    targetIdx++;
                    pos++;
                }
            }
        }

        while ((srcIdx < srcSize) && (targetIdx < targetSize) && (diffs.isEmpty() || !isCompositeObject)) {
            JsonNode srcNode = source.get(srcIdx);
            JsonNode targetNode = target.get(targetIdx);
            JsonPointer currPath = path.append(pos);
            diffs.addAll(generateDiffs(currPath, srcNode, targetNode));
            srcIdx++;
            targetIdx++;
            pos++;
        }

        if (diffs.isEmpty() || !isCompositeObject) {
            pos = addRemaining(path, target, pos, targetIdx, targetSize, diffs);
        }

        if (diffs.isEmpty() || !isCompositeObject) {
            removeRemaining(path, pos, srcIdx, srcSize, source, diffs);
        }
    }

    /**
     * Removes remaining nodes from the source JSON array and adds REMOVE operations to the diffs.
     *
     * @param path      The current {@link JsonPointer} path.
     * @param pos       The current position in the array.
     * @param srcIdx    The current index in the source array.
     * @param srcSize   The size of the source array.
     * @param source    The source JSON array node.
     * @param innerDiffs The list of diffs to populate.
     */
    private void removeRemaining(JsonPointer path, int pos, int srcIdx, int srcSize, JsonNode source, List<Diff> innerDiffs) {
        while (srcIdx < srcSize) {
            JsonPointer currPath = path.append(pos);
            if (flags.contains(DiffFlags.EMIT_TEST_OPERATIONS))
                innerDiffs.add(new Diff(Operation.TEST, currPath, source.get(srcIdx)));
            innerDiffs.add(Diff.generateDiff(Operation.REMOVE, currPath, source.get(srcIdx)));
            srcIdx++;
        }
    }

    /**
     * Adds remaining nodes from the target JSON array and adds ADD operations to the diffs.
     *
     * @param path       The current {@link JsonPointer} path.
     * @param target     The target JSON array node.
     * @param pos        The current position in the array.
     * @param targetIdx  The current index in the target array.
     * @param targetSize The size of the target array.
     * @param innerDiffs The list of diffs to populate.
     * @return The updated position in the array.
     */
    private int addRemaining(JsonPointer path, JsonNode target, int pos, int targetIdx, int targetSize, List<Diff> innerDiffs) {
        while (targetIdx < targetSize) {
            JsonNode jsonNode = target.get(targetIdx);
            JsonPointer currPath = path.append(pos);
            innerDiffs.add(Diff.generateDiff(Operation.ADD, currPath, jsonNode.deepCopy()));
            pos++;
            targetIdx++;
        }
        return pos;
    }

    /**
     * Compares two JSON objects and generates diffs representing the differences.
     *
     * @param path              The current {@link JsonPointer} path.
     * @param source            The source JSON object node.
     * @param target            The target JSON object node.
     * @param diffs             The list of diffs to populate.
     * @param isCompositeObject Whether the current path is a composite object.
     */
    private void compareObjects(JsonPointer path, JsonNode source, JsonNode target, List<Diff> diffs, boolean isCompositeObject) {
        Iterator<String> keysFromSrc = source.fieldNames();
        while (keysFromSrc.hasNext() && (diffs.isEmpty() || !isCompositeObject)) {
            String key = keysFromSrc.next();
            if (!target.has(key)) {
                // Remove case
                JsonPointer currPath = path.append(key);
                if (flags.contains(DiffFlags.EMIT_TEST_OPERATIONS))
                    diffs.add(new Diff(Operation.TEST, currPath, source.get(key)));
                diffs.add(Diff.generateDiff(Operation.REMOVE, currPath, source.get(key)));
                continue;
            }
            JsonPointer currPath = path.append(key);
            diffs.addAll(generateDiffs(currPath, source.get(key), target.get(key)));
        }
        Iterator<String> keysFromTarget = target.fieldNames();
        while (keysFromTarget.hasNext() && (diffs.isEmpty() || !isCompositeObject)) {
            String key = keysFromTarget.next();
            if (!source.has(key)) {
                // Add case
                JsonPointer currPath = path.append(key);
                diffs.add(Diff.generateDiff(Operation.ADD, currPath, target.get(key)));
            }
        }
    }

    /**
     * Computes the Longest Common Subsequence (LCS) between two JSON arrays.
     *
     * @param first  The first JSON array node.
     * @param second The second JSON array node.
     * @return A list of {@link JsonNode} representing the LCS.
     */
    private static List<JsonNode> getLCS(final JsonNode first, final JsonNode second) {
        return ListUtils.longestCommonSubsequence(InternalUtils.toList((ArrayNode) first), InternalUtils.toList((ArrayNode) second));
    }
}