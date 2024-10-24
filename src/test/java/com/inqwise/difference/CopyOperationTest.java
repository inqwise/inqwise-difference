package com.inqwise.difference;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class CopyOperationTest extends AbstractTest {

    @ParameterizedTest
    @MethodSource("testCases")
    public void testCopyOperation(PatchTestCase p) throws Exception {
        test(p); // Calling the abstract test method from AbstractTest
    }

    static Stream<PatchTestCase> testCases() throws IOException {
        Collection<PatchTestCase> cases = PatchTestCase.load("copy");
        return cases.stream();
    }
}