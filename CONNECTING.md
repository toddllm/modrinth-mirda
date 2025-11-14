# How to Connect to Your Mirda Minecraft Server

This guide explains how players connect to the Minecraft server hosted on Render.

## Understanding "Private Service"

**Important:** "Private Service" on Render does NOT mean "inaccessible"!

- ✅ **Still publicly accessible** via TCP
- ✅ Players can connect from anywhere
- ❌ Just means it's not HTTP/web-based
- ✅ Perfect for Minecraft (uses port 25565)

## Getting Your Server Address

### Step 1: Find Your Server's Hostname

After deploying to Render:

1. **Go to Render Dashboard**
   - [dashboard.render.com](https://dashboard.render.com)
   - Select your `mirda-minecraft-server` service

2. **Look for Connection Info**
   - You'll see something like:
     ```
     External Address: mirda-minecraft-server-xxxx.onrender.com
     Port: 25565
     ```

3. **Copy the full address:**
   ```
   mirda-minecraft-server-xxxx.onrender.com:25565
   ```

### Step 2: Share with Players

Give players the address:
```
mirda-minecraft-server-xxxx.onrender.com:25565
```

Or just the hostname (25565 is default):
```
mirda-minecraft-server-xxxx.onrender.com
```

## Connecting in Minecraft

### For Players:

1. **Launch Minecraft 1.20.1** with **NeoForge 47.1.106** installed

2. **Go to Multiplayer**
   - Click "Multiplayer"
   - Click "Add Server"

3. **Enter Server Info:**
   - **Server Name:** Mirda Boss Server (or whatever you want)
   - **Server Address:** `your-service.onrender.com:25565`

4. **Click "Done"** then **"Join Server"**

5. **Fight Mirda!**
   - Ask admin to run: `/summon_mirda_altar`

## Troubleshooting

### "Can't Connect to Server"

**Check if server is running:**
1. Dashboard → Your service → Status should be "Live"
2. Logs should show: "Done! For help, type 'help'"

**Wait for startup:**
- Server takes 2-5 minutes to start
- Check logs for "Server started"

**Verify address:**
- Make sure you're using the exact hostname from Render
- Include port :25565

### "Unknown Host"

- Double-check the hostname spelling
- Remove `https://` if you accidentally added it
- Just use: `hostname.onrender.com:25565`

### "Server Offline"

The watchdog may have shut it down due to inactivity!

**To restart:**
1. Render Dashboard → Your service
2. "Manual Deploy" → "Deploy latest commit"
3. Wait 2-5 minutes for server to start

### "Outdated Server/Client"

- Server is 1.20.1 with NeoForge 47.1.106
- Players must have exact same versions
- Download Mirda mod JAR and place in mods folder

## Server Administration

### Making Yourself OP

**Option 1: Environment Variable (before starting)**
1. Render Dashboard → Environment
2. Add: `OPS=YourMinecraftUsername`
3. Redeploy service

**Option 2: Via Console**
1. Render Dashboard → Shell
2. Attach to Minecraft console:
   ```bash
   tail -f logs/latest.log
   ```
3. In another shell tab, run:
   ```bash
   echo "op YourUsername" > /minecraft/stdin
   ```

### Essential Commands (once OP)

```
/summon_mirda_altar          # Spawn Mirda's altar
/give @s mirdamod:bocow      # Get Mirda's weapon
/give @s mirdamod:altar_compass  # Get compass to altar
/op PlayerName               # Make another player OP
/gamemode creative           # Creative mode
/tp PlayerName 0 100 0       # Teleport player
```

## Checking Server Status

### Is the server running?

**Method 1: Render Dashboard**
- Status indicator shows "Live" (green) or "Offline" (red)

**Method 2: Minecraft Server List**
- Add server to Minecraft
- Look for green connection bars
- Shows player count

**Method 3: Online Tools**
- [mcsrvstat.us](https://mcsrvstat.us)
- Enter: `your-hostname.onrender.com`
- Shows server status, version, players

## Auto-Shutdown Behavior

The server automatically shuts down after **30 minutes** of no players (to save costs).

**When it happens:**
- All players disconnect
- Wait 30 minutes
- Server shuts down
- Render status: "Offline"

**To restart:**
1. Render Dashboard → Manual Deploy
2. Wait 2-5 minutes
3. Reconnect in Minecraft

**To adjust timeout:**
1. Render Dashboard → Environment
2. Change `IDLE_MINUTES` (default: 30)
3. Set to 0 to disable auto-shutdown

## Performance Tips

### For Best Experience:

**Server Settings:**
- View distance: 10 chunks (default)
- Max players: 20 (configurable)
- Render plan: Standard (4GB RAM minimum)

**Player Requirements:**
- Good internet connection
- Minecraft 1.20.1
- NeoForge 47.1.106 installed
- Mirda mod JAR in mods folder

**Reduce Lag:**
- Lower `VIEW_DISTANCE` to 6-8 in render.yaml
- Reduce `MAX_PLAYERS` if needed
- Upgrade to Pro plan (8GB RAM)

## Sharing Your Server

### Server Information Template

Share this with your players:

```
🎮 MIRDA BOSS SERVER 🎮

Server Address: your-hostname.onrender.com:25565
Minecraft Version: 1.20.1
Mod Loader: NeoForge 47.1.106
Required Mod: Mirda Boss Mod (download from GitHub releases)

Installation:
1. Install Minecraft 1.20.1
2. Install NeoForge 47.1.106
3. Download Mirda mod JAR
4. Place in .minecraft/mods folder
5. Launch Minecraft with NeoForge profile
6. Add server and connect!

Special Features:
- Fight Mirda, the ultimate boss!
- Multiple phases and abilities
- Massive altar/castle to explore
- Custom weapons and items

Commands:
- /summon_mirda_altar - Spawn the altar (OP only)

Note: Server auto-shuts down after 30 min of inactivity.
If offline, message admin to restart!
```

## Port Forwarding (Not Needed!)

**Good news:** Render handles all networking automatically!

- ❌ No port forwarding needed
- ❌ No router configuration
- ❌ No firewall rules
- ✅ Just use the Render hostname

This is way easier than hosting at home!

## Whitelist (Optional)

To restrict who can join:

1. **Create whitelist file**
   - Render Shell: `touch whitelist.json`
   - Edit: `vi whitelist.json`
   - Add:
     ```json
     [
       {
         "uuid": "player-uuid-here",
         "name": "PlayerName"
       }
     ]
     ```

2. **Enable whitelist**
   - Add to render.yaml:
     ```yaml
     - key: WHITELIST
       value: true
     ```
   - Or in server console: `/whitelist on`

3. **Add players**
   - `/whitelist add PlayerName`

## IP Address vs Hostname

**Use hostname (recommended):**
```
your-service.onrender.com:25565
```

**IP address (may change):**
```
123.45.67.89:25565
```

⚠️ **Warning:** Render may change IP addresses on redeployment. Always use the hostname for reliability!

## Mobile Players

Minecraft Bedrock Edition (mobile, console) **cannot** connect to Java Edition servers.

This server is:
- ✅ Java Edition compatible
- ❌ Bedrock Edition incompatible

Players need:
- PC (Windows, Mac, Linux)
- Java Edition of Minecraft
- NeoForge mod loader

## Cost During Play Sessions

**Server running costs:**
- Standard plan: $25/month = ~$0.034/hour
- 4 hour play session: ~$0.14
- 100 hours/month total: ~$3.40/month

**Tip:** Manually start/stop server to minimize costs!

## Questions?

**Server Issues:**
- Check Render Dashboard logs
- GitHub Issues for mod bugs

**Minecraft Help:**
- [Minecraft Wiki](https://minecraft.fandom.com)
- [NeoForge Discord](https://discord.neoforged.net)

**Connection Problems:**
- Verify Minecraft version (1.20.1)
- Check NeoForge version (47.1.106)
- Ensure Mirda mod is installed
- Wait for server startup (2-5 min)
