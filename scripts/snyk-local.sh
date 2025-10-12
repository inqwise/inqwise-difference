#!/bin/bash

# Local Snyk Security Testing Script
# This script runs the same Snyk checks locally as in CI/CD

set -e

echo "🔍 Running local Snyk security checks..."

# Check if Snyk CLI is installed
if ! command -v snyk &> /dev/null; then
    echo "❌ Snyk CLI not found. Install it with:"
    echo "   npm install -g snyk"
    echo "   or"
    echo "   brew install snyk/tap/snyk"
    exit 1
fi

# Check if authenticated
if ! snyk auth &> /dev/null; then
    echo "🔑 Please authenticate with Snyk:"
    echo "   snyk auth"
    exit 1
fi

# Build the project first
echo "🏗️ Building project..."
mvn clean compile -DskipTests

# Run Snyk test on Maven dependencies (free tier optimized)
echo "🔍 Scanning Maven dependencies for vulnerabilities..."
echo "💰 Using high severity threshold to optimize free tier usage"
snyk test --severity-threshold=high --project-name=inqwise-difference --sarif-file-output=snyk-local.sarif

# Run Snyk monitor (optional - only for tracking)
read -p "📊 Do you want to monitor this project on Snyk? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "📊 Setting up Snyk monitoring..."
    snyk monitor --project-name=inqwise-difference
fi

echo "✅ Local Snyk security checks completed!"
echo "💡 Tip: Run 'snyk test --help' for more options"
echo "📊 SARIF report generated: snyk-local.sarif"
