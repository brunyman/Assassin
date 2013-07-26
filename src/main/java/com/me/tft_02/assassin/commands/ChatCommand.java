package com.me.tft_02.assassin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.util.PlayerData;

public class ChatCommand implements CommandExecutor {
    Assassin plugin;

    public ChatCommand(Assassin instance) {
        plugin = instance;
    }

    private PlayerData data = new PlayerData(plugin);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (!player.hasPermission("assassin.commands.chat")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        if (!data.isAssassin(player)) {
            player.sendMessage(ChatColor.RED + "You aren't an assassin.");
            return true;
        }

        if (!data.getAssassinChatMode(player)) {
            data.enterAssassinChat(player);
            player.sendMessage(ChatColor.GRAY + "Assassin Chat " + ChatColor.GREEN + "ON");
        }
        else {
            data.leaveAssassinChat(player);
            player.sendMessage(ChatColor.GRAY + "Assassin Chat " + ChatColor.RED + "OFF");
        }
        return true;
    }
}