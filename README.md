# Inqwise Difference - Library for Compare

This library provides an efficient and flexible way to compute and represent differences between two Java objects. It allows users to define “silent fields” that should be ignored during the comparison process, and “composite fields” that require more complex handling. The core functionality revolves around creating and managing differences in a structured format using Jackson for JSON processing. The library also supports various operations like add, remove, replace, move, and more. Ideal for projects that require object comparison, change tracking, or patching.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Installation](#installation)
  - [Maven Dependency](#maven-dependency)
- [Usage](#usage)
- [Examples](#examples)
  - [Comparing Two Objects](#comparing-two-objects)
  - [Ignoring Fields During Comparison](#ignoring-fields-during-comparison)
  - [Handling Composite Fields](#handling-composite-fields)
- [Contributing](#contributing)
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

## Installation

### Maven Dependency

To include the Inqwise Difference library in your Maven project, add the following dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>com.inqwise.difference</groupId>
    <artifactId>inqwise-difference</artifactId>
    <version>1.0.0</version>
</dependency>
```

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

## Contributing

Contributions are welcome! If you'd like to contribute to the project, please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Commit your changes with clear commit messages.
4. Submit a pull request describing your changes.

Please ensure that your code adheres to the project's coding standards and includes appropriate tests.

## License

This project is licensed under the [MIT License](LICENSE).

---

If you have any questions or need further assistance, feel free to open an issue or contact the maintainers.