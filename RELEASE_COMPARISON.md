# Release Methods Comparison

This document compares the two release methods available for the inqwise-difference project.

## Release Methods

### 1. Manual Script (`./release.sh`)

**When to use**: For local development releases and testing

**Process**:
```bash
./release.sh
# Interactive prompts for versions
# Runs Maven Release Plugin locally
```

**Features**:
- ✅ Interactive version prompts
- ✅ Pre-flight checks (clean working directory, correct branch)
- ✅ GPG key validation
- ✅ Maven Release Plugin (full lifecycle)
- ✅ Automatic git operations (commits, tags, pushes)
- ✅ Built-in rollback support
- ✅ Local execution

### 2. GitHub Actions (`release.yml`)

**When to use**: For CI/CD automated releases

**Triggers**:
- **Manual**: Workflow dispatch with version input
- **Automatic**: GitHub release published

**Features**:
- ✅ Cloud-based execution
- ✅ Maven Release Plugin (manual trigger)
- ✅ Simple deploy (tag-based trigger) 
- ✅ Automatic GitHub release assets
- ✅ Secret management via GitHub
- ✅ Build artifacts uploaded to releases

## Process Comparison

| **Step** | **Manual Script** | **GitHub Actions (Manual)** | **GitHub Actions (Auto)** |
|----------|-------------------|------------------------------|----------------------------|
| **Trigger** | Local execution | Workflow dispatch | GitHub release |
| **Version Input** | Interactive prompts | Workflow input | From tag |
| **Testing** | `mvn clean test` | `mvn clean test` | `mvn clean test` |
| **Release Process** | Maven Release Plugin | Maven Release Plugin | Direct deploy |
| **Git Operations** | Automatic (via plugin) | Automatic (via plugin) | None |
| **Deployment** | Maven Central | Maven Central | Maven Central |
| **Artifacts** | Local target/ | GitHub release assets | GitHub release assets |

## Recommended Workflows

### Development Releases
```bash
# Use manual script for testing and development
./release.sh
```

### Production Releases

**Option 1: Manual Trigger**
```bash
# Trigger GitHub Actions workflow
gh workflow run release.yml -f version=1.1.1
```

**Option 2: Tag-based (Recommended)**
```bash
# Create and push tag
git tag v1.1.1
git push origin v1.1.1

# Create GitHub release from tag
# This automatically triggers deployment
```

## Key Differences

### Manual Script Advantages
- **Local control**: Run from your development environment
- **Interactive**: Prompts guide you through the process  
- **Validation**: Pre-flight checks before release
- **Rollback**: Easy to rollback if needed
- **Testing**: Perfect for testing release process

### GitHub Actions Advantages  
- **Automation**: Triggered by tags/releases
- **Consistency**: Same environment every time
- **Security**: Secrets managed by GitHub
- **Artifacts**: Automatic GitHub release assets
- **Audit**: Full logs of release process
- **Integration**: Works with GitHub releases

## Environment Requirements

### Manual Script
- Local Maven installation
- GPG key configured locally
- Local Maven settings (~/.m2/settings.xml)
- Git repository with push access

### GitHub Actions
- GitHub repository secrets configured:
  - `SONATYPE_USERNAME`
  - `SONATYPE_PASSWORD` 
  - `GPG_PRIVATE_KEY`
  - `GPG_PASSPHRASE`

## Troubleshooting

### Manual Script Issues
```bash
# Check GPG keys
gpg --list-secret-keys

# Check Maven settings
cat ~/.m2/settings.xml

# Clean up if needed
mvn release:clean
```

### GitHub Actions Issues
```bash
# Check workflow status
gh run list --workflow=release.yml

# View logs
gh run view [RUN_ID] --log

# Validate configuration
./scripts/validate-release-workflow.sh
```

## Best Practices

1. **Use manual script** for development and testing
2. **Use GitHub Actions** for production releases
3. **Test locally first** before triggering CI/CD
4. **Create GitHub releases** to trigger automatic deployment
5. **Monitor Maven Central** for successful publication
6. **Tag semantic versions** (e.g., v1.1.0, v1.1.1)

## Summary

Both methods use the same underlying Maven Release Plugin for consistency. The main difference is execution environment:

- **Manual script**: Local development environment
- **GitHub Actions**: Cloud CI/CD environment

Both deploy to Maven Central and maintain the same release quality and process.