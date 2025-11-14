package com.mirdamod.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

/**
 * Bocow - Mirda's weapon
 * Hybrid axe/sword with special eruption ability
 */
public class BocowItem extends SwordItem {
    private static final Tier BOCOW_TIER = new Tier() {
        @Override
        public int getUses() {
            return 3000;
        }

        @Override
        public float getSpeed() {
            return 10.0f;
        }

        @Override
        public float getAttackDamageBonus() {
            return 15.0f; // +15 damage (very powerful)
        }

        @Override
        public int getLevel() {
            return 4; // Netherite level
        }

        @Override
        public int getEnchantmentValue() {
            return 20;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(net.minecraft.world.item.Items.NETHERITE_INGOT);
        }
    };

    private int eruptionCooldown = 0;

    public BocowItem(Properties properties) {
        super(BOCOW_TIER, 8, -2.4f, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && eruptionCooldown <= 0) {
            // Eruption ability
            performEruption(level, player);
            eruptionCooldown = 200; // 10 second cooldown
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    private void performEruption(Level level, Player player) {
        // Create eruption around player
        for (int i = 0; i < 12; i++) {
            double angle = (Math.PI * 2 * i) / 12.0;
            double radius = 4.0;
            double x = player.getX() + Math.cos(angle) * radius;
            double z = player.getZ() + Math.sin(angle) * radius;

            // Create explosion effect
            level.explode(
                player,
                x, player.getY(), z,
                3.0f,
                false,
                Level.ExplosionInteraction.NONE
            );
        }

        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Bocow's Eruption!"));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);

        if (eruptionCooldown > 0) {
            eruptionCooldown--;
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Extra effects on hit
        if (!attacker.level().isClientSide) {
            // Chance to create small explosion
            if (attacker.getRandom().nextFloat() < 0.3f) {
                attacker.level().explode(
                    attacker,
                    target.getX(), target.getY(), target.getZ(),
                    1.5f,
                    false,
                    Level.ExplosionInteraction.NONE
                );
            }
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean canAttackBlock(net.minecraft.world.level.block.state.BlockState state, Level level, BlockPos pos, Player player) {
        return true; // Can break blocks like an axe
    }
}
