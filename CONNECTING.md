# How to Connect to Your Mirda Minecraft Server

This guide explains how players connect to your Minecraft server running the Mirda Boss Mod.

## Important: Hosting Provider Matters

**WARNING: Render Private Services are NOT publicly accessible from the internet.**

If you deployed to Render as a "Private Service", players **CANNOT** connect directly. Render Private Services:
- Have no public IP address
- Are only accessible within Render's internal network
- Require VPN/tunnel setup for external access (advanced)

**For public multiplayer, use one of these providers instead:**
- FalixNodes (free, easy)
- Oracle Cloud Always Free (free, powerful)
- Aternos (free, with queue)
- Railway (paid after trial)
- Any VPS with public IP

See [MULTI_PROVIDER.md](MULTI_PROVIDER.md) for setup instructions.

---

## Connecting to FalixNodes Server

### Getting Your Server Address

1. **Log in to FalixNodes Panel**
   - [falixnodes.net/panel](https://falixnodes.net/panel)

2. **Find Connection Info**
   - Select your server
   - Look for "Server Address" section
   - Copy the address (format: `node.falixsrv.me:12345`)

3. **Share with Players**
   ```
   node.falixsrv.me:12345
   ```

### Player Connection Steps

1. **Install Prerequisites:**
   - Minecraft Java Edition 1.20.1
   - NeoForge 47.1.106
   - Mirda Boss Mod JAR (place in `.minecraft/mods/`)

2. **Launch Minecraft:**
   - Use NeoForge profile
   - Select version 1.20.1

3. **Add Server:**
   - Click "Multiplayer"
   - Click "Add Server"
   - Server Name: `Mirda Boss Server`
   - Server Address: `node.falixsrv.me:12345`
   - Click "Done"

4. **Connect and Play!**
   - Click "Join Server"
   - Ask admin to run `/summon_mirda_altar`

---

## Connecting to Oracle Cloud Server

### Getting Your Server Address

1. **Find VM Public IP**
   - Oracle Cloud Console → Compute → Instances
   - Note the Public IP Address (e.g., `140.238.xxx.xxx`)

2. **Server Address:**
   ```
   140.238.xxx.xxx:25565
   ```

3. **Or use custom domain** (if configured via Cloudflare):
   ```
   play.yourdomain.com
   ```

### Player Connection Steps

Same as FalixNodes, but use Oracle Cloud IP or your custom domain.

---

## Connecting to Aternos Server

### Getting Your Server Address

1. **Log in to Aternos**
   - [aternos.org](https://aternos.org)

2. **Start Server** (wait in queue)

3. **Find Address:**
   - Format: `YourServer.aternos.me`

### Player Connection Steps

1. **Server must be running** (owner starts it first)
2. **Add server in Minecraft:**
   - Server Address: `YourServer.aternos.me`
3. **Note:** Server auto-shuts down after inactivity

---

## Using Custom Domain (Cloudflare)

If you configured Cloudflare DNS:

### Players Connect To:
```
play.yourdomain.com
```

### No Port Needed
Cloudflare SRV records handle port routing automatically.

### Switching Providers
When you change providers:
1. Update DNS A record to new IP
2. Players still connect to same domain
3. Seamless transition

See [MULTI_PROVIDER.md](MULTI_PROVIDER.md) for DNS setup.

---

## Troubleshooting

### "Can't Connect to Server"

**Check server is running:**
- FalixNodes: Panel shows "Online"
- Oracle Cloud: SSH in and check process
- Aternos: Must be started, not in queue

**Verify address:**
- Double-check IP/hostname spelling
- Include port if not 25565
- Don't add `https://` prefix

**Wait for startup:**
- Server takes 1-3 minutes to fully start
- Check logs for "Done! For help, type 'help'"

### "Connection Refused"

**Firewall issues:**
- Oracle Cloud: Check security list allows TCP 25565
- VPS: Check iptables/ufw rules
- FalixNodes: Firewall handled automatically

**Wrong port:**
- FalixNodes uses non-standard ports (e.g., 12345)
- Oracle Cloud/VPS use 25565 by default
- Verify in hosting panel

### "Outdated Client/Server"

**Version mismatch:**
- Server: Minecraft 1.20.1, NeoForge 47.1.106
- Players must have exact same versions
- Download mod JAR from GitHub releases
- Place in `.minecraft/mods/` folder

### "Unknown Host"

**DNS issues:**
- Check hostname spelling
- For Cloudflare: Wait 5-10 minutes for propagation
- Use direct IP as fallback

### "Server Offline" (Aternos)

**Auto-shutdown occurred:**
- Aternos shuts down after inactivity
- Owner must restart via dashboard
- Wait in queue before connecting

---

## Server Administration

### Becoming OP (Operator)

**FalixNodes:**
1. Panel → Console
2. Type: `op YourUsername`
3. Press Enter

**Oracle Cloud:**
1. SSH into VM
2. Attach to screen/tmux session
3. Type: `op YourUsername`

**Aternos:**
1. Server must be running
2. Go to Console tab
3. Type: `op YourUsername`

### Essential Commands

```bash
/summon_mirda_altar           # Spawn Mirda's altar
/give @s mirdamod:bocow       # Get Mirda's weapon
/give @s mirdamod:altar_compass  # Get altar compass
/give @s mirdamod:crystal_heart  # Get crystal heart
/op PlayerName                # Make player OP
/gamemode creative            # Change game mode
/tp @s 0 100 0               # Teleport
```

### Whitelist Setup

```bash
/whitelist on                 # Enable whitelist
/whitelist add PlayerName     # Add player
/whitelist list              # View allowed players
/whitelist off               # Disable (allow all)
```

---

## What About Render?

**Render Private Services are NOT suitable for public Minecraft servers.**

If you insist on using Render, you must:

1. **Set up VPN/Tunnel:**
   - Install Tailscale on Render service and all player machines
   - Or configure WireGuard VPN
   - Or use ngrok/Cloudflare Tunnel

2. **Players connect via:**
   - VPN IP address (not public internet)
   - Only works if all players join VPN

3. **This is NOT recommended:**
   - Complex setup
   - Additional software required
   - Not worth the effort vs. free alternatives

**Better option:** Use FalixNodes or Oracle Cloud (free, public, easy).

---

## Server Information Template

Share this with your players:

```
🎮 MIRDA BOSS SERVER 🎮

Server Address: [your-server-address]
Minecraft Version: 1.20.1
Mod Loader: NeoForge 47.1.106
Required Mod: Mirda Boss Mod

Installation Steps:
1. Install Minecraft Java Edition 1.20.1
2. Install NeoForge 47.1.106
3. Download Mirda mod JAR from GitHub releases
4. Place JAR in .minecraft/mods folder
5. Launch with NeoForge profile
6. Add server and connect!

Features:
- Fight Mirda, the ultimate goddess boss!
- 13 different phases with unique abilities
- Massive altar/castle structure
- Custom weapons (Bocow) and items
- Blackhole attack, lightning, time manipulation
- Omensoul spirit boss fight

In-Game Commands (OP only):
- /summon_mirda_altar - Spawn the boss arena

Note: [Add any server-specific notes here]
```

---

## Player Requirements

### Minimum Requirements

- **Minecraft:** Java Edition 1.20.1
- **Mod Loader:** NeoForge 47.1.106
- **Mod:** Mirda Boss Mod JAR
- **RAM:** 4GB+ allocated to Minecraft
- **Java:** 17 or higher

### Recommended Setup

1. **Install Java 17** (if not already)
2. **Install Minecraft 1.20.1**
3. **Download NeoForge installer:**
   - [neoforged.net](https://neoforged.net)
   - Version 47.1.106 for 1.20.1
4. **Run installer** (choose "Install client")
5. **Download Mirda mod JAR** from GitHub releases
6. **Place in mods folder:**
   - Windows: `%appdata%\.minecraft\mods\`
   - Mac: `~/Library/Application Support/minecraft/mods/`
   - Linux: `~/.minecraft/mods/`
7. **Launch Minecraft** with NeoForge profile
8. **Connect to server**

---

## Checking Server Status

### Method 1: Direct Connection Test

In Minecraft:
1. Add server to server list
2. Look for green signal bars
3. Shows "Online" with player count

### Method 2: Online Status Checker

Use [mcsrvstat.us](https://mcsrvstat.us):
1. Enter server address
2. See status, version, MOTD, players
3. Note: Doesn't work for Render Private Services

### Method 3: Hosting Panel

- FalixNodes: Panel shows server status
- Oracle Cloud: Check if process running via SSH
- Aternos: Dashboard shows running/offline

---

## Performance Optimization

### Server Settings (Admin)

Edit `server.properties`:
```properties
view-distance=10        # Lower for less lag (6-8)
max-players=20          # Limit concurrent players
network-compression-threshold=256  # Default is fine
```

### Client Settings (Players)

- Lower render distance if lagging
- Allocate more RAM to Minecraft (4GB+)
- Use OptiFine or Sodium for better FPS
- Reduce graphics settings in intense fights

### Network Tips

- Wired connection > WiFi
- Close bandwidth-heavy applications
- Ping matters less than you think for MC

---

## Mobile/Console Players

**Minecraft Bedrock Edition CANNOT connect to Java Edition servers.**

This mod requires:
- ✅ Java Edition (PC, Mac, Linux)
- ❌ Bedrock Edition (mobile, console, Windows 10 Store version)

Bedrock players need:
- A PC with Minecraft Java Edition
- Or a separate Bedrock server (mod won't work)

---

## Further Reading

- [MULTI_PROVIDER.md](MULTI_PROVIDER.md) - Hosting provider details
- [DEPLOYMENT.md](DEPLOYMENT.md) - Server setup guide
- [FREE_TIER.md](FREE_TIER.md) - Cost optimization
- [README.md](README.md) - Mod features and commands
