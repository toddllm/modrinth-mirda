# Mirda Boss Mod

A NeoForge mod for Minecraft 1.20.1 that adds Mirda, the ultimate goddess-like boss entity.

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

```bash
./gradlew build
```

## Running

### Client
```bash
./gradlew runClient
```

### Server
```bash
./gradlew runServer
```

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

## Notes

- This is a work in progress
- Custom models and textures can be added later
- The image provided by the creator will be used for final textures
- Structure generation can be triggered via commands (to be implemented)

## Credits

Created for the Mirda Games, Cthuslone, Golden Mirda, Xztalh, Destroyer of Worlds universe.
