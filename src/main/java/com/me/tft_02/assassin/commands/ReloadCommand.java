package com.me.tft_02.assassin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.Assassin;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (!player.hasPermission("assassin.commands.reload")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }
        Assassin.p.reloadConfig();
        player.sendMessage(ChatColor.DARK_RED + "[Assassin]: " + ChatColor.GRAY + "Config reloaded");
        return true;
    }
}
