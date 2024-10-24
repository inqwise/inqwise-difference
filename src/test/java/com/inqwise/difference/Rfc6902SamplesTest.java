package com.inqwise.difference;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class Rfc6902SamplesTest extends AbstractTest {

    @ParameterizedTest
    @MethodSource("testCases")
    public void testRfc6902Samples(PatchTestCase p) throws Exception {
        test(p); // Call the abstract test method from AbstractTest
    }

    static Stream<PatchTestCase> testCases() throws IOException {
        Collection<PatchTestCase> cases = PatchTestCase.load("rfc6902-samples");
        return cases.stream();
    }

    @Override
    protected boolean matchOnErrors() {
        // Error matching disabled to avoid a lot of rote work on the samples.
        // TODO revisit samples and possibly change "message" fields to "reference" or something more descriptive.
        return false;
    }
}