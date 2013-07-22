package com.me.tft_02.assassin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.AssassinMode;

public class SpawnMaskCommand implements CommandExecutor {
    Assassin plugin;

    public SpawnMaskCommand(Assassin instance) {
        plugin = instance;
    }

    private AssassinMode assassin = new AssassinMode(plugin);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (!player.hasPermission("assassin.commands.spawnmask")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        if (args.length == 2) {
            assassin.spawnMask(player, Integer.parseInt(args[1]));
            return true;
        }
        else {
            assassin.spawnMask(player, 1);
            return true;
        }
    }
}
