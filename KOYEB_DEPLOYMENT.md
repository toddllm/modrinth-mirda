# Koyeb Deployment Guide

This guide explains how to deploy services to Koyeb using GitHub Actions.

## Important Limitations

**Koyeb's free tier (512 MB RAM, 0.1 vCPU) is NOT suitable for running a Minecraft server.**

The Mirda mod server requires at least 4GB RAM for stable operation. Koyeb's free tier is designed for:
- Status dashboards
- API proxies
- Health monitoring services
- Lightweight web applications

**For the actual Minecraft server**, use providers listed in [FREE_TIER.md](FREE_TIER.md):
- FalixNodes (4GB RAM, free)
- Oracle Cloud Always Free (24GB RAM)
- Aternos (backup option)

---

## Setting Up Koyeb Deployment

### Step 1: Create a Koyeb Account

1. Visit [Koyeb](https://www.koyeb.com/)
2. Sign up for a free account
3. Verify your email address

### Step 2: Generate an API Token

1. Log in to your Koyeb dashboard
2. Go to **Settings** (gear icon) in the left sidebar
3. Click **API** in the settings menu
4. Click **Create API Key**
5. Give it a descriptive name (e.g., "GitHub Actions Deploy")
6. Copy the generated token immediately (you won't see it again)

**Security Note:** Treat this token like a password. Anyone with this token can manage your Koyeb services.

### Step 3: Add Token to GitHub Secrets

1. Go to your GitHub repository
2. Navigate to **Settings** > **Secrets and variables** > **Actions**
3. Click **New repository secret**
4. Set the following:
   - **Name:** `KOYEB_API_TOKEN`
   - **Secret:** Paste your Koyeb API token
5. Click **Add secret**

### Step 4: Trigger the Deployment

The deployment workflow triggers automatically on:
- Any push to the `main` branch
- Manual trigger via GitHub Actions UI

**Manual Trigger:**
1. Go to **Actions** tab in your repository
2. Select **Deploy to Koyeb** workflow
3. Click **Run workflow**
4. Select the branch and click **Run workflow**

---

## What the Workflow Does

The GitHub Actions workflow (`.github/workflows/deploy_koyeb.yml`) performs these steps:

1. **Checkout** - Clones the repository
2. **Setup Java** - Installs Java 17 for Gradle
3. **Build Mod** - Compiles the Mirda mod JAR using Gradle
4. **Verify Build** - Ensures JAR file exists
5. **Setup Docker** - Prepares Docker Buildx
6. **Install Koyeb CLI** - Downloads and installs the Koyeb command-line tool
7. **Authenticate** - Logs in to Koyeb using your API token
8. **Build Image** - Creates Docker image from Dockerfile
9. **Deploy** - Creates or updates the Koyeb service
10. **Verify** - Checks deployment status

---

## Service Configuration

The workflow deploys with these settings:

| Setting | Value | Description |
|---------|-------|-------------|
| Service Name | `mirda-server` | Koyeb service identifier |
| Instance Type | `nano` | Free tier (512MB RAM, 0.1 vCPU) |
| Region | `fra` (Frankfurt) | Geographic location |
| Port | `25565` | Minecraft server port (TCP) |
| Memory Env | `512M` | JVM memory allocation |
| Scaling | 0-1 | Auto-scale to zero when idle |

---

## Monitoring Your Deployment

### Koyeb Dashboard

1. Visit [app.koyeb.com](https://app.koyeb.com)
2. View your service under **Apps**
3. Check logs, metrics, and deployment history

### GitHub Actions

1. Go to **Actions** tab in your repository
2. View workflow runs and logs
3. Check for any deployment errors

---

## Customizing the Deployment

### Change Region

Edit `.github/workflows/deploy_koyeb.yml`:

```yaml
env:
  KOYEB_REGION: was  # Washington DC (US)
```

Available regions:
- `fra` - Frankfurt, Germany
- `was` - Washington DC, USA
- `sin` - Singapore

### Change Service Name

```yaml
env:
  KOYEB_SERVICE_NAME: my-custom-service
```

### Adjust Environment Variables

Add more environment variables in the deployment step:

```bash
--env CUSTOM_VAR=value \
--env ANOTHER_VAR=another_value
```

---

## Suitable Use Cases for Koyeb

Given the resource constraints (512MB RAM), Koyeb is ideal for:

### 1. Status Dashboard
A lightweight web app showing server status:
```dockerfile
FROM node:18-alpine
# Build a simple status page
```

### 2. API Proxy
A proxy service to check if the main server is online:
```python
# Flask app that pings your actual Minecraft server
```

### 3. Discord Bot
A bot that reports server status to Discord:
```python
# Discord.py bot with server status commands
```

### 4. Health Monitor
Monitor multiple Minecraft servers and report status:
```go
// Go service that checks server health
```

---

## Deploying Auxiliary Services

To deploy something other than the main Minecraft server:

1. Create a separate branch or directory for your service
2. Modify the Dockerfile for your specific service
3. Update environment variables as needed
4. Push to trigger deployment

Example: Status Dashboard Dockerfile

```dockerfile
FROM python:3.11-slim

WORKDIR /app
COPY requirements.txt .
RUN pip install -r requirements.txt

COPY status_app.py .

EXPOSE 8080
CMD ["python", "status_app.py"]
```

---

## Troubleshooting

### Authentication Fails

**Error:** `KOYEB_API_TOKEN secret is not set`

**Solution:**
1. Ensure the secret is named exactly `KOYEB_API_TOKEN`
2. Check for typos in the token
3. Generate a new token if expired

### Build Fails

**Error:** `No JAR file found in build/libs/`

**Solution:**
1. Ensure Gradle build completes successfully
2. Check for compilation errors in the mod code
3. Verify `build.gradle` configuration

### Deployment Hangs

**Issue:** Service takes too long to start

**Reason:** Minecraft server requires more memory than available

**Solution:**
- Don't deploy the full Minecraft server to Koyeb free tier
- Use for auxiliary services only

### Service Crashes

**Error:** Out of memory (OOM)

**Reason:** 512MB is insufficient for Java + Minecraft

**Solution:**
- Reduce MEMORY env to absolute minimum
- Better yet, deploy only lightweight services

---

## Cost Considerations

### Free Tier Limits

- **Instance**: nano (512MB RAM, 0.1 vCPU)
- **Hours**: Limited monthly free tier
- **Bandwidth**: Some free allowance
- **Auto-sleep**: Services can scale to zero

### Staying Free

1. **Scale to zero** when not in use (configured in workflow)
2. **Monitor usage** in Koyeb dashboard
3. **Use for lightweight services only**
4. **Don't run Minecraft server** (too resource-intensive)

---

## Security Best Practices

1. **Never commit tokens** - Always use GitHub Secrets
2. **Rotate tokens** - Generate new tokens periodically
3. **Minimal permissions** - Use tokens only for deployment
4. **Monitor access** - Check Koyeb audit logs
5. **Update dependencies** - Keep Docker images patched

---

## Alternative Deployments

For the actual Minecraft server, see:

- [FREE_TIER.md](FREE_TIER.md) - Free hosting options with adequate resources
- [DEPLOYMENT.md](DEPLOYMENT.md) - Detailed provider guides
- [MULTI_PROVIDER.md](MULTI_PROVIDER.md) - Multi-cloud strategy

**Recommended for Minecraft Server:**
- FalixNodes: 4GB RAM, free, easy setup
- Oracle Cloud: 24GB RAM, free, best performance
- Railway: Pay-as-you-go, managed infrastructure

---

## Summary

Koyeb provides excellent infrastructure for lightweight services but lacks the resources for a full Minecraft server. Use this deployment workflow for:

- Monitoring dashboards
- Status APIs
- Proxy services
- Discord/Telegram bots

For the actual game server, use FalixNodes, Oracle Cloud, or other providers with adequate RAM (4GB minimum for modded Minecraft).

---

## Quick Reference

**Generate Koyeb Token:**
Settings > API > Create API Key

**Add to GitHub:**
Settings > Secrets > Actions > New secret > KOYEB_API_TOKEN

**Trigger Deploy:**
Push to main branch OR Actions > Run workflow

**Monitor:**
https://app.koyeb.com

**Get Help:**
- [Koyeb Documentation](https://www.koyeb.com/docs)
- [Koyeb CLI Reference](https://www.koyeb.com/docs/cli)
- [GitHub Actions Docs](https://docs.github.com/en/actions)
