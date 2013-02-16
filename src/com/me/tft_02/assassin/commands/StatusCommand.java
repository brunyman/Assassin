package com.me.tft_02.assassin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.util.Misc;
import com.me.tft_02.assassin.util.PlayerData;

public class StatusCommand implements CommandExecutor {
    Assassin plugin;

    public StatusCommand(Assassin instance) {
        plugin = instance;
    }

    private PlayerData data = new PlayerData(plugin);
    private Misc misc = new Misc(plugin);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (!player.hasPermission("assassin.commands.status")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "Your status = " + ChatColor.RED + data.getStatus(player));
        if (data.isAssassin(player)) {
            player.sendMessage(ChatColor.GOLD + "Time left in Assassin Mode = " + ChatColor.DARK_RED + misc.getStringTimeLeft(player));
        }
        player.sendMessage(ChatColor.DARK_GRAY + "=====================");
        player.sendMessage(ChatColor.GOLD + "Current bounty = " + ChatColor.DARK_RED + data.getKillCount(player));
        player.sendMessage(ChatColor.GOLD + "Bounty collected = " + ChatColor.GREEN + data.getBountyCollected(player));
        return true;
    }
}
