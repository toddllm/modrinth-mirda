# Server Deployment Guide

This document explains how to deploy a Minecraft server with the Mirda Boss Mod.

## Quick Start

**Recommended for most users:** Use FalixNodes or Oracle Cloud Always Free for **$0/month** hosting.

**Important:** Render Private Services are NOT publicly accessible and CANNOT be used for public game servers. See the "Advanced: Render (Internal Only)" section at the end if you need internal-only hosting.

---

## Option 1: FalixNodes (Recommended - Easy Setup)

**Cost:** $0/month forever
**Difficulty:** Easy
**Best for:** Users who want quick setup with mod support

### Setup Steps

1. **Create Account**
   - Go to [falixnodes.net](https://falixnodes.net)
   - Sign up for free account

2. **Create Server**
   - Dashboard → Create Server
   - Select "Minecraft Java"
   - Choose a server location close to your players

3. **Configure Server**
   - Server Type: NeoForge
   - Minecraft Version: 1.20.1
   - NeoForge Version: 47.1.106

4. **Upload Mirda Mod**
   - Go to File Manager or FTP
   - Navigate to `/mods` folder
   - Upload `mirdamod-1.0.0.jar`

5. **Start Server**
   - Click "Start"
   - Wait for startup (~1-2 minutes)
   - Note the Server Address (e.g., `node.falixsrv.me:12345`)

6. **Share with Players**
   ```
   node.falixsrv.me:12345
   ```

### FalixNodes Features

- 4GB RAM (sufficient for Mirda mod)
- NeoForge pre-installed
- Web-based file manager
- FTP access for larger uploads
- DDoS protection included
- Free forever

---

## Option 2: Oracle Cloud Always Free (Best Performance)

**Cost:** $0/month forever
**Difficulty:** Advanced
**Best for:** Users who want maximum performance and 24/7 uptime

### Prerequisites

- Oracle Cloud account (requires credit card, never charged)
- Basic Linux command line knowledge
- SSH client

### Setup Steps

1. **Create Oracle Cloud Account**
   - Go to [cloud.oracle.com](https://cloud.oracle.com)
   - Sign up (credit card required for verification only)
   - Select "Always Free" tier

2. **Create VM Instance**
   - Compute → Instances → Create Instance
   - Image: Oracle Linux or Ubuntu
   - Shape: VM.Standard.A1.Flex (Ampere ARM)
   - OCPU: 4, Memory: 24GB
   - Configure SSH keys

3. **Configure Security List**
   - Networking → Virtual Cloud Networks
   - Select your VCN → Security Lists
   - Add Ingress Rule:
     - Source CIDR: `0.0.0.0/0`
     - Protocol: TCP
     - Port: 25565

4. **Connect via SSH**
   ```bash
   ssh opc@your-vm-ip
   # or
   ssh ubuntu@your-vm-ip
   ```

5. **Install Java 17**
   ```bash
   sudo apt update  # Ubuntu
   sudo apt install openjdk-17-jdk-headless

   # or for Oracle Linux:
   sudo yum install java-17-openjdk-headless
   ```

6. **Install NeoForge Server**
   ```bash
   mkdir minecraft && cd minecraft

   # Download NeoForge installer
   wget https://maven.neoforged.net/releases/net/neoforged/neoforge/47.1.106/neoforge-47.1.106-installer.jar

   # Run installer
   java -jar neoforge-47.1.106-installer.jar --installServer

   # Accept EULA
   echo "eula=true" > eula.txt
   ```

7. **Upload Mirda Mod**
   ```bash
   mkdir mods
   # From your local machine:
   scp mirdamod-1.0.0.jar opc@your-vm-ip:~/minecraft/mods/
   ```

8. **Configure Firewall**
   ```bash
   sudo iptables -I INPUT -p tcp --dport 25565 -j ACCEPT
   sudo netfilter-persistent save  # Ubuntu
   # or
   sudo firewall-cmd --permanent --add-port=25565/tcp  # Oracle Linux
   sudo firewall-cmd --reload
   ```

9. **Start Server**
   ```bash
   java -Xmx4G -Xms2G -jar server.jar nogui
   ```

10. **Create Systemd Service** (for auto-restart)
    ```bash
    sudo nano /etc/systemd/system/minecraft.service
    ```

    ```ini
    [Unit]
    Description=Minecraft Mirda Server
    After=network.target

    [Service]
    User=opc
    WorkingDirectory=/home/opc/minecraft
    ExecStart=/usr/bin/java -Xmx4G -Xms2G -jar server.jar nogui
    Restart=on-failure
    RestartSec=10

    [Install]
    WantedBy=multi-user.target
    ```

    ```bash
    sudo systemctl daemon-reload
    sudo systemctl enable minecraft
    sudo systemctl start minecraft
    ```

11. **Share with Players**
    ```
    your-vm-public-ip:25565
    ```

### Oracle Cloud Features

- 24GB RAM available
- Full root access
- 24/7 operation
- No time limits
- Best free performance

---

## Option 3: Aternos (Backup/Casual)

**Cost:** $0/month
**Difficulty:** Very Easy
**Best for:** Backup server or casual play sessions

### Setup Steps

1. **Create Account**
   - Go to [aternos.org](https://aternos.org)
   - Sign up for free

2. **Create Server**
   - Create new server
   - Select "Software" → NeoForge
   - Version: 1.20.1

3. **Upload Mirda Mod**
   - Go to "Files" tab
   - Navigate to `mods` folder
   - Upload `mirdamod-1.0.0.jar`

4. **Start Server**
   - Click "Start" (wait in queue: 5-15 minutes)
   - Once running, note address: `YourServer.aternos.me`

5. **Share with Players**
   ```
   YourServer.aternos.me
   ```

### Aternos Limitations

- Queue wait times (5-15 minutes)
- Auto-shutdown after inactivity
- 4GB world size limit
- Best used as backup

---

## Option 4: Railway (Paid After Trial)

**Cost:** $5 free trial credit, then $1-5/month
**Difficulty:** Medium
**Best for:** Users willing to pay for convenience

### Prerequisites

- GitHub account
- Railway account connected to GitHub

### Setup Steps

1. **Connect Repository**
   - Go to [railway.app](https://railway.app)
   - New Project → Deploy from GitHub
   - Select this repository

2. **Configure Service**
   Railway auto-detects the `Dockerfile` and uses included configs:
   - `railway.json` - Build configuration
   - `railway.toml` - Deployment settings

3. **Set Environment Variables**
   ```
   MEMORY=4G
   MINECRAFT_VERSION=1.20.1
   NEOFORGE_VERSION=47.1.106
   SERVER_PORT=25565
   ONLINE_MODE=true
   MAX_PLAYERS=20
   ```

4. **Deploy**
   - Railway builds and deploys automatically
   - Provides public TCP endpoint
   - Format: `your-app.up.railway.app:25565`

5. **Share with Players**
   ```
   your-app.up.railway.app:25565
   ```

### Railway Features

- Easy GitHub integration
- Automatic deployments
- Public TCP support (unlike Render!)
- Good performance
- Pay-per-use after trial

---

## Using Cloudflare DNS (Optional)

Configure a custom domain for your server:

1. **Register Domain** ($10-15/year)
2. **Add to Cloudflare** (free)
3. **Create DNS Records:**

   **A Record:**
   ```
   Type: A
   Name: play
   IPv4: <your-server-ip>
   Proxy: OFF (required for game traffic)
   ```

   **SRV Record:**
   ```
   Type: SRV
   Name: _minecraft._tcp.play
   Service: _minecraft
   Protocol: TCP
   Port: 25565
   Target: play.yourdomain.com
   ```

4. **Players Connect To:**
   ```
   play.yourdomain.com
   ```

Benefits:
- Professional domain name
- Easy provider switching (just update IP)
- Automatic port routing via SRV

See [MULTI_PROVIDER.md](MULTI_PROVIDER.md) for complete DNS setup.

---

## CI/CD Pipeline (GitHub Actions)

The repository includes automated build and deployment:

### Configuration Files

- `github-workflow-build.yml` - GitHub Actions workflow template
- `railway.json` - Railway build config
- `railway.toml` - Railway environment settings

### Setup GitHub Actions

1. **Copy Workflow File**
   ```bash
   mkdir -p .github/workflows
   cp github-workflow-build.yml .github/workflows/build.yml
   ```

2. **Add Secrets** (if using Railway)
   - Repository → Settings → Secrets → Actions
   - Add `RAILWAY_TOKEN` from Railway dashboard

3. **Automatic Builds**
   - Push to main → builds mod
   - Tag with version → creates GitHub release
   - Optional Railway deployment

### Creating Releases

```bash
git tag v1.0.0
git push origin v1.0.0
# → Builds mod → Creates GitHub release with JAR
```

---

## Server Administration

### Essential Commands (after becoming OP)

```bash
/summon_mirda_altar          # Spawn Mirda's altar
/give @s mirdamod:bocow      # Get Mirda's weapon
/give @s mirdamod:altar_compass  # Get altar compass
/give @s mirdamod:crystal_heart  # Get crystal heart
/op PlayerName               # Make player OP
/whitelist add PlayerName    # Add to whitelist
```

### Becoming OP

**FalixNodes:** Panel → Console → type `op YourUsername`

**Oracle Cloud:**
```bash
# Attach to screen/tmux
screen -r minecraft
op YourUsername
# Ctrl+A, D to detach
```

**Aternos:** Console tab → type `op YourUsername`

**Railway:** Deploy with `OPS=YourUsername` environment variable

---

## Performance Optimization

### Server Properties

Edit `server.properties`:
```properties
view-distance=10        # Lower for better performance (6-8)
max-players=20          # Adjust to your needs
difficulty=hard         # Boss fights are harder
enable-command-block=true
```

### JVM Flags (Aikar's Flags)

The Dockerfile includes optimized flags:
```bash
java -Xmx4G -Xms2G \
  -XX:+UseG1GC \
  -XX:+ParallelRefProcEnabled \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UnlockExperimentalVMOptions \
  -XX:+DisableExplicitGC \
  -XX:+AlwaysPreTouch \
  -jar server.jar nogui
```

---

## Cost Summary

| Provider | Monthly Cost | RAM | Setup | Public Access |
|----------|-------------|-----|-------|---------------|
| FalixNodes | $0 | 4GB | Easy | Yes |
| Oracle Cloud | $0 | 24GB | Hard | Yes |
| Aternos | $0 | Variable | Easy | Yes (with queue) |
| Railway | $1-5 | 8GB | Medium | Yes |
| **Render** | N/A | N/A | N/A | **NO (Internal only)** |

---

## Advanced: Render (Internal/VPN Only)

**WARNING: Render Private Services are NOT publicly accessible from the internet.**

Render is only suitable if you:
- Need internal service-to-service communication
- Will configure VPN/tunnel (Tailscale, WireGuard, ngrok)
- Understand that players CANNOT connect directly

### Why Render Doesn't Work for Public Gaming

1. **Private Services** have no public IP
2. **Web Services** only expose HTTP/HTTPS (ports 80/443)
3. No raw TCP port exposure to internet
4. Internal hostname only reachable within Render network

### If You Must Use Render

1. **Deploy as Private Service** (using `render.yaml`)
2. **Install VPN on Render Service:**
   - Add Tailscale to Dockerfile
   - Configure Tailscale network key
3. **All Players Install VPN:**
   - Each player installs Tailscale
   - Joins your Tailscale network
4. **Connect via VPN IP:**
   - Use Tailscale IP, not public hostname
   - Only works for players on your VPN

**This is NOT recommended.** FalixNodes or Oracle Cloud are free and much easier.

### Render Configuration Files

The repository includes for advanced users:
- `render.yaml` - Infrastructure as Code
- `watchdog.sh` - Auto-shutdown for cost savings
- `Dockerfile` - Container configuration

These are useful for:
- Reference Docker setup
- Internal development environments
- Users with VPN infrastructure
- Cost optimization ideas

---

## Backup Strategies

### Manual Backup

```bash
# FalixNodes: Use File Manager to download world folder
# Oracle Cloud:
tar -czf world-backup-$(date +%Y%m%d).tar.gz world/
# Download via SCP

# Aternos: Files → Download world
```

### Automated Backups (Oracle Cloud)

```bash
# Add to crontab
0 4 * * * tar -czf /backups/world-$(date +\%Y\%m\%d).tar.gz /home/opc/minecraft/world/
```

### Sync Between Providers

Keep world data synced for failover:
1. Download from primary provider
2. Upload to backup provider
3. Update DNS to switch

---

## Troubleshooting

### Server Won't Start

- **Check memory:** Minimum 2GB, recommended 4GB
- **Verify Java version:** Must be Java 17
- **Check NeoForge:** Version 47.1.106 for MC 1.20.1
- **Review logs:** Look for errors in console

### Players Can't Connect

- **Verify server running:** Check process/panel status
- **Firewall rules:** Ensure port 25565 is open
- **Correct address:** Include port if non-standard
- **Version match:** All players need MC 1.20.1 + NeoForge 47.1.106

### Mod Not Loading

- **Check mods folder:** JAR must be in correct location
- **File permissions:** Ensure readable
- **NeoForge version:** Must match server and client
- **No duplicate mods:** Remove old versions

---

## Next Steps

1. **Choose provider** based on your needs
2. **Deploy server** using instructions above
3. **Upload Mirda mod** JAR
4. **Configure server settings**
5. **Test with `/summon_mirda_altar`**
6. **Share server address** with players
7. **Optional:** Configure Cloudflare DNS

See also:
- [CONNECTING.md](CONNECTING.md) - Player connection guide
- [MULTI_PROVIDER.md](MULTI_PROVIDER.md) - Multi-provider strategy
- [FREE_TIER.md](FREE_TIER.md) - Cost optimization
- [README.md](README.md) - Mod features
