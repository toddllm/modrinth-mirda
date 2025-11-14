# Free Tier Optimization Guide

This guide helps you run the Mirda Minecraft server on Render while staying within free tier limits (750 hours/month).

## Understanding Render Free Tier

**Free Tier Limits:**
- 750 hours/month of service runtime
- 512MB RAM (NOT enough for Minecraft)
- Services spin down after 15 minutes of inactivity (web services only)

**Reality for Minecraft:**
- Minecraft needs minimum 2-4GB RAM
- Private services (required for Minecraft) don't auto-suspend
- **You'll need a paid plan, BUT can minimize costs**

## Cost Optimization Strategy

### Option 1: Auto-Shutdown When Idle (Included)

The server now includes a **watchdog script** that automatically shuts down when no players are online.

**How it works:**
1. Monitors player count every 60 seconds
2. If 0 players for 30 minutes → auto-shutdown
3. Server stops, Render stops billing
4. Manually restart when you want to play

**Configuration (via environment variables):**
```yaml
IDLE_MINUTES: 30        # Shutdown after 30 min of no players
CHECK_INTERVAL: 60      # Check every 60 seconds
```

**Example Cost Savings:**
- Play 4 hours/day = ~120 hours/month
- Standard plan ($25/month) = $0.034/hour
- Monthly cost: ~$4 instead of $25! ✅

### Option 2: Manual Start/Stop

**Disable auto-deploy** to prevent automatic restarts:
```yaml
autoDeploy: false  # Already set in render.yaml
```

**Start server manually:**
1. Go to Render Dashboard
2. Your service → "Manual Deploy"
3. Deploy latest
4. Server starts, play session begins

**Stop server when done:**
1. Dashboard → Shell
2. Run: `pkill -f "java.*server.jar"`
3. Server stops, billing stops

### Option 3: Scheduled Server Times

Add cron-based startup for specific hours:

```dockerfile
# Add to Dockerfile
RUN echo "0 18 * * * /start-server.sh" >> /etc/crontab
RUN echo "0 23 * * * pkill -f 'java.*server.jar'" >> /etc/crontab
```

Server runs:
- 6 PM to 11 PM daily = 5 hours/day
- ~150 hours/month
- Cost: ~$5/month

## Monitoring Usage

### Render Dashboard
- View service runtime hours
- Check current billing cycle usage
- Monitor costs in real-time

### Command to Check Server Status
```bash
# Via Render Shell
pgrep -f "java.*server.jar" && echo "Server running" || echo "Server stopped"
```

## Recommended Setup for Budget

**Best approach:**
1. ✅ Use auto-shutdown (included)
2. ✅ Set `IDLE_MINUTES=30` (or lower)
3. ✅ Disable auto-deploy (`autoDeploy: false`)
4. ✅ Manual start when you want to play
5. ✅ Let watchdog stop it when done

**Monthly cost estimate:**
- Play 3-4 hours/day
- ~100-120 hours/month
- **~$3-4/month** instead of $25

## Alternative: Free Tier Workarounds

### Use Render Free Web Service + External Server

**Not recommended** but possible:
1. Host website on Render free tier
2. Run actual Minecraft server elsewhere (free):
   - Oracle Cloud (always free tier, 4GB ARM instance)
   - Google Cloud (free $300 credit)
   - AWS Free Tier (12 months, t2.micro)

### Ngrok Tunnel (Local Hosting)

**Free option:**
1. Run server on your PC
2. Use ngrok for public access
3. Only run when you're playing
4. $0/month

## Free Tier Comparison

| Platform | Free Tier | Minecraft Viable? | Notes |
|----------|-----------|-------------------|-------|
| **Render** | 750hrs/month, 512MB | ❌ Need paid ($25/mo) | But can optimize to $3-4/mo |
| **Oracle Cloud** | Always free, 4GB ARM | ✅ Yes | Best free option |
| **Railway** | $5 credit/mo | ⚠️ Limited | ~50-80 hrs/month |
| **Fly.io** | 3 VMs free | ⚠️ Limited RAM | 256MB each |
| **AWS** | 12 months free | ✅ t2.micro | After 12mo: paid |

## Recommended: Oracle Cloud Free Tier

For **truly free** 24/7 Minecraft hosting:

1. **Oracle Cloud Always Free:**
   - ARM-based Ampere A1 instance
   - 4 OCPUs, 24GB RAM (can use 4GB for Minecraft)
   - Always free, no time limit
   - No credit card charges

2. **Setup:**
   ```bash
   # SSH to Oracle instance
   # Install Docker
   # Use same Dockerfile from this repo
   docker run -p 25565:25565 -v minecraft-data:/minecraft mirda-server
   ```

3. **Cost:** $0/month forever ✅

## Summary

**For Render:**
- ✅ Use auto-shutdown watchdog (included)
- ✅ Set low idle timeout (15-30 min)
- ✅ Manual deploy when needed
- ✅ Cost: ~$3-5/month for casual play

**For Free 24/7:**
- ✅ Use Oracle Cloud Always Free
- ✅ Same Docker setup
- ✅ Cost: $0/month

## Configuration

The watchdog is already configured in:
- `watchdog.sh` - Auto-shutdown script
- `Dockerfile` - Runs watchdog alongside server
- `render.yaml` - Environment variables for tuning

**To adjust idle timeout:**
```yaml
# In render.yaml
IDLE_MINUTES: 15  # Shutdown after 15 min (more aggressive)
```

**To disable watchdog:**
```yaml
IDLE_MINUTES: 0   # Disables auto-shutdown
```

## Questions?

- **Render billing**: [Render Pricing](https://render.com/pricing)
- **Oracle free tier**: [Oracle Cloud Free Tier](https://www.oracle.com/cloud/free/)
- **Mod issues**: GitHub Issues
