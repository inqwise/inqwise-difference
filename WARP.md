# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

This is a Java library that provides efficient and flexible comparison of Java objects and generation of JSON Patches (RFC 6902). The library specializes in computing differences between two Java objects and creating structured representations using Jackson for JSON processing.

**Key Features:**
- JSON Patch operations (add, remove, replace, move, copy, test)
- Silent fields (ignored during comparison)
- Composite fields (special handling for complex objects)
- Integration with Jackson JSON library
- RFC 6902 compliance

## Architecture

### Core Components

**Main API Classes:**
- `Differences` - Main API class for computing and managing differences between objects
- `Differentiator` - Builder-pattern facade for customizing comparison behavior
- `JsonDiff` - Core diffing engine that generates JSON Patch operations
- `JsonPatch` - Utility for applying and validating JSON Patch operations

**Supporting Classes:**
- `Operation` - Enum for RFC 6902 operations (ADD, REMOVE, REPLACE, MOVE, COPY, TEST)
- `DiffFlags` - Configuration flags controlling diff behavior 
- `JsonPointer` - RFC 6901 JSON Pointer implementation for path navigation
- `Diff` - Internal representation of a single difference operation

**Processing Classes:**
- `CopyingApplyProcessor` - Applies patches to copies of source data
- `InPlaceApplyProcessor` - Applies patches directly to source data
- `NoopProcessor` - Validation-only processor

### Key Design Patterns

1. **Builder Pattern**: `Differentiator.Builder` for flexible configuration
2. **Strategy Pattern**: Different processors for different patch application strategies  
3. **Factory Pattern**: Static methods in `JsonDiff` and `Differences` for object creation
4. **Visitor Pattern**: Processing of JSON Patch operations through processor interfaces

### JSON Patch Flow

1. Objects are converted to JsonNodes using Jackson
2. Silent fields are removed from comparison
3. `JsonDiff` compares nodes and generates list of `Diff` objects
4. Operations are optimized (MOVE/COPY introduction if enabled)
5. Results are converted to RFC 6902 compliant JSON Patch format

## Development Commands

### Building and Testing
```bash
# Clean and compile
mvn clean compile

# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=JsonDiffTest

# Run specific test method
mvn test -Dtest=JsonDiffTest#testSampleJsonDiff

# Package the library
mvn package

# Install to local repository
mvn install

# Generate Javadocs
mvn javadoc:javadoc
```

### Code Quality
```bash
# Run tests with coverage (if configured)
mvn test

# Clean generated files
mvn clean

# Run security scans locally (requires Snyk CLI)
./scripts/snyk-local.sh
```

### Release Management
```bash
# Generate sources and javadoc JARs
mvn source:jar javadoc:jar

# Release to repository (with profile)
mvn clean deploy -P sonatype-oss-release
```

## Technical Specifications

- **Java Version**: 21 (specified in pom.xml)
- **Maven Version**: 3.6.3+ required
- **Key Dependencies**:
  - Jackson 2.17.2 for JSON processing
  - Vert.x 5.0.4 (provided scope)
  - Guava 33.0.0-jre for utilities
  - JUnit 5.10.1 for testing
  - Apache Commons Collections 4.4

## Common Usage Patterns

### Basic Object Comparison
```java
Object obj1 = /* first object */;
Object obj2 = /* second object */;
Differences diffs = Differences.between(obj1, obj2, null);
```

### With Silent Fields
```java
List<String> silentFields = Arrays.asList("timestamp", "id");
Differences diffs = Differences.between(obj1, obj2, silentFields);
```

### Using Differentiator Builder
```java
Differentiator differ = Differentiator.builder()
    .withSilentFields(Arrays.asList("id", "timestamp"))
    .withCompositeFields(Arrays.asList("address"))
    .build();
Differences diffs = differ.between(obj1, obj2);
```

### Applying Patches
```java
// Apply differences to transform obj1 into obj2
Object result = differences.applyTo(obj1);
```

## Test Data Organization

- **Test Resources**: Located in `src/test/resources/testdata/`
- **RFC 6901 Tests**: JSON Pointer specification compliance tests
- **RFC 6902 Tests**: JSON Patch specification compliance tests
- **Generated Tests**: `TestDataGenerator` creates random test data for robustness testing

## Important Implementation Notes

- All patch operations are RFC 6902 compliant
- Silent fields support wildcard patterns (`**`) for recursive matching
- Composite fields are treated as atomic units during comparison
- The library supports both copying and in-place patch application
- Jackson modules are automatically registered if available on classpath
- Vert.x integration is provided but dependencies are marked as provided scope

## Performance Considerations

- Uses Longest Common Subsequence (LCS) algorithm for efficient array comparisons
- Supports composite object optimization to reduce diff complexity
- Provides flags to control operation normalization (MOVE/COPY vs ADD/REMOVE)
- Offers in-place processing to minimize memory usage when appropriate

## Library Integration

This library is designed as a low-level utility for other systems. It integrates well with:
- JSON processing pipelines
- Object versioning systems  
- Configuration management tools
- API change tracking systems
- Database migration tools

When extending this library, focus on the processor pattern for new patch application strategies and the flags system for new diff behaviors.