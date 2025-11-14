package com.mirdamod.entity.boss;

/**
 * Represents the different phases/forms that Mirda can be in during battle
 */
public enum MirdaPhase {
    NORMAL(0, "Normal Mirda", 1.0f),
    GOLDEN(1, "Golden Mirda", 1.5f),
    ULTRA(2, "Ultra Form", 2.0f),
    GOLEM_FORM(3, "Grave Diamond Golem", 1.3f),
    ICE_PHASE(4, "Ice Phase", 1.2f),
    NETHERITE_ARMOR(5, "Netherite Armor Phase", 1.8f),
    SUPER_SAYTHREN(6, "Super Saythren Rebirth", 2.5f),
    CASTLE_THRONE(7, "Castle Throne Phase", 1.0f),
    CRYSTAL_POWER(8, "Crystal Power Phase", 2.2f),
    FINAL_FORM(9, "Final Immortal Form", 3.0f),
    DEMON_YELLOW(10, "Demon Yellow (Night)", 1.6f),
    GODDESS_YELLOW_RED(11, "Goddess Yellow-Red (Day)", 1.4f),
    CHECKMATED(12, "Checkmated - Impossible Dodge", 2.0f);

    private final int id;
    private final String displayName;
    private final float damageMultiplier;

    MirdaPhase(int id, String displayName, float damageMultiplier) {
        this.id = id;
        this.displayName = displayName;
        this.damageMultiplier = damageMultiplier;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public static MirdaPhase fromId(int id) {
        for (MirdaPhase phase : values()) {
            if (phase.id == id) {
                return phase;
            }
        }
        return NORMAL;
    }
}
