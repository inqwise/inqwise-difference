# Release Guide

This document describes how to release new versions of the inqwise-difference library.

## Prerequisites

Before releasing, ensure you have:

✅ **GPG Key Setup**: GPG key configured and available
✅ **Maven Settings**: `~/.m2/settings.xml` configured with Sonatype credentials
✅ **Repository Access**: Push access to the GitHub repository
✅ **Clean Working Directory**: All changes committed and pushed

## Automated Release (Recommended)

Use the provided release script for an automated release process:

```bash
./release.sh
```

The script will:
1. Verify prerequisites (clean working directory, GPG key)
2. Run tests
3. Ask for release version and next development version
4. Prepare and perform the release
5. Deploy to Maven Central

## Manual Release Process

### 1. Prepare for Release

```bash
# Ensure working directory is clean
git status

# Run full test suite
mvn clean test

# Verify current version
mvn help:evaluate -Dexpression=project.version -q -DforceStdout
```

### 2. Release Preparation

```bash
# Clean any previous release attempts
mvn release:clean

# Prepare release (this will create tags and update versions)
mvn release:prepare \
    -DreleaseVersion=1.1.0 \
    -DdevelopmentVersion=1.2.0-SNAPSHOT \
    -Dtag=v1.1.0
```

### 3. Perform Release

```bash
# This will build, sign, and deploy to Maven Central
mvn release:perform
```

### 4. Verify Release

1. **Check Maven Central**: Visit [Maven Central](https://central.sonatype.com) and search for `com.inqwise.difference:inqwise-difference`
2. **GitHub Release**: Create a GitHub release from the created tag
3. **Update Documentation**: Update README and CHANGELOG if needed

## Release Configuration

The project is configured with:

- **Maven Release Plugin**: Handles version management and tagging
- **Central Publishing Plugin**: Publishes to Maven Central via Sonatype
- **GPG Plugin**: Signs artifacts for security
- **Source/Javadoc Plugins**: Generates required additional artifacts

## Version Strategy

This project follows [Semantic Versioning](https://semver.org/):

- **Major** (X.y.z): Breaking API changes
- **Minor** (x.Y.z): New features, backward compatible
- **Patch** (x.y.Z): Bug fixes, backward compatible

## Rollback Process

If you need to rollback a release:

```bash
# Rollback local changes
mvn release:rollback

# Delete the created tag (if needed)
git tag -d v1.1.0
git push origin :refs/tags/v1.1.0
```

## Troubleshooting

### GPG Issues
```bash
# Test GPG signing
echo "test" | gpg --clearsign

# List available keys
gpg --list-secret-keys
```

### Maven Central Issues
- Check credentials in `~/.m2/settings.xml`
- Verify server ID matches: `sonatype-oss-release`
- Wait up to 4 hours for sync to Maven Central

### Common Errors

**"Working directory is not clean"**
- Commit all changes before releasing

**"Authentication failed"**
- Check Maven settings and credentials
- Ensure GPG passphrase is correct

**"Tag already exists"**
- Use `mvn release:clean` to clean up
- Delete existing tag if needed

## Contact

For release issues, contact: alex@inqwise.com