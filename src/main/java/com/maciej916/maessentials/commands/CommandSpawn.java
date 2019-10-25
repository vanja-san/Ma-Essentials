package com.maciej916.maessentials.commands;

import com.maciej916.maessentials.classes.Location;
import com.maciej916.maessentials.config.ConfigValues;
import com.maciej916.maessentials.data.DataManager;
import com.maciej916.maessentials.data.PlayerData;
import com.maciej916.maessentials.libs.Methods;
import com.maciej916.maessentials.libs.Teleport;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TextFormatting;

public class CommandSpawn {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> builder = Commands.literal("spawn").requires(source -> source.hasPermissionLevel(0));
        builder.executes(context -> spawn(context));
        dispatcher.register(builder);
    }

    private static int spawn(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().asPlayer();
        PlayerData playerData = DataManager.getPlayerData(player);
        long cooldown = Methods.delayCommand(playerData.getSpawnTime(), ConfigValues.homes_cooldown);
        if (cooldown == 0) {
            long currentTime = System.currentTimeMillis() / 1000;
            playerData.setSpawnTime(currentTime);
            DataManager.savePlayerData(playerData);
            doSpawn(player);
        } else {
            player.sendMessage(Methods.formatText("maessentials.cooldown", TextFormatting.RED, cooldown));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static void doSpawn(ServerPlayerEntity player) {
        if (ConfigValues.spawn_delay == 0) {
            player.sendMessage(Methods.formatText("spawn.maessentials.success", TextFormatting.WHITE));
        } else {
            player.sendMessage(Methods.formatText("spawn.maessentials.success.wait", TextFormatting.WHITE, ConfigValues.spawn_delay));
        }
        Location spawnLocation = DataManager.getModData().getSpawnPoint();
        Teleport.teleportPlayer(player, spawnLocation, true, ConfigValues.spawn_delay);
    }
}