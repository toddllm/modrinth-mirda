# CI/CD Setup Instructions

This document explains how to manually set up GitHub Actions workflow for automated builds and deployments to Render.

## Why Manual Setup?

Due to GitHub App permissions, the workflow file cannot be added automatically. You'll need to create it manually on GitHub.

## Step-by-Step Setup

### 1. Create GitHub Actions Workflow

1. **Go to your GitHub repository**
2. **Create new file**: `.github/workflows/build.yml`
3. **Copy the contents** from `github-workflow-build.yml` in this repository
4. **Commit the file** to your repository

### 2. Add Render Deploy Hook (Optional - for auto-deployment)

If you want automatic Render deployments:

1. **Create Render Service First**:
   - Go to [dashboard.render.com](https://dashboard.render.com)
   - Create your Minecraft server service (see DEPLOYMENT.md)
   - Wait for initial deployment

2. **Get Deploy Hook URL**:
   - Go to your service in Render Dashboard
   - Settings → Deploy Hook
   - Copy the URL (e.g., `https://api.render.com/deploy/srv-xxx?key=yyy`)

3. **Add to GitHub Secrets**:
   - Go to your repository on GitHub
   - Settings → Secrets and variables → Actions
   - Click "New repository secret"
   - Name: `RENDER_DEPLOY_HOOK_URL`
   - Value: (paste deploy hook URL)
   - Click "Add secret"

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
✅ Checks out code
✅ Sets up Java 17
✅ Builds the mod with Gradle
✅ Uploads JAR artifacts (available for 30 days)

### On Tag Push (e.g., `v1.0.0`)
✅ All of the above, plus:
✅ Creates a GitHub Release
✅ Attaches JAR files to the release
✅ Generates release notes automatically
✅ Deploys to Render (if RENDER_DEPLOY_HOOK_URL is configured)

### On Pull Request
✅ Builds the mod to verify it compiles
✅ Runs tests
✅ Reports status to PR

## Workflow File Location

The workflow file should be created at:
```
.github/workflows/build.yml
```

The contents are in `github-workflow-build.yml` in the root of this repository.

## Render Deployment

The workflow includes automatic Render deployment when:
- You push to `main` branch
- You create a version tag

**Requirements:**
- Render service created and deployed once
- `RENDER_DEPLOY_HOOK_URL` secret added to GitHub

**To disable Render deployment:**
Remove or comment out the `deploy-to-render` job in the workflow file.

## Troubleshooting

### Workflow doesn't appear
- Make sure file is at `.github/workflows/build.yml`
- Check that it's on the correct branch
- Verify YAML syntax is valid

### Build fails
- Check Java version (must be 17)
- Verify Gradle build works locally: `./gradlew build`
- Review workflow logs in Actions tab

### Render deployment fails
- Verify `RENDER_DEPLOY_HOOK_URL` is set correctly
- Check Render service exists and is running
- Review Render deployment logs
- Ensure deploy hook URL is complete (includes `?key=xxx`)

## Alternative: Skip GitHub Actions

If you prefer not to use GitHub Actions:

1. **Manual Builds**: Run `./gradlew build` locally
2. **Manual Releases**: Create releases on GitHub manually
3. **Manual Render Deploy**: Use Render dashboard or deploy hook

See `DEPLOYMENT.md` for manual deployment instructions.

## Questions?

- **GitHub Actions**: [GitHub Actions Documentation](https://docs.github.com/en/actions)
- **Render**: See `DEPLOYMENT.md` in this repository
- **Issues**: Open an issue on this GitHub repository
