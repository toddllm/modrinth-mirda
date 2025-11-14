# Deployment to Railway

This document explains how to deploy a Minecraft server with the Mirda Boss Mod to Railway.

## Prerequisites

1. **Railway Account**: Sign up at [railway.app](https://railway.app)
2. **GitHub Repository**: This repository should be connected to Railway
3. **Railway Token**: Get your token from Railway dashboard

## Quick Start

### Option 1: Deploy via Railway Dashboard (Recommended)

1. **Create New Project**
   - Go to [Railway Dashboard](https://railway.app/dashboard)
   - Click "New Project"
   - Select "Deploy from GitHub repo"
   - Choose this repository

2. **Configure Environment Variables**
   In the Railway dashboard, add these variables:
   ```
   MEMORY=4G
   MINECRAFT_VERSION=1.20.1
   NEOFORGE_VERSION=47.1.106
   SERVER_PORT=25565
   ONLINE_MODE=true
   MOTD=Mirda Boss Mod Server - Prepare for Battle!
   DIFFICULTY=hard
   MAX_PLAYERS=20
   VIEW_DISTANCE=10
   ENABLE_COMMAND_BLOCK=true
   ```

3. **Deploy**
   - Railway will automatically detect the `Dockerfile`
   - Click "Deploy"
   - Wait for build to complete (~5-10 minutes)

4. **Connect to Server**
   - Once deployed, Railway will provide a public URL
   - Copy the domain (e.g., `your-app.up.railway.app`)
   - In Minecraft, use this address to connect

### Option 2: Deploy via Railway CLI

1. **Install Railway CLI**
   ```bash
   npm install -g @railway/cli
   ```

2. **Login to Railway**
   ```bash
   railway login
   ```

3. **Initialize Project**
   ```bash
   railway init
   ```

4. **Link to Existing Project (Optional)**
   ```bash
   railway link
   ```

5. **Deploy**
   ```bash
   railway up
   ```

### Option 3: Automatic Deployment via GitHub Actions

This repository includes a GitHub Actions workflow that automatically deploys to Railway.

1. **Add Railway Token to GitHub Secrets**
   - Go to your GitHub repository
   - Settings → Secrets and variables → Actions
   - Click "New repository secret"
   - Name: `RAILWAY_TOKEN`
   - Value: Your Railway token from [railway.app/account/tokens](https://railway.app/account/tokens)

2. **Automatic Deployment**
   - Push to `main` branch → Deploys to Railway
   - Create a tag (e.g., `v1.0.0`) → Creates release AND deploys

## CI/CD Pipeline

The GitHub Actions workflow (`.github/workflows/build.yml`) automatically:

### On Every Push
- ✅ Builds the mod with Gradle
- ✅ Runs tests
- ✅ Uploads build artifacts
- ✅ Caches dependencies for faster builds

### On Tag Push (e.g., `v1.0.0`)
- ✅ Everything above, plus:
- ✅ Creates GitHub Release
- ✅ Attaches JAR files to release
- ✅ Generates release notes
- ✅ Deploys to Railway (if configured)

### Creating a Release
```bash
# Create and push a tag
git tag v1.0.0
git push origin v1.0.0

# GitHub Actions will automatically:
# 1. Build the mod
# 2. Create a GitHub release
# 3. Deploy to Railway
```

## Railway Configuration

### Files

- **`Dockerfile`**: Builds the Minecraft server image with NeoForge and Mirda mod
- **`railway.json`**: Railway-specific configuration
- **`railway.toml`**: Alternative Railway configuration format

### Server Specifications

**Default Configuration:**
- **Memory**: 4GB RAM (configurable via `MEMORY` env var)
- **Minecraft**: 1.20.1
- **NeoForge**: 47.1.106
- **Port**: 25565
- **Max Players**: 20
- **Difficulty**: Hard
- **View Distance**: 10 chunks

### Optimizations

The Dockerfile includes Aikar's flags for optimal Minecraft server performance:
- G1GC garbage collector
- Optimized heap settings
- Reduced pause times
- Better memory management

## Cost Estimation

Railway pricing (as of 2024):
- **Hobby Plan**: $5/month includes $5 credit
- **Pro Plan**: $20/month includes $20 credit

**Estimated costs for Minecraft server:**
- 4GB RAM server: ~$10-15/month
- Includes 24/7 uptime
- Automatic scaling

**Note**: Minecraft servers can be resource-intensive. Monitor your usage in Railway dashboard.

## Connecting to Your Server

### After Deployment

1. **Get Server Address**
   - Railway dashboard → Your project → "Networking"
   - Copy the public domain (e.g., `mirda-server.up.railway.app`)

2. **Connect in Minecraft**
   - Open Minecraft 1.20.1 with NeoForge 47.1.106
   - Multiplayer → Add Server
   - Server Address: `your-domain.up.railway.app:25565`
   - Save and connect!

3. **Test Mirda**
   Once connected, try:
   ```
   /summon_mirda_altar
   ```

## Managing Your Server

### View Logs
```bash
railway logs
```

### Restart Server
```bash
railway restart
```

### Update Environment Variables
```bash
railway variables set MEMORY=6G
```

### SSH into Server (if needed)
```bash
railway run bash
```

## Server Administration

### Making Yourself OP

1. **Via Railway Dashboard**
   - Add to environment variables:
     ```
     OPS=YourMinecraftUsername
     ```
   - Redeploy

2. **Via Console** (if you have access)
   ```
   op YourMinecraftUsername
   ```

### Server Commands

Once OP, you can use:
- `/summon_mirda_altar` - Spawn Mirda's altar
- `/give @s mirdamod:bocow` - Get Mirda's weapon
- `/give @s mirdamod:altar_compass` - Get altar compass

## Troubleshooting

### Build Fails
- Check Gradle version compatibility
- Ensure Java 17 is being used
- Review build logs in Railway dashboard

### Server Won't Start
- Check memory allocation (minimum 2GB recommended)
- Verify NeoForge version matches Minecraft version
- Review Railway logs for errors

### Can't Connect
- Verify Railway networking is configured
- Check if server port is exposed (25565)
- Ensure server has finished starting (can take 2-5 minutes)

### Out of Memory
- Increase `MEMORY` environment variable
- Upgrade Railway plan for more resources
- Reduce `VIEW_DISTANCE` or `MAX_PLAYERS`

## Advanced Configuration

### Custom Server Properties

Create `server.properties.template` in the root:
```properties
server-port=${SERVER_PORT}
online-mode=${ONLINE_MODE}
difficulty=${DIFFICULTY}
max-players=${MAX_PLAYERS}
view-distance=${VIEW_DISTANCE}
# Add your custom properties
```

### Adding More Mods

1. Add mod JARs to `extra_mods/` directory
2. Update Dockerfile to copy them:
   ```dockerfile
   COPY extra_mods/*.jar /minecraft/mods/
   ```

### Using a Custom World

1. Create `world/` directory with your world files
2. Update Dockerfile:
   ```dockerfile
   COPY world/ /minecraft/world/
   ```

## Monitoring

### Railway Dashboard
- CPU usage
- Memory usage
- Network traffic
- Deployment history

### In-Game
- `/tps` - Check server performance (requires plugin)
- Monitor player count
- Watch for lag or errors

## Backup & Restore

### Manual Backup
```bash
railway run bash
tar -czf world-backup.tar.gz world/
railway run cat world-backup.tar.gz > local-backup.tar.gz
```

### Automatic Backups
Consider using Railway's built-in backups or external services.

## Security

- ✅ Online mode enabled by default (prevents cracked clients)
- ✅ Whitelist support via environment variables
- ✅ OP permissions required for admin commands
- ✅ No sensitive data in repository
- ⚠️ Keep Railway token secret (never commit to Git)

## Support

- **Railway Issues**: [Railway Discord](https://discord.gg/railway)
- **Mod Issues**: GitHub Issues in this repository
- **Minecraft Server**: [Minecraft Forums](https://www.minecraftforum.net/)

## Local Development

### Test Dockerfile Locally
```bash
docker build -t mirda-server .
docker run -p 25565:25565 mirda-server
```

### Test Deployment Locally
```bash
railway run
```

## Updating the Mod

1. Make changes to the mod code
2. Commit and push to GitHub
3. Railway automatically rebuilds and redeploys
4. Or manually trigger: `railway up`

## Production Checklist

Before going live:
- [ ] Set appropriate `MEMORY` (4GB minimum recommended)
- [ ] Configure `OPS` with admin usernames
- [ ] Set `ONLINE_MODE=true` for security
- [ ] Adjust `MAX_PLAYERS` for your community size
- [ ] Set meaningful `MOTD`
- [ ] Enable backups
- [ ] Monitor costs in Railway dashboard
- [ ] Test connection from external network
- [ ] Verify Mirda spawns correctly with `/summon_mirda_altar`

## Railway Alternatives

If Railway doesn't fit your needs, consider:
- **AWS EC2**: More control, but more setup
- **DigitalOcean**: Simple droplets for Minecraft
- **Minecraft server hosts**: Pre-configured solutions
- **Self-hosting**: Your own hardware

## License

This deployment configuration is provided as-is. Minecraft server hosting should comply with [Minecraft EULA](https://www.minecraft.net/en-us/eula).
