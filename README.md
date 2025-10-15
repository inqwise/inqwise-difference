# Inqwise Difference - Library for Compare

[![CI](https://github.com/inqwise/inqwise-difference/actions/workflows/ci.yml/badge.svg)](https://github.com/inqwise/inqwise-difference/actions/workflows/ci.yml)
[![Release](https://github.com/inqwise/inqwise-difference/actions/workflows/release.yml/badge.svg)](https://github.com/inqwise/inqwise-difference/actions/workflows/release.yml)
[![CodeQL](https://github.com/inqwise/inqwise-difference/actions/workflows/codeql.yml/badge.svg)](https://github.com/inqwise/inqwise-difference/actions/workflows/codeql.yml)
[![Snyk Security](https://github.com/inqwise/inqwise-difference/actions/workflows/snyk.yml/badge.svg)](https://github.com/inqwise/inqwise-difference/actions/workflows/snyk.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.inqwise.difference/inqwise-difference.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.inqwise.difference%22%20AND%20a:%22inqwise-difference%22)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-21%2B-blue.svg)](https://openjdk.java.net/projects/jdk/21/)
[![RFC 6902](https://img.shields.io/badge/RFC-6902-green.svg)](https://tools.ietf.org/html/rfc6902)

This library provides an efficient and flexible way to compute and represent differences between two Java objects. It allows users to define "silent fields" that should be ignored during the comparison process, and "composite fields" that require more complex handling. The core functionality revolves around creating and managing differences in a structured format using Jackson for JSON processing. The library also supports various operations like add, remove, replace, move, and more. Ideal for projects that require object comparison, change tracking, or patching.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
  - [Maven Dependency](#maven-dependency)
- [Usage](#usage)
- [Examples](#examples)
  - [Comparing Two Objects](#comparing-two-objects)
  - [Ignoring Fields During Comparison](#ignoring-fields-during-comparison)
  - [Handling Composite Fields](#handling-composite-fields)
- [Security](#security)
- [Contributing](#contributing)
  - [Development Setup](#development-setup)
  - [Continuous Integration](#continuous-integration)
- [License](#license)

## Introduction

Inqwise Difference is a Java library designed to compare two Java objects and produce a structured representation of their differences. It leverages the power of Jackson for JSON processing to handle complex object structures, including arrays and nested objects. The library is highly customizable, allowing developers to specify fields to ignore (silent fields) and fields that require special handling (composite fields).

## Features

- **Efficient Comparison**: Quickly compute differences between two Java objects.
- **Silent Fields**: Define fields that should be ignored during comparison.
- **Composite Fields**: Handle complex fields that may require special processing.
- **JSON Patch Operations**: Supports operations like add, remove, replace, move, copy, and test as per [RFC 6902](https://tools.ietf.org/html/rfc6902).
- **Integration with Jackson**: Utilizes Jackson for JSON serialization and deserialization.
- **Easy to Use API**: Simple and intuitive methods to perform comparisons and apply patches.

## Requirements

- **Java**: 21 or higher
- **Maven**: 3.6.3 or higher
- **Dependencies**: Jackson 2.17+, Apache Commons Collections 4.4+

## Installation

### Maven Dependency

To include the Inqwise Difference library in your Maven project, add the following dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>com.inqwise.difference</groupId>
    <artifactId>inqwise-difference</artifactId>
    <version>${latest.version}</version>
</dependency>
```

> **🔗 Latest Version:** Check the [Releases page](https://github.com/inqwise/inqwise-difference/releases/latest) or use the version shown in the Maven Central badge above.

Make sure to update your project's repositories if necessary, depending on where the library is hosted. If the library is available on [Maven Central](https://search.maven.org/), no additional repository configuration is needed.

**Note**: Replace the version number with the latest version if a newer one is available.

## Usage

Here's a basic example of how to use the Inqwise Difference library:

```java
import com.inqwise.difference.Differences;
import java.util.Arrays;
import java.util.List;

public class Example {
    public static void main(String[] args) {
        // Assume obj1 and obj2 are your Java objects to compare
        Object obj1 = ...;
        Object obj2 = ...;

        // Define silent fields to ignore during comparison
        List<String> silentFields = Arrays.asList("timestamp", "id");

        // Define composite fields for special handling (if any)
        List<String> compositeFields = Arrays.asList("address");

        // Compute differences
        Differences differences = Differences.between(obj1, obj2, silentFields, compositeFields);

        // Check if there are any differences
        if (!differences.isEmpty()) {
            // Apply differences to obj1 to make it equal to obj2
            Object patchedObj = differences.applyTo(obj1);

            // Do something with the patched object
            System.out.println("Objects are now equal: " + patchedObj.equals(obj2));
        }
    }
}
```

## Examples

### Comparing Two Objects

```java
// Create two sample objects
MyObject obj1 = new MyObject("Alice", 30, "Developer");
MyObject obj2 = new MyObject("Alice", 31, "Senior Developer");

// Define silent fields
List<String> silentFields = Arrays.asList("id");

// Compute differences
Differences differences = Differences.between(obj1, obj2, silentFields);

// Output differences
differences.stream().forEach(System.out::println);
```

### Ignoring Fields During Comparison

```java
// Define silent fields
List<String> silentFields = Arrays.asList("lastModified", "createdBy");

// Compute differences while ignoring specified fields
Differences differences = Differences.between(obj1, obj2, silentFields);
```

### Handling Composite Fields

```java
// Define composite fields that require special handling
List<String> compositeFields = Arrays.asList("address.street", "address.city");

// Compute differences with composite fields
Differences differences = Differences.between(obj1, obj2, silentFields, compositeFields);
```

## Security

This project takes security seriously and includes multiple layers of security scanning:

- **CodeQL Analysis**: Static code analysis for security vulnerabilities
- **Snyk Scanning**: Dependency vulnerability scanning and monitoring
- **Automated Updates**: Dependabot keeps dependencies current with security patches

### Reporting Security Issues

If you discover a security vulnerability, please report it privately by:
1. Using GitHub's [Security Advisory](https://github.com/inqwise/inqwise-difference/security/advisories/new) feature
2. Or emailing security concerns to [alex@inqwise.com](mailto:alex@inqwise.com)

Please do not report security vulnerabilities through public issues.

## Contributing

Contributions are welcome! If you'd like to contribute to the project, please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes and ensure all tests pass:
   ```bash
   mvn clean test
   ```
4. Commit your changes with clear commit messages.
5. Submit a pull request describing your changes.

### Development Setup

```bash
# Clone the repository
git clone https://github.com/inqwise/inqwise-difference.git
cd inqwise-difference

# Build and test
mvn clean compile test

# Generate Javadocs
mvn javadoc:javadoc
```

### Continuous Integration

This project uses GitHub Actions for CI/CD:
- **CI Pipeline**: Automatically runs tests on JDK 21 and 22
- **Code Quality**: Validates dependencies and project structure
- **Release Pipeline**: Automated releases to Maven Central

Please ensure that your code adheres to the project's coding standards and includes appropriate tests. All pull requests must pass the CI checks before merging.

## License

This project is licensed under the [MIT License](LICENSE).

---

If you have any questions or need further assistance, feel free to open an issue or contact the maintainers.