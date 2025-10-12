# Snyk Free Tier Configuration

This project is optimized for [Snyk's free tier](https://snyk.io/plans/) usage while maintaining effective security scanning.

## 📊 Free Tier Limits

- **200 tests/month** for open source projects
- **Limited container scans**
- **Basic vulnerability database**
- **Community support**

## 🎯 Our Optimization Strategy

### **Scheduled Scans**
- **Monthly automated scans** (1st of each month)
- **Pull request scans** for code changes
- **Release scans** on tagged versions
- **Manual dispatch** when needed

### **Severity Focus**
- **High severity threshold** to reduce API usage
- **Production dependencies only** (skip test scope)
- **Critical vulnerabilities prioritized**

### **Smart Triggers**
- **No daily scans** - only on important events
- **Container scans** only on releases
- **Monitoring** only on tagged releases

## 🔧 Configuration Files

### `.snyk` Policy
```yaml
language-settings:
  java:
    skipUnresolved: true
    skipTestScope: true  # Free tier optimization
```

### Workflow Triggers
- ✅ **Pull Requests** - Catch issues before merge
- ✅ **Releases** - Security validation for published versions
- ✅ **Monthly Schedule** - Regular dependency monitoring
- ✅ **Manual Dispatch** - On-demand scanning
- ❌ **Every Push** - Disabled to conserve API calls

## 📈 Usage Estimate

With this configuration, monthly usage should be:
- **~8-12 scans/month** (well within 200 limit)
- **Pull requests**: ~4-8/month
- **Releases**: ~1-2/month
- **Scheduled**: 1/month
- **Manual**: As needed

## 🚀 Getting Started

1. **Sign up** at [snyk.io](https://snyk.io) (free account)
2. **Get your token** from Account Settings
3. **Add to GitHub secrets**: `SNYK_TOKEN`
4. **Workflows activate automatically**

## 💡 Tips for Free Tier

### **Maximize Value**
- **Focus on high/critical** vulnerabilities first
- **Use manual dispatch** for urgent scans
- **Monitor release candidates** before publishing
- **Review monthly reports** for trends

### **Avoid Overuse**
- **Don't scan feature branches** frequently
- **Batch dependency updates** when possible
- **Use local script** for development testing
- **Let CI handle official scans**

## 🔍 Local Development

Use the local script for development without consuming API quota:

```bash
# Install Snyk CLI
npm install -g snyk

# Authenticate
snyk auth

# Run local scan (counts against quota)
./scripts/snyk-local.sh
```

## 📊 Alternative Free Tools

If you exceed the free tier limits, consider these alternatives:

- **GitHub Dependabot** (free, built-in)
- **OWASP Dependency Check** (free, open source)
- **Trivy** (free, open source)
- **npm audit** (for Node.js dependencies)

## 🔄 Upgrading Options

If you need more scans:
- **Snyk Team**: $25/month, 1000 tests
- **GitHub Advanced Security**: Includes dependency scanning
- **Enterprise tools**: For larger organizations

## 📞 Support

- **Community**: [Snyk Community](https://support.snyk.io/hc/en-us/community/topics)
- **Documentation**: [Snyk Docs](https://docs.snyk.io/)
- **GitHub Issues**: For project-specific questions