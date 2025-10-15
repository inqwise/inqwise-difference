#!/bin/bash

# GitHub Secrets Collection Script
# This script helps you collect and set the required GitHub secrets

set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}🔐 GitHub Secrets Collection${NC}"
echo "======================================"

# Check if gh CLI is available
if ! command -v gh &> /dev/null; then
    echo -e "${RED}❌ GitHub CLI (gh) is not installed${NC}"
    echo "Please install it: brew install gh"
    exit 1
fi

# Check if we're in the right directory
if [[ ! -f "pom.xml" ]] || ! grep -q "inqwise-difference" pom.xml; then
    echo -e "${RED}❌ Please run this script from the inqwise-difference directory${NC}"
    exit 1
fi

echo -e "${GREEN}📋 Collected Secret Values:${NC}"
echo ""

# 1. SONATYPE_USERNAME
SONATYPE_USERNAME="+OTIkt8f"
echo -e "${YELLOW}1. SONATYPE_USERNAME:${NC} $SONATYPE_USERNAME"

# 2. SONATYPE_PASSWORD
SONATYPE_PASSWORD="oSMPeklIOIVBoSWBqMmMuNOOCUOh7v1SCC52IcBXJ3sb"
echo -e "${YELLOW}2. SONATYPE_PASSWORD:${NC} $SONATYPE_PASSWORD"

# 3. GPG_PASSPHRASE
GPG_PASSPHRASE="v*@WCLzdWCeC8v6v"
echo -e "${YELLOW}3. GPG_PASSPHRASE:${NC} $GPG_PASSPHRASE"

# 4. GPG_PRIVATE_KEY
echo -e "${YELLOW}4. GPG_PRIVATE_KEY:${NC} (will be exported securely)"

echo ""
echo -e "${GREEN}🔑 Exporting GPG Private Key...${NC}"
echo "You may be prompted for your GPG passphrase."

GPG_KEY_ID="C3B00B83B0ED1843769A0BFFA7B22DF4D545B1CE"
GPG_PRIVATE_KEY=$(gpg --armor --export-secret-keys "$GPG_KEY_ID" 2>/dev/null)

if [[ -z "$GPG_PRIVATE_KEY" ]]; then
    echo -e "${RED}❌ Failed to export GPG private key${NC}"
    echo "Please make sure:"
    echo "  1. Your GPG key is unlocked"
    echo "  2. The passphrase is correct"
    echo ""
    echo "You can manually export it with:"
    echo "  gpg --armor --export-secret-keys $GPG_KEY_ID"
    exit 1
fi

echo -e "${GREEN}✅ GPG private key exported successfully${NC}"

echo ""
echo -e "${GREEN}🚀 Setting GitHub Secrets...${NC}"

# Set secrets using GitHub CLI
echo "$SONATYPE_USERNAME" | gh secret set SONATYPE_USERNAME
echo "$SONATYPE_PASSWORD" | gh secret set SONATYPE_PASSWORD
echo "$GPG_PASSPHRASE" | gh secret set GPG_PASSPHRASE
echo "$GPG_PRIVATE_KEY" | gh secret set GPG_PRIVATE_KEY

echo ""
echo -e "${GREEN}✅ All secrets set successfully!${NC}"

# Verify secrets were set
echo ""
echo -e "${GREEN}📋 Verifying secrets...${NC}"
gh secret list

echo ""
echo -e "${GREEN}🎉 Setup Complete!${NC}"
echo ""
echo -e "${YELLOW}📝 Next steps:${NC}"
echo "  1. Test the workflow:"
echo "     gh workflow run release.yml -f version=1.1.4"
echo ""
echo "  2. Monitor the run:"
echo "     gh run list --workflow=release.yml"
echo ""
echo "  3. View logs if needed:"
echo "     gh run view --log"

echo ""
echo -e "${GREEN}🔒 Security Notes:${NC}"
echo "  • All secrets are now stored securely in GitHub"
echo "  • They will be masked in workflow logs"
echo "  • You can rotate them anytime in repository settings"