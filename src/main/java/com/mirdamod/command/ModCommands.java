package com.mirdamod.command;

import com.mirdamod.MirdaMod;
import com.mirdamod.structure.MirdaAltarGenerator;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

/**
 * Custom commands for the Mirda mod
 */
public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("summon_mirda_altar")
                .requires(source -> source.hasPermission(2)) // Requires OP level 2
                .executes(ModCommands::summonAltar)
        );

        dispatcher.register(
            Commands.literal("mirda_altar")
                .requires(source -> source.hasPermission(2))
                .executes(ModCommands::summonAltar)
        );

        MirdaMod.LOGGER.info("Registered Mirda commands!");
    }

    private static int summonAltar(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!(source.getLevel() instanceof ServerLevel level)) {
            source.sendFailure(Component.literal("Command must be run in a loaded world!"));
            return 0;
        }

        // Get player position or command block position
        BlockPos pos = BlockPos.containing(source.getPosition());

        // Find suitable ground level
        BlockPos groundPos = findGroundLevel(level, pos);

        source.sendSuccess(() -> Component.literal("Generating Mirda's altar..."), true);

        // Generate the altar
        MirdaAltarGenerator.generateAltar(level, groundPos);

        source.sendSuccess(() -> Component.literal(
            "Mirda's altar has been summoned at " +
            groundPos.getX() + ", " + groundPos.getY() + ", " + groundPos.getZ() +
            "! Prepare for battle!"
        ), true);

        return 1;
    }

    private static BlockPos findGroundLevel(ServerLevel level, BlockPos startPos) {
        BlockPos pos = new BlockPos(startPos.getX(), level.getMaxBuildHeight() - 1, startPos.getZ());

        // Find the first solid block going down
        while (pos.getY() > level.getMinBuildHeight()) {
            if (level.getBlockState(pos).isSolid()) {
                return pos.above();
            }
            pos = pos.below();
        }

        return startPos;
    }
}
