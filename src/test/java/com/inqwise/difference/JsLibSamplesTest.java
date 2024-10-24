package com.inqwise.difference;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class JsLibSamplesTest extends AbstractTest {

    @ParameterizedTest
    @MethodSource("testCases")
    public void testJsLibSamples(PatchTestCase p) throws Exception {
        test(p);  // Call the abstract test method from AbstractTest
    }

    static Stream<PatchTestCase> testCases() throws IOException {
        Collection<PatchTestCase> cases = PatchTestCase.load("js-libs-samples");
        return cases.stream();
    }

    @Override
    protected boolean matchOnErrors() {
        return false; // Override the method to disable error matching for this test class
    }
}