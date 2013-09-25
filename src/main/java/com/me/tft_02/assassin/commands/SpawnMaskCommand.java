package com.me.tft_02.assassin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.AssassinMode;
import com.me.tft_02.assassin.util.CommandUtils;

public class SpawnMaskCommand implements CommandExecutor {

    private AssassinMode assassin = new AssassinMode();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("assassin.commands.spawnmask")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        int amount = 1;

        if (args.length == 2) {
            amount = Integer.parseInt(args[1]);
        }

        assassin.spawnMask(player, amount);
        player.sendMessage("Spawned one mask");
        return true;
    }
}
