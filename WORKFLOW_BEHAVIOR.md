# GitHub Actions Release Workflow Behavior

## ✅ **Yes, the workflow now creates GitHub releases automatically!**

The updated workflow has two distinct flows that both handle GitHub release creation properly.

## Workflow Flows

### 1. 🔧 **Manual Trigger** (`workflow_dispatch`)

**Trigger**: Manual workflow execution with version input

**Process**:
```bash
# Trigger via GitHub CLI
gh workflow run release.yml -f version=1.1.1

# Or via GitHub web interface:
# Actions → Release → Run workflow
```

**What it does**:
1. ✅ Runs tests (`mvn clean test`)
2. ✅ Uses Maven Release Plugin for full release lifecycle
3. ✅ Creates git tag (e.g., `v1.1.1`) 
4. ✅ Deploys to Maven Central
5. ✅ **Creates GitHub release automatically**
6. ✅ Uploads release assets (JAR, sources, javadoc)

### 2. 📋 **Tag-based Trigger** (`release: [published]`)

**Trigger**: When a GitHub release is manually created

**Process**:
```bash
# Create GitHub release from existing tag
# This triggers the workflow automatically
```

**What it does**:
1. ✅ Runs tests (`mvn clean test`)
2. ✅ Deploys to Maven Central (direct deploy)
3. ✅ Uploads release assets to existing GitHub release

## Release Creation Details

### Manual Trigger Release Creation

When using `workflow_dispatch`, the workflow automatically creates a GitHub release with:

**Release Details**:
- **Tag**: `v{version}` (e.g., `v1.1.1`)
- **Title**: `Release v{version}`
- **Status**: Published (not draft)
- **Type**: Stable (not prerelease)

**Release Body**:
```markdown
🚀 **Release v1.1.1**

## 📦 Maven Central
```xml
<dependency>
    <groupId>com.inqwise.difference</groupId>
    <artifactId>inqwise-difference</artifactId>
    <version>1.1.1</version>
</dependency>
```

## 📋 Release Assets
- Main library JAR
- Sources JAR  
- Javadoc JAR
- All artifacts GPG signed

## 🔗 Links
- [Maven Central](https://central.sonatype.com/artifact/com.inqwise.difference/inqwise-difference/1.1.1)
- [Documentation](https://github.com/inqwise/inqwise-difference#readme)

---
*Released automatically via GitHub Actions*
```

**Attached Files**:
- `inqwise-difference-1.1.1.jar`
- `inqwise-difference-1.1.1-sources.jar`
- `inqwise-difference-1.1.1-javadoc.jar`

## Comparison with Manual Script

| **Aspect** | **Manual Script** | **GitHub Actions (Manual)** | **GitHub Actions (Tag-based)** |
|------------|-------------------|------------------------------|--------------------------------|
| **GitHub Release** | ❌ Manual creation needed | ✅ **Created automatically** | ✅ Uses existing release |
| **Release Assets** | ❌ Manual upload needed | ✅ **Uploaded automatically** | ✅ Uploaded automatically |
| **Maven Central** | ✅ Deployed | ✅ Deployed | ✅ Deployed |
| **Git Operations** | ✅ Full lifecycle | ✅ Full lifecycle | ❌ None |

## Recommended Usage

### For Complete Automation
```bash
# Single command creates everything:
# - Git tag
# - GitHub release  
# - Maven Central deployment
# - Release assets
gh workflow run release.yml -f version=1.1.1
```

### For Custom Release Notes
```bash
# 1. Run workflow for deployment
gh workflow run release.yml -f version=1.1.1

# 2. Edit release notes afterwards if needed
gh release edit v1.1.1
```

## Summary

✅ **The GitHub Actions workflow now provides complete automation:**

- **Manual Trigger**: Creates GitHub release + deploys to Maven Central
- **Tag-based Trigger**: Uses existing GitHub release + deploys to Maven Central  
- **Release Assets**: Automatically attached to all GitHub releases
- **Consistency**: Both flows produce the same final result

The workflow is now a complete replacement for manual release processes! 🚀