# Deployment to Render

This document explains how to deploy a Minecraft server with the Mirda Boss Mod to Render.

## Prerequisites

1. **Render Account**: Sign up at [render.com](https://render.com)
2. **GitHub Repository**: This repository should be connected to Render
3. **Render Deploy Hook**: Get from Render dashboard (for CI/CD)

## Quick Start

### Option 1: Deploy via Render Dashboard (Recommended)

1. **Create New Service**
   - Go to [Render Dashboard](https://dashboard.render.com)
   - Click "New +" → "Private Service"
   - Connect your GitHub repository
   - Render will auto-detect the `Dockerfile`

2. **Configure Service**
   - **Name**: `mirda-minecraft-server`
   - **Region**: Choose closest to your players
   - **Instance Type**: Standard (4GB RAM minimum)
   - **Docker Command**: (leave default, uses Dockerfile CMD)

3. **Add Environment Variables**
   In Render dashboard, add:
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

4. **Add Persistent Disk**
   - Name: `minecraft-data`
   - Mount Path: `/minecraft`
   - Size: 10GB minimum

5. **Deploy**
   - Click "Create Private Service"
   - Wait for build (~5-10 minutes)
   - Render will provide a connection address

### Option 2: Deploy via render.yaml (Infrastructure as Code)

This repository includes `render.yaml` for automatic setup:

1. **Push to GitHub**
   ```bash
   git push origin main
   ```

2. **Connect to Render**
   - Dashboard → "New +" → "Blueprint"
   - Connect repository
   - Render reads `render.yaml` and creates everything

3. **Automatic Setup**
   - Creates Private Service
   - Configures environment variables
   - Adds persistent disk
   - Deploys automatically

### Option 3: Automatic Deployment via GitHub Actions

1. **Get Render Deploy Hook**
   - Go to your service in Render Dashboard
   - Settings → Deploy Hook
   - Copy the URL (e.g., `https://api.render.com/deploy/srv-xxx?key=yyy`)

2. **Add to GitHub Secrets**
   - Repository → Settings → Secrets → Actions
   - New secret: `RENDER_DEPLOY_HOOK_URL`
   - Value: (paste deploy hook URL)

3. **Automatic Deployment**
   - Push to `main` → triggers deployment
   - Create tag → creates release AND deploys

## Render vs Railway

**Why Render?**
- ✅ More predictable pricing
- ✅ Better free tier (750 hours/month)
- ✅ Infrastructure as Code (render.yaml)
- ✅ Persistent disk included
- ✅ Better for long-running services

## CI/CD Pipeline

The GitHub Actions workflow (in `github-workflow-build.yml`) automatically:

### On Every Push
- ✅ Builds the mod
- ✅ Uploads artifacts
- ✅ Caches dependencies

### On Tag Push (e.g., `v1.0.0`)
- ✅ Everything above, plus:
- ✅ Creates GitHub Release
- ✅ Triggers Render deployment

### Creating a Release
```bash
git tag v1.0.0
git push origin v1.0.0
# → Build → Release → Deploy to Render
```

## Server Configuration

### Default Specifications
- **Memory**: 4GB RAM (configurable)
- **Minecraft**: 1.20.1
- **NeoForge**: 47.1.106
- **Port**: 25565
- **Max Players**: 20
- **Difficulty**: Hard
- **View Distance**: 10 chunks

### Performance Optimizations
The Dockerfile includes Aikar's JVM flags:
- G1GC garbage collector
- Optimized heap settings
- Reduced pause times
- Better memory management

## Cost Estimation

Render pricing (as of 2024):
- **Free Tier**: 750 hours/month (not suitable for 24/7 servers)
- **Starter**: $7/month (512MB RAM - too small)
- **Standard**: $25/month (4GB RAM) - **Recommended**
- **Pro**: $85/month (8GB RAM)

**Estimated costs:**
- 4GB server (Standard): $25/month
- Persistent disk (10GB): Included
- Includes 24/7 uptime

**Note**: Minecraft servers need at least 4GB RAM. Use Standard plan.

## Connecting to Your Server

### After Deployment

1. **Get Server Address**
   - Render Dashboard → Your service
   - Look for "Private Address" or external connection info
   - Format: `your-service.onrender.com:25565`

2. **Connect in Minecraft**
   - Open Minecraft 1.20.1 with NeoForge 47.1.106
   - Multiplayer → Add Server
   - Server Address: `your-service.onrender.com:25565`
   - Save and connect!

3. **Test Mirda**
   ```
   /summon_mirda_altar
   ```

## Managing Your Server

### View Logs
In Render Dashboard:
- Go to your service
- Click "Logs" tab
- Real-time streaming logs

### Restart Server
- Dashboard → Your service → "Manual Deploy" → "Clear build cache & deploy"

### Update Environment Variables
- Dashboard → Your service → "Environment"
- Edit variables
- Save (auto-redeploys)

### Access Shell
- Dashboard → Your service → "Shell"
- Opens terminal in container

## Server Administration

### Making Yourself OP

1. **Via Environment Variables**
   ```
   OPS=YourMinecraftUsername
   ```
   Redeploy after adding

2. **Via Shell** (Dashboard → Shell)
   ```bash
   screen -r  # Attach to Minecraft console
   op YourMinecraftUsername
   # Press Ctrl+A then D to detach
   ```

### Server Commands
Once OP:
- `/summon_mirda_altar` - Spawn Mirda's altar
- `/give @s mirdamod:bocow` - Get Mirda's weapon
- `/give @s mirdamod:altar_compass` - Get altar compass

## Troubleshooting

### Build Fails
- Check Dockerfile syntax
- Verify Java 17 is being used
- Review build logs in Render dashboard

### Server Won't Start
- Check memory allocation (minimum 4GB)
- Verify NeoForge version matches Minecraft
- Review server logs for errors

### Can't Connect
- Verify service is running (Dashboard → Status)
- Check port 25565 is exposed
- Wait 2-5 minutes for server startup
- Use correct address format: `host:25565`

### Out of Memory
- Upgrade to larger instance (8GB)
- Reduce `VIEW_DISTANCE` or `MAX_PLAYERS`
- Check for memory leaks in logs

### Slow Performance
- Upgrade instance type
- Reduce view distance
- Lower max players
- Check TPS in-game

## Advanced Configuration

### Custom Server Properties

Add to environment variables:
```
SERVER_PROPERTIES=server-port=25565\nonline-mode=true\ndifficulty=hard
```

Or modify in Dockerfile:
```dockerfile
RUN echo "your-property=value" >> server.properties
```

### Adding More Mods

1. **Create `extra_mods/` directory**
2. **Add mod JARs**
3. **Update Dockerfile**:
   ```dockerfile
   COPY extra_mods/*.jar /minecraft/mods/
   ```
4. **Push and redeploy**

### Using Custom World

1. **Create `world/` directory with world files**
2. **Update Dockerfile**:
   ```dockerfile
   COPY world/ /minecraft/world/
   ```
3. **Use persistent disk** to preserve between deploys

## Monitoring

### Render Dashboard
- CPU usage graphs
- Memory usage graphs
- Network traffic
- Deployment history
- Error logs

### In-Game Monitoring
- Monitor player count
- Watch for lag
- Check server console for errors

## Backup & Restore

### Manual Backup

Via Render Shell:
```bash
tar -czf world-backup.tar.gz world/
# Download via Render file browser or scp
```

### Automatic Backups

Render doesn't have built-in backups for disks. Options:
1. **Manual periodic backups** via shell
2. **S3 sync** script in cron
3. **External backup service**

### Persistent Disk

Render's persistent disk survives redeploys:
- World data saved
- Player data preserved
- Server settings maintained

## Security

- ✅ Online mode enabled (no cracked clients)
- ✅ Whitelist support via environment
- ✅ OP permissions for admin commands
- ✅ No sensitive data in repository
- ⚠️ Keep deploy hook URL secret

## Render-Specific Features

### Auto-Deploy
- Push to main → automatic deployment
- Uses `render.yaml` for config
- Zero-downtime deploys (optional)

### Health Checks
Built into Dockerfile:
```dockerfile
HEALTHCHECK --interval=30s CMD pgrep -f "java.*server.jar"
```

### Persistent Disks
- Survives redeploys
- 10GB default (configurable)
- Mounted at `/minecraft`

## Local Development

### Test Dockerfile Locally
```bash
docker build -t mirda-server .
docker run -p 25565:25565 -v minecraft-data:/minecraft mirda-server
```

### Test with Render CLI (if available)
```bash
render deploy
```

## Updating the Mod

1. **Make code changes**
2. **Commit and push**
   ```bash
   git commit -am "Update mod"
   git push
   ```
3. **Render auto-deploys** from main branch
4. **Or trigger manually** via deploy hook

## Production Checklist

Before going live:
- [ ] Set `MEMORY=4G` minimum
- [ ] Configure `OPS` with admin usernames
- [ ] Set `ONLINE_MODE=true`
- [ ] Adjust `MAX_PLAYERS` for your community
- [ ] Set meaningful `MOTD`
- [ ] Add persistent disk (10GB+)
- [ ] Test connection externally
- [ ] Verify Mirda spawns: `/summon_mirda_altar`
- [ ] Set up backups
- [ ] Monitor costs in Render dashboard

## Render Alternatives

If Render doesn't fit:
- **AWS ECS**: More control, complex setup
- **DigitalOcean App Platform**: Similar to Render
- **Google Cloud Run**: Not ideal for Minecraft
- **Self-hosting**: Your own hardware

## Support

- **Render Issues**: [Render Community](https://community.render.com)
- **Render Docs**: [render.com/docs](https://render.com/docs)
- **Mod Issues**: GitHub Issues
- **Minecraft**: [Minecraft Forums](https://www.minecraftforum.net/)

## render.yaml Reference

The included `render.yaml` defines:
```yaml
services:
  - type: pserv              # Private Service (non-HTTP)
    name: mirda-minecraft-server
    env: docker              # Use Dockerfile
    dockerfilePath: ./Dockerfile
    envVars: [...]           # All server config
    disk:
      name: minecraft-data   # Persistent storage
      mountPath: /minecraft
      sizeGB: 10
    plan: standard           # 4GB RAM
```

## License

Minecraft server hosting must comply with [Minecraft EULA](https://www.minecraft.net/en-us/eula).
