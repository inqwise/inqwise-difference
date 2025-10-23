# Repository Guidelines

## Project Structure & Module Organization
The library lives under `src/main/java/com/inqwise/difference`; supporting configuration sits in `src/main/resources` and generated sources in `src/main/generated`. Tests mirror this layout inside `src/test/java` and `src/test/resources`, while release utilities live in `scripts/` and `release.sh`. Use this structure when adding new packages or fixtures to keep tooling aligned.

## Build, Test, and Development Commands
- `mvn clean install`: Compile, run the unit suite, and assemble the JAR.
- `mvn test`: Fast cycle to execute JUnit 5 specs only.
- `mvn verify`: Run full validation, including signatures and enforcer rules.
- `mvn javadoc:javadoc`: Generate API docs into `target/site/apidocs`.
- `./scripts/snyk-local.sh`: Perform dependency vulnerability scanning before shipping.

## Coding Style & Naming Conventions
Target Java 21 with 4-space indentation and UTF-8 source files. Keep classes in `UpperCamelCase`, methods and fields in `lowerCamelCase`, and constants in `UPPER_SNAKE_CASE`. Stick to the `com.inqwise.difference` root package and favour expressive, single-responsibility classes. Before committing, ensure imports are ordered and unused code removed.

## Testing Guidelines
JUnit 5 and Vert.x extensions power the suite; name new test classes `*Test` and store sample payloads under `src/test/resources`. Prefer parameterized tests for combinatorial cases and cover both positive and failure paths. Run `mvn test` locally and confirm new branches pass GitHub Actions before requesting review.

## Commit & Pull Request Guidelines
Write imperative commits, optionally with scopes, such as `feat(diff): add composite handler`. Group related changes together and include tests or fixtures in the same commit. Pull requests should summarize intent, link relevant issues, list verification commands, and add screenshots or sample payloads when behavior changes. Ensure branch history is rebased and CI is green prior to merge.

## Security & Release Notes
Follow `SECURITY.md` for responsible disclosure and avoid committing secrets. Run `./scripts/snyk-local.sh` and `mvn verify` before invoking `release.sh`. Tag release preparation commits via the Maven Release Plugin and document notable changes in `RELEASE.md`.
