# Multi-Provider Free Hosting Strategy

This guide explains how to host your Mirda Boss Mod Minecraft server for free or minimal cost using multiple cloud providers combined with Cloudflare DNS.

## Overview

Rather than relying on a single expensive host, this strategy combines multiple free-tier services:

- **Primary Server**: FalixNodes (4GB RAM, free) or Oracle Cloud Always Free (24GB RAM)
- **Backup Server**: Aternos (4GB world limit, free with queue)
- **Auxiliary Services**: GCP e2-micro or Koyeb for status pages/APIs
- **DNS Management**: Cloudflare for routing and failover

**Total Cost**: $0/month (or $1/month if using Railway as backup)

---

## Free-Tier Provider Comparison

### FalixNodes (Recommended Primary)

**Resources:**
- 4 GB RAM
- Mod support (NeoForge compatible)
- Custom JAR uploads
- Free forever

**Pros:**
- Purpose-built for Minecraft hosting
- Easy mod installation
- No time limits or queue system
- DDoS protection included
- FTP access for file management

**Cons:**
- May have occasional downtime
- Limited CPU (shared resources)
- Server location options may be limited

**Best For:** Primary server for regular play sessions

**Setup:**
1. Create account at [falixnodes.net](https://falixnodes.net)
2. Create new server, select Minecraft Java
3. Choose NeoForge 1.20.1 (47.1.106)
4. Upload Mirda mod JAR via FTP or web panel
5. Start server and note the IP:port

---

### Oracle Cloud Always Free

**Resources:**
- Up to 4 OCPUs (ARM-based)
- 24 GB RAM total
- 200 GB block storage
- 24/7 operation, truly free forever

**Pros:**
- Massive resources for free
- Full VM control (install anything)
- Runs 24/7 without time limits
- No queue or auto-shutdown
- Best performance of free options

**Cons:**
- Requires credit card for signup (not charged)
- Complex initial setup (VM provisioning)
- Must configure firewall rules manually
- ARM architecture requires compatible Java

**Best For:** 24/7 dedicated server with full control

**Setup:**
1. Sign up at [cloud.oracle.com](https://cloud.oracle.com) (credit card required, never charged)
2. Create Always Free VM (Ampere A1, 4 OCPU, 24GB RAM)
3. Configure security list to allow TCP port 25565
4. SSH into VM and install Java 17 (ARM64)
5. Download NeoForge installer and run
6. Upload Mirda mod JAR to mods folder
7. Create systemd service for auto-restart

**Firewall Configuration:**
```bash
# Allow Minecraft port
sudo iptables -I INPUT -p tcp --dport 25565 -j ACCEPT
sudo netfilter-persistent save
```

---

### Aternos (Backup/Casual)

**Resources:**
- 4 GB world size limit
- Supports mods (including NeoForge)
- Free with queue system

**Pros:**
- Zero setup complexity
- Automatic mod installation
- No account limits
- Good for casual/backup use

**Cons:**
- Queue wait times (5-15 minutes)
- Auto-shutdown after inactivity
- 4 GB world size limit
- Slower performance than dedicated hosting

**Best For:** Backup server or casual play when primary is unavailable

**Setup:**
1. Create account at [aternos.org](https://aternos.org)
2. Create server, select NeoForge 1.20.1
3. Upload Mirda mod via Files tab
4. Start server (wait in queue)
5. Share with friends via Aternos launcher

---

### Google Cloud e2-micro (Auxiliary)

**Resources:**
- 1 vCPU
- ~1 GB RAM
- 30 GB storage
- Free forever (one per account)

**Pros:**
- Reliable Google infrastructure
- Good for lightweight tasks
- Static IP available
- Runs 24/7

**Cons:**
- Too weak for Minecraft server
- Complex setup
- Limited RAM

**Best For:** Status page, backup scripts, monitoring, webhooks

**Use Cases:**
- Host a simple status page showing which provider is active
- Run backup scripts that sync world data between providers
- Monitor server health and send notifications
- Lightweight API for server control

---

### Koyeb (Control Plane)

**Resources:**
- 512 MB RAM
- 0.1 vCPU
- Free instance available

**Pros:**
- Easy deployment (Docker support)
- Good for web services
- Auto-scaling
- Free SSL

**Cons:**
- Far too weak for Minecraft
- Limited resources
- Sleep after inactivity

**Best For:** Status API, control panel web app, Discord bot

---

### Railway (Optional Paid)

**Resources:**
- $5 free trial credit (consumed)
- $1/month minimum after trial
- 8 GB RAM available

**Pros:**
- Easy deployment from GitHub
- Good performance
- Simple scaling

**Cons:**
- Costs money after trial
- Trial may be consumed quickly
- Not truly free long-term

**Best For:** Users willing to pay $1-5/month for convenience

**Note:** Railway configuration files (`railway.json`, `railway.toml`) are included in this repo for users who choose this option.

---

## Cloudflare DNS Setup

Cloudflare provides free DNS management to route your domain to whichever provider is currently active.

### Benefits

- **Single canonical domain** for all players (e.g., `play.mirda.example.com`)
- **Easy failover** - switch providers by updating DNS
- **SRV records** for custom port mapping
- **DDoS protection** (proxy mode for web services)
- **Free SSL** for web dashboards

### Setup Steps

1. **Register domain** (or use existing one)
2. **Add to Cloudflare** (free plan)
3. **Configure nameservers** at registrar
4. **Create DNS records** (see below)

### DNS Record Configuration

#### A Record (Direct IP)

Point your subdomain to the active server's IP:

```
Type: A
Name: play
IPv4: <server-ip>
Proxy: OFF (DNS only - required for game traffic)
TTL: Auto
```

#### SRV Record (Custom Port)

Minecraft clients use SRV records to find the correct port:

```
Type: SRV
Name: _minecraft._tcp.play
Service: _minecraft
Protocol: TCP
TTL: Auto
Priority: 0
Weight: 5
Port: 25565
Target: play.example.com
```

**Example for FalixNodes:**
```
Type: A
Name: play
IPv4: 123.45.67.89  (FalixNodes IP)
Proxy: OFF

Type: SRV
Name: _minecraft._tcp.play
Target: play.example.com
Port: 12345  (FalixNodes assigned port)
```

**Switching Providers:**

To switch from FalixNodes to Oracle Cloud:
1. Update A record IP to Oracle Cloud VM IP
2. Update SRV record port if different
3. Players continue using `play.example.com`
4. DNS propagates in minutes

### Player Connection

Players always connect to:
```
play.example.com
```

Minecraft automatically:
1. Queries SRV record for `_minecraft._tcp.play.example.com`
2. Gets actual IP and port
3. Connects to correct server

No need for players to know which provider is active.

---

## Render - Internal/VPN Only

**Important:** Render Private Services are NOT publicly accessible.

Render can only be used if you:
- Set up a VPN (Tailscale, WireGuard) between players and Render
- Use tunneling services (ngrok, Cloudflare Tunnel)
- Only need internal service-to-service communication

**Why Render Doesn't Work for Public Gaming:**
- Private Services have no public IP
- Web Services only expose HTTP/HTTPS (port 80/443)
- No raw TCP port exposure to internet
- Only accessible within Render's private network

**When to Use Render:**
- Internal microservices
- Backend APIs accessed by your other Render services
- Development/testing with VPN access
- Advanced users comfortable with network tunneling

The `render.yaml` file is kept in this repository for advanced users who understand these limitations.

---

## Recommended Setup

### Budget: $0/month

**Primary Server: FalixNodes**
- Easy setup, good for modded servers
- 4 GB RAM handles Mirda mod well
- Players connect via Cloudflare DNS

**Backup Server: Aternos**
- Use when FalixNodes has issues
- Queue system acceptable for backup
- Same Cloudflare domain, just update DNS

**Optional: Oracle Cloud**
- Best performance if you want 24/7
- More setup effort but worth it
- Use as primary once configured

**Auxiliary: GCP e2-micro**
- Host status page showing server state
- Run backup scripts
- Monitor and alert on issues

### Workflow

1. **Normal Operation:**
   - FalixNodes runs primary server
   - DNS points `play.example.com` to FalixNodes
   - Players connect normally

2. **FalixNodes Down:**
   - Start Aternos server (wait in queue)
   - Update Cloudflare DNS to Aternos IP
   - Players reconnect to same domain
   - World may be from last backup

3. **Migration to Oracle Cloud:**
   - Set up Oracle VM (one-time effort)
   - Upload world data
   - Update DNS to Oracle Cloud IP
   - Better performance, 24/7 uptime

---

## World Data Synchronization

Keep world data synced between providers:

### Manual Backup

1. Download world from FalixNodes (FTP)
2. Upload to Aternos (Files tab)
3. Upload to Oracle Cloud (SCP/SFTP)

### Automated (Using GCP e2-micro)

```bash
#!/bin/bash
# backup-worlds.sh

# Download from FalixNodes
lftp -c "open sftp://falix; mirror /world /backup/world"

# Upload to Oracle Cloud
scp -r /backup/world oracle-vm:/minecraft/

# Compress and store
tar -czf /backup/world-$(date +%Y%m%d).tar.gz /backup/world
```

Run via cron every night.

---

## Cost Summary

| Provider | Monthly Cost | Resources | Use Case |
|----------|-------------|-----------|----------|
| FalixNodes | $0 | 4GB RAM | Primary server |
| Oracle Cloud | $0 | 24GB RAM | Primary (advanced) |
| Aternos | $0 | 4GB world | Backup server |
| GCP e2-micro | $0 | 1GB RAM | Status/monitoring |
| Koyeb | $0 | 512MB RAM | API/bot hosting |
| Cloudflare | $0 | DNS | Domain management |
| **Total** | **$0** | - | Full hosting stack |

Optional:
- Railway: $1-5/month after trial
- Custom domain: $10-15/year

---

## Getting Started

1. **Create FalixNodes account** and set up NeoForge 1.20.1 server
2. **Upload Mirda mod JAR** to the server
3. **Register domain** (or use free subdomain from services like FreeDNS)
4. **Add domain to Cloudflare** (free tier)
5. **Configure DNS** to point to FalixNodes
6. **Share `play.yourdomain.com`** with players
7. **Set up Aternos** as backup option
8. **Optionally** set up Oracle Cloud for 24/7 performance

Players always connect to your canonical domain, and you control which provider serves them via DNS.

---

## Troubleshooting

### DNS Not Resolving
- Wait 5-10 minutes for propagation
- Check Cloudflare dashboard for errors
- Ensure proxy is OFF for game traffic

### Can't Connect to Server
- Verify server is running on provider
- Check firewall rules (Oracle Cloud)
- Confirm correct IP in DNS record
- Test with direct IP:port first

### World Data Lost
- Always backup before switching providers
- Use automated backup scripts
- Keep world compressed archives

### Queue Times (Aternos)
- Normal for free tier
- Start server before play session
- Consider Oracle Cloud for no queue

---

## Next Steps

1. Read [DEPLOYMENT.md](DEPLOYMENT.md) for detailed provider setup
2. Check [CONNECTING.md](CONNECTING.md) for player connection guide
3. Review [FREE_TIER.md](FREE_TIER.md) for cost optimization tips
4. Set up your preferred provider combination
5. Configure Cloudflare DNS
6. Start playing with Mirda!
