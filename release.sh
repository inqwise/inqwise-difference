#!/bin/bash

# Release script for inqwise-difference
# This script automates the Maven release process

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 Starting Maven Release Process${NC}"

# Check if working directory is clean
if ! git diff-index --quiet HEAD --; then
    echo -e "${RED}❌ Working directory is not clean. Please commit or stash changes first.${NC}"
    exit 1
fi

# Check if we're on master branch
BRANCH=$(git branch --show-current)
if [ "$BRANCH" != "master" ]; then
    echo -e "${YELLOW}⚠️  You're not on master branch (current: $BRANCH). Continue? [y/N]${NC}"
    read -r response
    if [[ ! "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        echo "Aborted."
        exit 1
    fi
fi

# Get current version
CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo -e "${GREEN}📦 Current version: $CURRENT_VERSION${NC}"

# Ask for release version
echo -e "${YELLOW}Enter release version (press Enter to use ${CURRENT_VERSION%-SNAPSHOT}):${NC}"
read -r RELEASE_VERSION
if [ -z "$RELEASE_VERSION" ]; then
    RELEASE_VERSION=${CURRENT_VERSION%-SNAPSHOT}
fi

# Ask for next development version
echo -e "${YELLOW}Enter next development version:${NC}"
read -r NEXT_VERSION

# Verify GPG key is available
echo -e "${GREEN}🔐 Verifying GPG setup...${NC}"
if ! gpg --list-secret-keys | grep -q "alex@inqwise.com"; then
    echo -e "${RED}❌ GPG key not found or not configured${NC}"
    exit 1
fi

echo -e "${GREEN}✅ GPG key found${NC}"

# Run tests before release
echo -e "${GREEN}🧪 Running tests...${NC}"
mvn clean test

# Prepare and perform release
echo -e "${GREEN}🚀 Preparing release...${NC}"
mvn release:clean release:prepare \
    -DreleaseVersion="$RELEASE_VERSION" \
    -DdevelopmentVersion="$NEXT_VERSION" \
    -Dtag="v$RELEASE_VERSION"

echo -e "${GREEN}🚀 Performing release...${NC}"
mvn release:perform

echo -e "${GREEN}🎉 Release completed successfully!${NC}"
echo -e "${GREEN}📋 Next steps:${NC}"
echo -e "  1. Check Maven Central for deployment status"
echo -e "  2. Update GitHub release notes"
echo -e "  3. Announce the release"

# Ask if user wants to clean up release files
echo -e "${YELLOW}Clean up release files? [y/N]${NC}"
read -r cleanup_response
if [[ "$cleanup_response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
    mvn release:clean
    echo -e "${GREEN}✅ Release files cleaned up${NC}"
fi