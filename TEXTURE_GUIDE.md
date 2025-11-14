# Mirda Texture Guide

This guide explains how to create textures for Mirda based on the reference artwork.

## Reference Image
The reference image `Connected Mirda and Xztalh.png` shows Mirda's visual design:
- **Dark robes**: Black/dark gray flowing cloak
- **Golden crown/halo**: Bright yellow/gold glowing circle above head
- **Golden chest orb**: Bright yellow/gold glowing orb on chest
- **Lightning effects**: Yellow/gold energy crackling around
- **Mystical goddess appearance**: Powerful, imposing figure

## Required Texture Files

### 1. `mirda.png` (Normal Form)
**Location**: `src/main/resources/assets/mirdamod/textures/entity/mirda.png`
**Size**: 128x128 pixels
**UV Layout**: Standard Minecraft entity texture

#### Color Palette:
- **Dark Robe**: RGB(20, 20, 25) - Very dark blue-gray
- **Robe Highlights**: RGB(40, 40, 50) - Slightly lighter for depth
- **Crown/Halo**: RGB(255, 220, 0) - Bright golden yellow
- **Chest Orb**: RGB(255, 200, 0) - Golden orange
- **Energy Lines**: RGB(255, 230, 100) - Bright yellow for lightning

#### UV Mapping:
```
Face/Head (0, 0, 12x12):
  - Dark shadowy face
  - Glowing eyes (cyan or yellow)

Crown (48, 0, 16x2):
  - Bright golden color
  - Glowing effect
  - Circular pattern

Body (0, 24, 16x24):
  - Dark robe texture
  - Golden energy lines
  - Flowing fabric patterns

Chest Orb (96, 0, 6x6):
  - Bright golden sphere
  - Radial gradient from center
  - Glowing appearance

Arms (48, 24 and 68, 24, 4x24):
  - Dark robe sleeves
  - Golden trim/accents
  - Energy crackles

Robe (0, 56, 24x32):
  - Flowing dark fabric
  - Golden energy patterns
  - Mystical symbols optional

Dragon Heads (88, 24, 8x8):
  - Dragon skull/head design
  - Yellow/gold eyes
  - Dark scales with gold accents
```

### 2. `mirda_golden.png` (Golden Form)
**Same layout as mirda.png but:**
- More intense golden colors
- Brighter glow effects
- Golden highlights on robe
- Crown even more radiant
- RGB(255, 215, 0) for primary gold

### 3. `mirda_ultra.png` (Ultra Form)
**Same layout but:**
- Add red/orange energy to gold
- More intense effects
- RGB(255, 100, 0) for fiery energy
- Purple/magenta accents: RGB(200, 0, 255)
- More aggressive appearance

## Texture Creation Tips

### Tools:
- Use any image editor (GIMP, Photoshop, Paint.NET)
- Create at 128x128 resolution
- Save as PNG with transparency
- Use pixel art style for Minecraft aesthetic

### Workflow:
1. Start with a black base (RGB 20, 20, 25)
2. Add golden crown at top
3. Add golden orb at chest
4. Paint dark robes with flowing lines
5. Add golden energy crackling effects
6. Add glowing eyes to face
7. Save as PNG

### Color Guidelines:
- **Dark Base**: Keep most of the texture dark to match the mysterious goddess theme
- **Golden Accents**: Use bright yellows/golds for crown, orb, and energy
- **Glow Effect**: Make certain areas very bright (255, 255, 255) for glow
- **Contrast**: High contrast between dark robes and bright gold creates drama

## Placeholder Instructions

If you cannot create textures immediately:
1. Copy a vanilla entity texture as a placeholder
2. The mod will still work, just without the custom appearance
3. Replace the placeholder when textures are ready

## File Locations
```
src/main/resources/assets/mirdamod/textures/entity/
├── mirda.png (Normal form - REQUIRED)
├── mirda_golden.png (Golden form)
├── mirda_ultra.png (Ultra form)
└── mirda_reference.png (Reference artwork - already added)
```

## Testing
After creating textures:
1. Place them in the correct directory
2. Run the mod in Minecraft
3. Spawn Mirda with `/summon mirdamod:mirda`
4. Check if textures load correctly
5. Test phase transformations to see different textures

## Notes
- The model expects 128x128 textures
- Transparency is supported for effects
- Emissive (glowing) textures can be added later with _e suffix
- The reference image should guide all color choices
