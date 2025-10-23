# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Repository contributor guide (`AGENTS.md`) with coding, testing, and release expectations.
- Codecov coverage badge in the README and automated upload via CI workflow.

### Fixed
- Aligned JUnit Platform launcher with JUnit Jupiter 6 to restore CI test discovery.

## [1.1.0] - Work in Progress

### Added
- **Comprehensive CI/CD pipeline with GitHub Actions**
  - Multi-JDK testing (Java 21, 22)
  - Automated Maven Central releases with GPG signing
  - CodeQL security analysis for vulnerability detection
  - Snyk vulnerability scanning optimized for free tier
  - Dependabot dependency updates with automated PRs
- **Security-focused development workflow**
  - Multiple layers of security scanning (CodeQL + Snyk)
  - GitHub Security tab integration with SARIF uploads
  - Automated vulnerability monitoring and alerting
  - Free tier optimized scanning (8-12 scans/month)
- **Developer experience improvements**
  - Issue templates for bug reports and feature requests
  - Pull request templates with RFC 6902 compliance checks
  - Local development scripts for security testing
  - Snyk free tier usage guide and optimization
- **Documentation enhancements**
  - WARP.md for AI assistant guidance and architecture overview
  - Comprehensive README with status badges
  - Security reporting guidelines and contact information
  - CI/CD documentation and workflow guides
  - Free tier optimization documentation

### Changed
- **Updated Vert.x from 4.5.21 to 5.0.4**
  - Maintains full backward compatibility
  - All tests passing with improved performance
  - Enhanced Java 21+ support and security updates
- **Enhanced project structure and documentation**
  - Added comprehensive status badges to README
  - Improved security section with vulnerability reporting
  - Organized CI/CD configuration with clear documentation
  - Optimized workflow triggers for cost-effective security scanning

### Security
- **Multi-layered security analysis**
  - CodeQL static analysis for security vulnerabilities
  - Snyk dependency and container vulnerability scanning
  - Automated security monitoring with smart scheduling
  - GitHub Security tab integration for centralized reporting
- **Free tier optimization**
  - Strategic scan scheduling to maximize security within budget
  - High-severity focus to optimize API quota usage
  - Production dependency focus for efficient scanning

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
