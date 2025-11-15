# Free Tier Hosting Guide

This guide helps you run the Mirda Minecraft server for **$0/month** using free-tier cloud providers.

## Important: Render Limitations

**Render Private Services are NOT publicly accessible from the internet.**

Render cannot be used for public Minecraft servers because:
- Private Services have no public IP address
- Web Services only expose HTTP/HTTPS (ports 80/443), not game ports
- Players cannot connect directly to a Render Private Service
- Internal networking only works between Render services

**Render is only suitable for:**
- Internal services (databases, APIs)
- VPN/tunnel-based access (Tailscale, WireGuard)
- Advanced users who configure their own networking layer

See [MULTI_PROVIDER.md](MULTI_PROVIDER.md) for the recommended multi-provider strategy.

---

## Truly Free Hosting Options

### Option 1: FalixNodes (Recommended for Easy Setup)

**Cost: $0/month forever**

**Resources:**
- 4 GB RAM (perfect for modded Minecraft)
- NeoForge support included
- DDoS protection
- FTP access

**Setup:**
1. Create account at [FalixNodes](https://falixnodes.net)
2. Create new Minecraft server
3. Select NeoForge 1.20.1 (version 47.1.106)
4. Upload Mirda mod JAR via web panel or FTP
5. Start server
6. Share IP:port with players

**Pros:**
- Purpose-built for Minecraft
- Easy mod installation
- No queue or waiting
- Free forever

**Cons:**
- Shared resources (occasional lag)
- Limited server locations

---

### Option 2: Oracle Cloud Always Free (Best Performance)

**Cost: $0/month forever**

**Resources:**
- 4 OCPUs (ARM Ampere A1)
- 24 GB RAM total
- 200 GB storage
- 24/7 uptime

**Setup:**
```bash
# SSH into Oracle VM
ssh ubuntu@your-vm-ip

# Install Java 17 (ARM64)
sudo apt update
sudo apt install openjdk-17-jdk-headless

# Download NeoForge installer
wget https://maven.neoforged.net/releases/net/neoforged/neoforge/47.1.106/neoforge-47.1.106-installer.jar

# Install server
java -jar neoforge-47.1.106-installer.jar --installServer

# Create mods folder
mkdir mods

# Upload Mirda mod JAR
scp mirdamod-1.0.0.jar ubuntu@your-vm-ip:~/mods/

# Accept EULA
echo "eula=true" > eula.txt

# Run server
java -Xmx4G -Xms2G -jar server.jar nogui
```

**Firewall Configuration:**
```bash
# Oracle Cloud console: Add ingress rule for TCP 25565
# Then on VM:
sudo iptables -I INPUT -p tcp --dport 25565 -j ACCEPT
sudo netfilter-persistent save
```

**Systemd Service (for auto-restart):**
```bash
sudo nano /etc/systemd/system/minecraft.service
```

```ini
[Unit]
Description=Minecraft Server
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/home/ubuntu
ExecStart=/usr/bin/java -Xmx4G -Xms2G -jar server.jar nogui
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl enable minecraft
sudo systemctl start minecraft
```

**Pros:**
- Massive resources (24GB RAM)
- Full VM control
- 24/7 operation
- Best performance of free options

**Cons:**
- Requires credit card (not charged)
- Complex initial setup
- ARM architecture (but Java works fine)

---

### Option 3: Aternos (Backup/Casual)

**Cost: $0/month**

**Resources:**
- Variable RAM
- 4 GB world size limit
- NeoForge support

**Setup:**
1. Create account at [Aternos](https://aternos.org)
2. Create new server
3. Select NeoForge 1.20.1
4. Upload Mirda mod via Files tab
5. Start server (wait in queue)
6. Connect via Aternos launcher

**Pros:**
- Zero setup complexity
- Automatic mod compatibility
- No cost ever

**Cons:**
- Queue wait times (5-15 minutes)
- Auto-shutdown after inactivity
- 4 GB world limit

**Best for:** Backup server or casual play

---

## Cost Comparison

| Provider | Monthly Cost | RAM | 24/7? | Setup Difficulty | Best For |
|----------|-------------|-----|-------|------------------|----------|
| **FalixNodes** | $0 | 4GB | Yes | Easy | Primary server |
| **Oracle Cloud** | $0 | 24GB | Yes | Hard | High performance |
| **Aternos** | $0 | Variable | No | Easy | Backup/casual |
| **Railway** | $1-5 | 8GB | Yes | Medium | Paid convenience |
| **Render** | N/A | N/A | N/A | N/A | **NOT usable** |
| **Koyeb** | $0 | 512MB | Yes | Easy | **Auxiliary only** |

---

## Koyeb Free Tier - For Auxiliary Services

**Koyeb's free tier is NOT suitable for Minecraft servers.**

**Resources:**
- 512 MB RAM
- 0.1 vCPU
- Auto-scaling to zero
- Git-based deployments

**Why Not for Minecraft:**
- 512MB RAM is insufficient (Mirda needs 4GB minimum)
- 0.1 vCPU cannot handle game tick processing
- Server would crash on startup or during gameplay

**What Koyeb IS Good For:**
- **Status dashboards** - Lightweight web app showing server status
- **API proxies** - Query server status via REST API
- **Discord/Telegram bots** - Report server status to chat platforms
- **Health monitors** - Track uptime across multiple servers
- **Web landing pages** - Static sites about your server

**Deployment:**
This repository includes a GitHub Actions workflow for Koyeb deployment:
- See [KOYEB_DEPLOYMENT.md](KOYEB_DEPLOYMENT.md) for setup instructions
- Workflow: `.github/workflows/deploy_koyeb.yml`
- Triggers on push to main branch
- Requires `KOYEB_API_TOKEN` secret

**Example Use Case - Status Bot:**
```python
# status_bot.py - Deploy to Koyeb for monitoring
from mcstatus import JavaServer
import discord

async def check_server():
    server = JavaServer.lookup("your-minecraft-server.com")
    status = server.status()
    return f"Players: {status.players.online}/{status.players.max}"
```

**Summary:** Use Koyeb for lightweight companion services that enhance your Minecraft server experience, not for hosting the game server itself.

---

## Using Cloudflare DNS

Combine providers with a single domain:

1. **Register domain** (optional, ~$10-15/year)
2. **Add to Cloudflare** (free)
3. **Point to active provider:**

```
Type: A
Name: play
IPv4: <provider-ip>
Proxy: OFF (required for game traffic)

Type: SRV
Name: _minecraft._tcp.play
Port: 25565
Target: play.yourdomain.com
```

**Switching providers:** Just update the A record IP

**Players always connect to:** `play.yourdomain.com`

---

## Watchdog Auto-Shutdown (Optional)

For providers that charge by usage (not free-tier), the watchdog script saves money:

```bash
#!/bin/bash
# watchdog.sh - Monitors player count and shuts down when idle

IDLE_MINUTES=${IDLE_MINUTES:-30}
CHECK_INTERVAL=${CHECK_INTERVAL:-60}

idle_time=0

while true; do
    sleep $CHECK_INTERVAL

    # Check player count
    player_count=$(echo "list" | screen -S minecraft -p 0 -X stuff "$(printf '\r')" 2>/dev/null | grep -oP '\d+(?= players)')

    if [ "$player_count" -eq 0 ]; then
        idle_time=$((idle_time + CHECK_INTERVAL))
        if [ $idle_time -ge $((IDLE_MINUTES * 60)) ]; then
            echo "Idle timeout. Shutting down..."
            screen -S minecraft -X quit
            exit 0
        fi
    else
        idle_time=0
    fi
done
```

**Useful for:**
- Railway (pay-per-use after trial)
- Self-hosted VPS with metered billing
- NOT needed for FalixNodes, Oracle Cloud, or Aternos (always free)

---

## Recommended Strategy

**Primary Server: FalixNodes**
- Easy setup, no queue
- 4GB RAM handles Mirda mod
- Free forever
- Good for regular play

**Backup Server: Aternos**
- Use when FalixNodes is down
- Accept queue wait time
- Free, no setup

**Migration Path: Oracle Cloud**
- Best performance available
- 24/7 with no interruptions
- Worth the initial setup effort

**Optional: Custom Domain via Cloudflare**
- Professional look: `play.mirda.games`
- Easy provider switching
- $10-15/year for domain

---

## What About Render?

The `render.yaml` and `watchdog.sh` files remain in this repository for:

1. **Internal services** - Backend APIs that don't need public access
2. **VPN setups** - If you configure Tailscale/WireGuard tunneling
3. **Advanced users** - Who understand networking limitations
4. **Reference** - Docker configuration is still useful for other providers

**DO NOT** expect to deploy to Render and have players connect publicly. It will not work.

---

## Quick Start

1. **Choose provider:**
   - Easy setup → FalixNodes
   - Best performance → Oracle Cloud
   - Just testing → Aternos

2. **Set up server:**
   - Install NeoForge 1.20.1 (47.1.106)
   - Upload Mirda mod JAR
   - Configure server.properties
   - Open port 25565 (if needed)

3. **Share with players:**
   - Direct IP:port, or
   - Custom domain via Cloudflare

4. **Cost: $0/month**

---

## Further Reading

- [MULTI_PROVIDER.md](MULTI_PROVIDER.md) - Complete multi-provider strategy
- [DEPLOYMENT.md](DEPLOYMENT.md) - Detailed deployment guides
- [CONNECTING.md](CONNECTING.md) - Player connection instructions
- [Oracle Cloud Free Tier](https://www.oracle.com/cloud/free/)
- [FalixNodes](https://falixnodes.net)
- [Aternos](https://aternos.org)
