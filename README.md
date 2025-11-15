# Mirda Boss Mod

A NeoForge mod for Minecraft 1.20.1 that adds Mirda, the ultimate goddess-like boss entity.

**Based on the Mirda Games universe**: Cthuslone, Golden Mirda, Xztalh, Destroyer of Worlds

## Features

### Mirda - The Ultimate Boss
- **Multiple Phases**: Normal, Golden Mirda, Ultra Form, and many more
- **Unique Abilities**:
  - Lightning attacks from the sky and hands
  - Exploding beam attacks
  - Evoker fang summoning (representing spikes/bones)
  - Time manipulation (lifts and freezes nearby mobs)
  - Blackhole attack (strongest move)
  - Spinning attacks with projectiles
  - Mob consumption to gain power
  - Flying and crash attacks
  - Iron block armor defense
  - Energy drain abilities

### Combat System
- **Dynamic Phase System**: Mirda transforms through multiple phases with increasing power
- **Health Scaling**: Gains health and power from consuming other mobs
- **Immunity System**: Immune to lava, drowning, lightning, fire, and all non-melee damage
- **Combo System**: Melee attacks get stronger with each consecutive hit
- **Grab Mechanic**: Can grab players and inflict Wither/Poison effects

### Summoning & Minions
- Advanced skeletons with special powers
- Healing towers (gold/diamond structures) that restore Mirda's health
- Netherite-armored zombies
- Omensoul - A spirit boss that spawns during Mirda's sitting phase

### Mirda's Altar
- Huge obsidian castle with lava, redstone, gold, and diamond
- Lightning strikes around the structure
- Creeper banners on glass windows
- Interior throne room with hearts
- Four corner towers
- Mirda automatically spawns here, ready for battle

### Items
- **Bocow**: Mirda's legendary weapon - hybrid axe/sword with eruption ability
- **Altar Compass**: Shows players where to find Mirda's altar
- **Crystal Heart**: Dropped when Mirda is defeated

## Building

### Local Build
```bash
./gradlew build
```

The compiled JAR will be in `build/libs/`

### CI/CD
The project includes automated GitHub Actions that:
- ✅ Build the mod on every push
- ✅ Run tests and cache dependencies
- ✅ Create GitHub releases on version tags
- ✅ Optional deployment to Railway (paid after trial)

#### Creating a Release
```bash
git tag v1.0.0
git push origin v1.0.0
```
GitHub Actions will automatically build and create a release with the JAR file.

## Running

### Local Client
```bash
./gradlew runClient
```

### Local Server
```bash
./gradlew runServer
```

### Production Server (Free Hosting)

Host a public Minecraft server with the Mirda mod for **$0/month** using our multi-provider strategy:

**Recommended Options:**
- **FalixNodes** (Primary) - 4GB RAM, free forever, easy mod support
- **Oracle Cloud Always Free** - 24GB RAM, 24/7 uptime, requires setup
- **Aternos** (Backup) - Free with queue system, 4GB world limit

**Quick Start:**
1. Create account at [FalixNodes](https://falixnodes.net) or [Oracle Cloud](https://cloud.oracle.com)
2. Set up NeoForge 1.20.1 server
3. Upload Mirda mod JAR
4. Configure Cloudflare DNS (optional, for custom domain)
5. Share server address with players

**Full Guide:** See [MULTI_PROVIDER.md](MULTI_PROVIDER.md) for complete multi-provider strategy

**Deployment Docs:**
- [DEPLOYMENT.md](DEPLOYMENT.md) - Detailed setup for each provider
- [CONNECTING.md](CONNECTING.md) - How players connect
- [FREE_TIER.md](FREE_TIER.md) - Cost optimization tips

**Important:** Render Private Services are NOT publicly accessible and cannot be used for public game servers. See [MULTI_PROVIDER.md](MULTI_PROVIDER.md) for details.

## Development

This mod is built with:
- **Minecraft**: 1.20.1
- **NeoForge**: 47.1.106
- **Java**: 17

## Special Mechanics

### Day/Night Transformations
- **Night**: Mirda transforms to Demon Yellow form
- **Day**: Goddess Yellow-Red form with rainbow lightning

### Sunlight Exposure
- Mirda changes size when exposed to sunlight
- Uses hand lightning instead of sky lightning during sunlight

### Energy System
- Mirda starts with 1 energy
- Gains energy by consuming mobs
- Uses energy for special attacks

### Eye Contact
- Looking at Mirda's eyes sets players on fire
- Similar to Enderman aggro mechanic but stronger

## Advanced Features

### Omensoul System
When Mirda sits, she creates Omensoul:
1. Boss battle vs Omensoul
2. On defeat, Omensoul curses nearby players (Bad Omen)
3. Omensoul possesses a nearby mob
4. Boss battle vs Omensoul + possessed mob
5. On final defeat, Omensoul breaks and fades

### Phase Progression
1. Normal Form → Golden Mirda (halfway health)
2. Gains extra hands and dragon heads
3. Can transform to Ultra Form
4. Golem Form with dragon head guardians
5. Ice Phase with powder snow
6. Netherite Armor Phase
7. Super Saythren Rebirth
8. Castle Throne Phase
9. Crystal Power Phase
10. Final Immortal Form

## Commands

### Summon Mirda's Altar
```
/summon_mirda_altar
```
or
```
/mirda_altar
```
Requires OP level 2. Generates Mirda's massive altar at your current location.

## Custom Model & Textures

The mod includes a **custom entity model** for Mirda with:
- Tall goddess-like figure (giant-sized, 3x scale)
- Flowing dark robes
- Golden glowing crown/halo
- Golden glowing chest orb
- Animated dragon heads (appear at half health)
- Dynamic animations for different phases
- Particle effects for special abilities

### Reference Artwork
The mod includes reference artwork (`Connected Mirda and Xztalh.png`) showing:
- Dark mystical robes
- Golden crown/halo above head
- Golden chest orb
- Yellow/gold lightning energy
- Powerful goddess appearance

### Texture Creation
See `TEXTURE_GUIDE.md` and `ITEM_TEXTURES.md` for detailed instructions on creating:
- **Entity textures** (128x128): mirda.png, mirda_golden.png, mirda_ultra.png
- **Item textures** (16x16): bocow.png, altar_compass.png, crystal_heart.png

Textures should be placed in:
```
src/main/resources/assets/mirdamod/textures/entity/
src/main/resources/assets/mirdamod/textures/item/
```

## Development Status

### Completed ✅
- Complete entity system with all phases
- 11 custom AI goals for combat
- Custom model with animations
- Summoning system (skeletons, towers, zombies)
- Omensoul boss fight
- Custom items (Bocow, Altar Compass, Crystal Heart)
- Altar/castle structure generation
- Command system for testing
- Full NBT save/load
- Localization (English)

### To Complete 🔨
- Entity textures (waiting for artist or can use guides)
- Item textures (waiting for artist or can use guides)
- Emissive (glowing) texture layers
- Additional phase implementations
- Sound effects
- Particle effects customization
- World generation integration

## Notes

- Reference artwork included: `Connected Mirda and Xztalh.png`
- Detailed texture guides provided for creating assets
- Mod is fully functional but uses placeholder textures until custom ones are added
- All mechanics and abilities are implemented and working

## Credits

Created for the Mirda Games, Cthuslone, Golden Mirda, Xztalh, Destroyer of Worlds universe.

Based on reference artwork showing Mirda's design.
