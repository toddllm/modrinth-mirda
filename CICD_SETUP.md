# CI/CD Setup Instructions

This document explains how to set up GitHub Actions workflow for automated builds and deployments.

## Why Manual Setup?

Due to GitHub App permissions, the workflow file cannot be added automatically. You'll need to create it manually on GitHub.

## Step-by-Step Setup

### 1. Create GitHub Actions Workflow

1. **Go to your GitHub repository**
2. **Create new file**: `.github/workflows/build.yml`
3. **Copy the contents** from `github-workflow-build.yml` in this repository
4. **Commit the file** to your repository

### 2. Add Railway Token (Optional - for auto-deployment)

If you want automatic Railway deployments:

1. **Create Railway Project First**:
   - Go to [railway.app](https://railway.app)
   - New Project → Deploy from GitHub
   - Select this repository
   - Wait for initial deployment

2. **Get Railway API Token**:
   - Go to Railway dashboard
   - Account Settings → Tokens
   - Create new token
   - Copy the token value

3. **Add to GitHub Secrets**:
   - Go to your repository on GitHub
   - Settings → Secrets and variables → Actions
   - Click "New repository secret"
   - Name: `RAILWAY_TOKEN`
   - Value: (paste Railway token)
   - Click "Add secret"

**Note:** Railway deployment is optional and costs money after the free trial. For free hosting, use FalixNodes or Oracle Cloud manually.

### 3. Verify Setup

Push a commit or create a tag to test:

```bash
# Test build on push
git commit --allow-empty -m "Test CI/CD"
git push

# Test release creation
git tag v1.0.0
git push origin v1.0.0
```

Check the "Actions" tab on GitHub to see the workflow running.

## What the Workflow Does

### On Every Push
- Checks out code
- Sets up Java 17
- Builds the mod with Gradle
- Uploads JAR artifacts (available for 30 days)

### On Tag Push (e.g., `v1.0.0`)
- All of the above, plus:
- Creates a GitHub Release
- Attaches JAR files to the release
- Generates release notes automatically
- Optionally deploys to Railway (if configured)

### On Pull Request
- Builds the mod to verify it compiles
- Reports status to PR

## Workflow File Location

The workflow file should be created at:
```
.github/workflows/build.yml
```

The contents are in `github-workflow-build.yml` in the root of this repository.

## Deployment Options

### 1. FalixNodes / Oracle Cloud (Free - Manual)

The workflow builds the mod JAR. You then:
1. Download JAR from GitHub releases or artifacts
2. Upload to your FalixNodes/Oracle Cloud server manually
3. Restart server

**Cost:** $0/month

### 2. Railway (Paid - Automatic)

The workflow can auto-deploy to Railway when:
- You push to `main` branch
- You create a version tag

**Requirements:**
- Railway account with project configured
- `RAILWAY_TOKEN` secret added to GitHub

**Cost:** $1-5/month after trial

**Note:** Railway provides public TCP access for Minecraft servers, unlike Render.

### 3. Render (Internal Only - Not Recommended)

Render cannot expose public TCP ports for game servers. Only use Render if:
- You have VPN/tunnel infrastructure
- You need internal services only

See [DEPLOYMENT.md](DEPLOYMENT.md) for details on Render limitations.

## Troubleshooting

### Workflow doesn't appear
- Make sure file is at `.github/workflows/build.yml`
- Check that it's on the correct branch
- Verify YAML syntax is valid

### Build fails
- Check Java version (must be 17)
- Verify Gradle build works locally: `./gradlew build`
- Review workflow logs in Actions tab

### Railway deployment fails
- Verify `RAILWAY_TOKEN` is set correctly
- Check Railway project exists and is linked
- Review Railway deployment logs

## Alternative: Skip GitHub Actions

If you prefer not to use GitHub Actions:

1. **Manual Builds**: Run `./gradlew build` locally
2. **Manual Releases**: Create releases on GitHub manually
3. **Manual Upload**: Upload JAR to FalixNodes/Oracle Cloud directly

See [DEPLOYMENT.md](DEPLOYMENT.md) for manual deployment instructions.

## Recommended Workflow

1. **Develop locally** with `./gradlew runClient` or `runServer`
2. **Push to GitHub** → automatic build and testing
3. **Create version tag** → GitHub Release created with JAR
4. **Download JAR** from release
5. **Upload to free host** (FalixNodes/Oracle Cloud)
6. **Play with friends!**

This workflow gives you professional CI/CD while keeping hosting costs at $0.

## Questions?

- **GitHub Actions**: [GitHub Actions Documentation](https://docs.github.com/en/actions)
- **Deployment Options**: See [DEPLOYMENT.md](DEPLOYMENT.md)
- **Free Hosting**: See [MULTI_PROVIDER.md](MULTI_PROVIDER.md)
- **Issues**: Open an issue on this GitHub repository
