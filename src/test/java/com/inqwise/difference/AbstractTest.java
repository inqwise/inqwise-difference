package com.inqwise.difference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.stream.Stream;

import org.apache.logging.log4j.core.util.StringBuilderWriter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractTest {

    protected PatchTestCase currentTestCase;

    // MethodSource to provide test cases
    static Stream<PatchTestCase> testCases() throws Exception {
        // You can override this in each subclass to load different test cases
        return loadTestCases("default").stream();
    }

    // Override this method in subclasses to specify the test case source
    protected static Collection<PatchTestCase> loadTestCases(String operation) throws Exception {
        return PatchTestCase.load(operation);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    public void test(PatchTestCase p) throws Exception {
        this.currentTestCase = p;
        if (p.isOperation()) {
            testOperation(p);
        } else {
            testError(p);
        }
    }

    private void testOperation(PatchTestCase p) throws Exception {
        JsonNode node = p.getNode();

        JsonNode doc = node.get("node");
        JsonNode expected = node.get("expected");
        JsonNode patch = node.get("op");
        String message = node.has("message") ? node.get("message").toString() : "";

        JsonNode result = JsonPatch.apply(patch, doc);
        String failMessage = "The following test failed: \n" +
                "message: " + message + '\n' +
                "at: " + p.getSourceFile();
        assertEquals(expected, result, failMessage);
    }

    private Class<?> exceptionType(String type) throws ClassNotFoundException {
        String packageName = this.getClass().getPackage().getName();
        return Class.forName(type.contains(".") ? type : packageName + "." + type);
    }

    private String errorMessage(PatchTestCase p, String header) throws JsonProcessingException {
        return errorMessage(p, header, null);
    }

    private String errorMessage(PatchTestCase p, String header, Exception e) throws JsonProcessingException {
        StringBuilder res = new StringBuilder()
                .append(header)
                .append("\nFull test case (in file ")
                .append(p.getSourceFile())
                .append("):\n")
                .append(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(p.getNode()));
        if (e != null) {
            res.append("\nFull error: ");
            e.printStackTrace(new PrintWriter(new StringBuilderWriter(res)));
        }
        return res.toString();
    }

    private void testError(PatchTestCase p) throws JsonProcessingException, ClassNotFoundException {
        JsonNode node = p.getNode();
        JsonNode first = node.get("node");
        JsonNode patch = node.get("op");
        JsonNode message = node.get("message");
        Class<?> type = node.has("type") ? exceptionType(node.get("type").textValue()) : JsonPatchApplicationException.class;

        try {
            JsonPatch.apply(patch, first);
            fail(errorMessage(p, "Failure expected: " + message));
        } catch (Exception e) {
            if (matchOnErrors()) {
                StringWriter fullError = new StringWriter();
                e.printStackTrace(new PrintWriter(fullError));

                assertThat(
                        errorMessage(p, "Operation failed but with wrong exception type", e),
                        e,
                        instanceOf(type));
                if (message != null) {
                    assertThat(
                            errorMessage(p, "Operation failed but with wrong message", e),
                            e.toString(),
                            containsString(message.textValue()));    // equalTo would be better, but fail existing tests
                }
            }
        }
    }

    protected boolean matchOnErrors() {
        return true;
    }
}