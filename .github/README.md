# CI/CD Configuration

This directory contains the GitHub Actions workflows and templates for the Inqwise Difference project.

## Workflows

### CI Pipeline (`ci.yml`)
- **Trigger**: Push/PR to main, master, develop branches
- **Jobs**:
  - **Test**: Runs tests on JDK 21 and 22
  - **Build**: Compiles, packages, and generates artifacts
  - **Code Quality**: Validates dependencies and project structure
  - **Integration Test**: Runs comprehensive verification tests
  - **Release Check**: Validates release readiness (main/master only)

### Release Pipeline (`release.yml`)
- **Trigger**: GitHub releases or manual dispatch
- **Features**:
  - Automatic deployment to Maven Central
  - GPG signing of artifacts
  - GitHub release asset upload
  - Version management for manual releases

### CodeQL Security Analysis (`codeql.yml`)
- **Trigger**: Push/PR + weekly schedule
- **Features**:
  - Security vulnerability scanning
  - Code quality analysis
  - Automatic security alerts

### Snyk Security Scanning (`snyk.yml`)
- **Trigger**: Push/PR + weekly schedule + manual dispatch
- **Jobs**:
  - **Snyk Scan**: Dependency vulnerability scanning with SARIF upload
  - **Snyk Monitor**: Continuous monitoring for main/master branches
  - **Container Scan**: Docker image vulnerability assessment
- **Features**:
  - High-severity vulnerability blocking
  - GitHub Security tab integration
  - Continuous project monitoring

## Automation

### Dependabot (`dependabot.yml`)
- **Maven Dependencies**: Weekly updates on Mondays
- **GitHub Actions**: Weekly updates on Mondays
- **Auto-assignment**: PRs assigned to alex-inqwise
- **Labels**: Automatic categorization

## Templates

### Pull Request Template
- Structured PR descriptions
- Type of change categorization
- Testing checklist
- RFC 6902 compliance verification
- Breaking change documentation

### Issue Templates
- **Bug Report**: Comprehensive bug reporting with environment details
- **Feature Request**: Structured feature proposals with API examples

## Required Secrets

For the workflows to function properly, the following secrets must be configured in the repository:

**Release Workflow:**
- `SONATYPE_USERNAME`: Sonatype OSSRH username
- `SONATYPE_PASSWORD`: Sonatype OSSRH password
- `GPG_PRIVATE_KEY`: GPG private key for artifact signing
- `GPG_PASSPHRASE`: Passphrase for the GPG key

**Security Scanning:**
- `SNYK_TOKEN`: Snyk authentication token (get from [snyk.io](https://snyk.io))

## Badge Status

The README includes the following status badges:
- CI build status
- Release workflow status
- CodeQL security analysis
- Snyk security scanning
- Maven Central version
- License information
- Java version compatibility
- RFC 6902 compliance

## Local Development

All workflows are designed to work with the following local development setup:

```bash
# Java 21+ required
java -version

# Maven 3.6.3+ required
mvn --version

# Run the same checks locally
mvn clean verify
mvn dependency:analyze
mvn javadoc:javadoc
```