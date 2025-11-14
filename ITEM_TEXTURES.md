# Item Texture Guide

Item textures needed for the Mirda mod.

## Required Item Textures

All item textures should be **16x16 pixels** in PNG format.

### 1. Bocow (`bocow.png`)
**Location**: `src/main/resources/assets/mirdamod/textures/item/bocow.png`

**Description**: Mirda's legendary weapon - a hybrid axe/sword

**Design Ideas**:
- Half of the texture shows an axe blade
- Other half shows a sword blade
- Golden/yellow handle representing Mirda's power
- Dark blade with golden energy crackling
- Mystical appearance

**Color Scheme**:
- Blade: Dark gray/black (RGB 30, 30, 35)
- Handle: Golden (RGB 255, 215, 0)
- Energy: Yellow crackling (RGB 255, 230, 100)
- Accent: Red/orange for power (RGB 255, 100, 0)

### 2. Altar Compass (`altar_compass.png`)
**Location**: `src/main/resources/assets/mirdamod/textures/item/altar_compass.png`

**Description**: Special compass that points to Mirda's altar

**Design Ideas**:
- Base: Compass design similar to vanilla compass
- Golden frame representing divine connection
- Dark center with mystical symbol
- Glowing pointer/needle in yellow
- Obsidian/dark accents

**Color Scheme**:
- Frame: Golden (RGB 255, 215, 0)
- Face: Dark obsidian (RGB 20, 20, 30)
- Needle: Bright yellow (RGB 255, 255, 100)
- Glow: Light yellow (RGB 255, 240, 200)

### 3. Crystal Heart (`crystal_heart.png`)
**Location**: `src/main/resources/assets/mirdamod/textures/item/crystal_heart.png`

**Description**: Rare drop from defeating Mirda

**Design Ideas**:
- Heart-shaped crystal
- Translucent/glowing appearance
- Multiple colors representing Mirda's power
- Golden glow emanating
- Geometric/crystalline facets

**Color Scheme**:
- Primary: Pink/magenta crystal (RGB 255, 50, 150)
- Secondary: Golden glow (RGB 255, 215, 0)
- Highlights: White/bright (RGB 255, 255, 255)
- Shadows: Dark purple (RGB 100, 0, 100)

## Quick Creation Tips

### Using Placeholder Textures
If you need quick placeholders:
1. Copy similar vanilla item textures
2. Recolor them to match the color scheme
3. Add a few pixels of golden glow
4. Save in correct location

### Creating From Scratch
1. Open 16x16 canvas in image editor
2. Use pencil tool (1px brush) for pixel art
3. Start with dark outline
4. Fill with base colors
5. Add highlights and shadows
6. Add golden glow/accent pixels
7. Save as PNG

### Recommended Tools
- **Aseprite**: Professional pixel art tool
- **Piskel**: Free online pixel art editor
- **GIMP**: Free general image editor
- **Paint.NET**: Free Windows image editor

## File Structure
```
src/main/resources/assets/mirdamod/textures/item/
├── bocow.png (16x16)
├── altar_compass.png (16x16)
└── crystal_heart.png (16x16)
```

## Testing
1. Place textures in correct directory
2. Run the mod
3. Use `/give @s mirdamod:bocow` to get items
4. Check if textures appear correctly
5. Verify items render properly in inventory and hand

## Notes
- All item textures are 16x16 pixels
- Use transparent background (alpha channel)
- Follow Minecraft's pixel art style
- Match the golden/dark theme from Mirda's design
- Can add emissive/glowing layers later with _e suffix
