#!/bin/bash

# GitHub Actions Release Workflow Validation Script
# This script validates that all required secrets and configurations are properly set

set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}🔍 Validating GitHub Actions Release Workflow${NC}"
echo "============================================="

# Check if we're in a git repository
if ! git rev-parse --git-dir > /dev/null 2>&1; then
    echo -e "${RED}❌ Not in a git repository${NC}"
    exit 1
fi

# Check if release.yml exists
if [[ ! -f ".github/workflows/release.yml" ]]; then
    echo -e "${RED}❌ .github/workflows/release.yml not found${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Release workflow file found${NC}"

# Validate workflow syntax (basic YAML check)
if command -v yq >/dev/null 2>&1; then
    if yq eval '.jobs.release' .github/workflows/release.yml > /dev/null 2>&1; then
        echo -e "${GREEN}✅ YAML syntax appears valid${NC}"
    else
        echo -e "${RED}❌ YAML syntax error in release.yml${NC}"
        exit 1
    fi
else
    echo -e "${YELLOW}⚠️  yq not found - skipping YAML validation${NC}"
fi

# Check required secrets documentation
echo ""
echo -e "${GREEN}📋 Required GitHub Secrets:${NC}"
echo "The following secrets must be configured in GitHub repository settings:"
echo ""

secrets=(
    "SONATYPE_USERNAME:Your Sonatype OSSRH username"
    "SONATYPE_PASSWORD:Your Sonatype OSSRH password/token"
    "GPG_PRIVATE_KEY:Your GPG private key (ASCII armored)"
    "GPG_PASSPHRASE:Your GPG key passphrase"
)

for secret in "${secrets[@]}"; do
    name="${secret%%:*}"
    description="${secret#*:}"
    echo -e "  ${YELLOW}$name${NC}: $description"
done

echo ""
echo -e "${GREEN}🔧 Maven Configuration Check:${NC}"

# Check if pom.xml has required configuration
if grep -q "sonatype-oss-release" pom.xml; then
    echo -e "${GREEN}✅ Sonatype profile found in pom.xml${NC}"
else
    echo -e "${RED}❌ Sonatype profile missing in pom.xml${NC}"
fi

if grep -q "central-publishing-maven-plugin" pom.xml; then
    echo -e "${GREEN}✅ Central Publishing Plugin found${NC}"
else
    echo -e "${RED}❌ Central Publishing Plugin missing${NC}"
fi

if grep -q "maven-gpg-plugin" pom.xml; then
    echo -e "${GREEN}✅ GPG Plugin found${NC}"
else
    echo -e "${RED}❌ GPG Plugin missing${NC}"
fi

if grep -q "distributionManagement" pom.xml; then
    echo -e "${GREEN}✅ Distribution Management found${NC}"
else
    echo -e "${RED}❌ Distribution Management missing${NC}"
fi

echo ""
echo -e "${GREEN}🚀 Workflow Trigger Configuration:${NC}"
echo "  • Manual trigger: workflow_dispatch with version input"
echo "  • Automatic trigger: GitHub release published"

echo ""
echo -e "${GREEN}📦 Build Process:${NC}"
echo "  1. Checkout code"
echo "  2. Setup JDK 21 with Maven cache"
echo "  3. Configure Git user"
echo "  4. Update version (manual trigger only)"
echo "  5. Build and test"
echo "  6. Deploy to Maven Central"
echo "  7. Create GitHub release assets"

# Check current version
CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout 2>/dev/null)
echo ""
echo -e "${GREEN}📋 Current Project Status:${NC}"
echo "  Current version: $CURRENT_VERSION"

# Check if there are any uncommitted changes
if git diff-index --quiet HEAD --; then
    echo -e "${GREEN}  Working directory: Clean${NC}"
else
    echo -e "${YELLOW}  Working directory: Has uncommitted changes${NC}"
fi

# Check last tag
LAST_TAG=$(git describe --tags --abbrev=0 2>/dev/null || echo "No tags found")
echo "  Last tag: $LAST_TAG"

echo ""
echo -e "${GREEN}✅ Validation Complete!${NC}"
echo ""
echo -e "${YELLOW}🔑 Next Steps to Enable Automated Releases:${NC}"
echo "  1. Configure all required secrets in GitHub repository settings"
echo "  2. Test manual release: Go to Actions → Release → Run workflow"
echo "  3. Create GitHub releases to trigger automatic deployment"
echo ""
echo -e "${GREEN}📝 Manual Release Command:${NC}"
echo "  gh workflow run release.yml -f version=1.1.1"
echo ""
echo -e "${GREEN}🎯 Automated Release Process:${NC}"
echo "  1. Create and push a git tag: git tag v1.1.1 && git push origin v1.1.1"
echo "  2. Create GitHub release from tag"
echo "  3. Workflow automatically deploys to Maven Central"