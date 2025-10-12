# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Comprehensive CI/CD pipeline with GitHub Actions
  - Multi-JDK testing (Java 21, 22)
  - Automated Maven Central releases
  - CodeQL security analysis
  - Snyk vulnerability scanning
  - Dependabot dependency updates
- Security-focused development workflow
  - Multiple layers of security scanning
  - GitHub Security tab integration
  - Automated vulnerability detection
- Developer experience improvements
  - Issue templates for bug reports and feature requests
  - Pull request templates with RFC 6902 compliance checks
  - Local development scripts for security testing
- Documentation enhancements
  - WARP.md for AI assistant guidance
  - Comprehensive README with badges
  - Security reporting guidelines
  - CI/CD documentation

### Changed
- **Updated Vert.x from 4.5.21 to 5.0.4**
  - Maintains backward compatibility
  - All tests passing
  - JSON handling functionality preserved
- Enhanced README with status badges and security information
- Improved project structure with organized CI/CD configuration

### Security
- Added CodeQL static analysis for security vulnerabilities
- Integrated Snyk for dependency vulnerability scanning
- Automated security monitoring and alerting
- Security advisory reporting system

## [1.0.0] - Initial Release

### Added
- Core JSON Patch functionality with RFC 6902 compliance
- Silent fields support with wildcard patterns
- Composite fields handling
- Jackson integration for JSON processing
- Vert.x JsonObject compatibility
- Comprehensive test suite
- Maven Central distribution
- MIT license