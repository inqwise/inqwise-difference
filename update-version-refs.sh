#!/bin/bash

# Update Version References Script
# This script updates hardcoded version references with the latest git tag

set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}🔄 Version Reference Updater${NC}"
echo "========================================"

# Get the latest version tag (without 'v' prefix)
LATEST_TAG=$(git tag --sort=-version:refname | head -n 1 | sed 's/^v//')

if [[ -z "$LATEST_TAG" ]]; then
    echo -e "${RED}❌ No git tags found${NC}"
    exit 1
fi

echo -e "${GREEN}📋 Latest version: ${NC}$LATEST_TAG"
echo ""

# Files to update
files_to_check=(
    "README.md"
    ".github/ISSUE_TEMPLATE/bug_report.md"
    "collect-secrets.sh"
)

# Backup function
backup_file() {
    local file="$1"
    cp "$file" "$file.bak"
    echo -e "${YELLOW}📄 Backed up: ${NC}$file → $file.bak"
}

# Update README.md Maven dependency version
if [[ -f "README.md" ]]; then
    backup_file "README.md"
    
    # Update Maven dependency version
    sed -i '' "s|<version>[0-9]*\.[0-9]*\.[0-9]*</version>|<version>$LATEST_TAG</version>|g" README.md
    
    echo -e "${GREEN}✅ Updated Maven dependency version in README.md${NC}"
fi

# Update bug report template
if [[ -f ".github/ISSUE_TEMPLATE/bug_report.md" ]]; then
    backup_file ".github/ISSUE_TEMPLATE/bug_report.md"
    
    # Update example version in bug report template
    sed -i '' "s|\[e.g\. [0-9]*\.[0-9]*\.[0-9]*\]|[e.g. $LATEST_TAG]|g" .github/ISSUE_TEMPLATE/bug_report.md
    
    echo -e "${GREEN}✅ Updated bug report template version${NC}"
fi

# Update collect-secrets.sh example
if [[ -f "collect-secrets.sh" ]]; then
    backup_file "collect-secrets.sh"
    
    # Calculate next minor version for the example
    MAJOR=$(echo $LATEST_TAG | cut -d. -f1)
    MINOR=$(echo $LATEST_TAG | cut -d. -f2)
    PATCH=$(echo $LATEST_TAG | cut -d. -f3)
    NEXT_MINOR=$((MINOR + 1))
    NEXT_VERSION="$MAJOR.$NEXT_MINOR.0"
    
    # Update the example version command
    sed -i '' "s|version=[0-9]*\.[0-9]*\.[0-9]*|version=$NEXT_VERSION|g" collect-secrets.sh
    
    echo -e "${GREEN}✅ Updated collect-secrets.sh example version to: ${NC}$NEXT_VERSION"
fi

echo ""
echo -e "${GREEN}🎉 Version references updated successfully!${NC}"
echo ""
echo -e "${YELLOW}📝 Summary:${NC}"
echo "  • Latest version: $LATEST_TAG"
echo "  • Updated Maven dependency in README.md"
echo "  • Updated bug report template"
echo "  • Updated example in collect-secrets.sh"
echo ""
echo -e "${GREEN}🔒 Backup files created with .bak extension${NC}"
echo "Remove them when you're satisfied with the changes:"
echo "  rm *.bak .github/ISSUE_TEMPLATE/*.bak"