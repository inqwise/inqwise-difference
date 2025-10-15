# GitHub Secrets Configuration

This document explains how to configure the required GitHub repository secrets for automated releases.

## Required Secrets

The following secrets must be configured in your GitHub repository settings (`Settings` → `Secrets and variables` → `Actions`):

### 1. SONATYPE_USERNAME
- **Value**: Your Sonatype OSSRH username 
- **Source**: From your Sonatype OSSRH account
- **Example**: `alex-inqwise`

### 2. SONATYPE_PASSWORD  
- **Value**: Your Sonatype OSSRH password or user token
- **Source**: From your Sonatype OSSRH account (prefer user tokens over passwords)
- **Note**: Use a user token for better security

### 3. GPG_PRIVATE_KEY
- **Value**: Your GPG private key in ASCII armored format
- **How to get it**:
  ```bash
  # List your keys to find the key ID
  gpg --list-secret-keys
  
  # Export the private key (replace KEY_ID with your actual key ID)
  gpg --armor --export-secret-keys KEY_ID
  ```
- **Format**: Should start with `-----BEGIN PGP PRIVATE KEY BLOCK-----`

### 4. GPG_PASSPHRASE
- **Value**: The passphrase for your GPG private key
- **Source**: The passphrase you set when creating your GPG key
- **Security**: This is sensitive - GitHub will mask it in logs

## How to Configure Secrets

1. **Navigate to Repository Settings**:
   - Go to your GitHub repository
   - Click `Settings` tab
   - Select `Secrets and variables` → `Actions`

2. **Add Repository Secrets**:
   - Click `New repository secret`
   - Enter the secret name (e.g., `SONATYPE_USERNAME`)
   - Enter the secret value
   - Click `Add secret`
   - Repeat for all 4 secrets

3. **Verify Configuration**:
   - All 4 secrets should be listed under "Repository secrets"
   - Secret values will be masked (showing only "***")

## Testing the Configuration

### Manual Release Test
```bash
# Trigger manual workflow via GitHub CLI
gh workflow run release.yml -f version=1.1.1

# Or via GitHub web interface:
# Go to Actions → Release → Run workflow
```

### Automatic Release Test
```bash
# Create and push a tag
git tag v1.1.2
git push origin v1.1.2

# Create GitHub release from the tag
# This will automatically trigger the release workflow
```

## Security Best Practices

1. **Use User Tokens**: For Sonatype, prefer user tokens over passwords
2. **Rotate Secrets**: Regularly update your secrets, especially if compromised
3. **Minimal Permissions**: Ensure your Sonatype account has minimal required permissions
4. **GPG Key Security**: Keep your GPG private key secure and backed up
5. **Monitor Usage**: Check GitHub Actions logs for any authentication issues

## Troubleshooting

### Common Issues

**Authentication Failed (Sonatype)**:
- Verify username and password/token are correct
- Check if your Sonatype account is active
- Ensure you have deployment permissions

**GPG Signing Failed**:
- Verify GPG private key format (should be ASCII armored)
- Check if passphrase is correct
- Ensure the key hasn't expired

**Build Failures**:
- Check if all dependencies resolve correctly
- Verify Java version compatibility (JDK 21)
- Review test failures in the workflow logs

### Debug Steps

1. **Check Workflow Logs**: Go to Actions tab and review failed workflow runs
2. **Validate Locally**: Run the same commands locally to isolate issues
3. **Test Secrets**: Use the validation script: `./scripts/validate-release-workflow.sh`

## Related Documentation

- [Maven Central Publishing Guide](https://central.sonatype.org/publish/publish-guide/)
- [GPG Key Generation](https://docs.github.com/en/authentication/managing-commit-signature-verification/generating-a-new-gpg-key)
- [GitHub Actions Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)

## Support

For issues specific to this repository's release process, check:
1. Repository Issues
2. RELEASE.md documentation  
3. GitHub Actions workflow logs