package com.inqwise.difference;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;

public class AddOperationTest extends AbstractTest {

    // Provide test cases for "add" operation
    static Stream<PatchTestCase> testCases() throws IOException {
        return loadTestCases("add").stream();
    }

    // Override method to ensure test cases are correctly loaded
    protected static Collection<PatchTestCase> loadTestCases(String operation) throws IOException {
        return PatchTestCase.load(operation);
    }
}