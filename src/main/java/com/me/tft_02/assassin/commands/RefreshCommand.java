package com.me.tft_02.assassin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.util.CommandUtils;
import com.me.tft_02.assassin.util.Permissions;
import com.me.tft_02.assassin.util.player.PlayerData;

import org.kitteh.tag.TagAPI;

public class RefreshCommand implements CommandExecutor {

    private PlayerData data = new PlayerData();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        Player player = (Player) sender;

        if (!Permissions.refresh(player)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        OfflinePlayer target = player;
        if (args.length == 2) {
            target = Bukkit.getServer().getOfflinePlayer(args[1]);
        }

        if (CommandUtils.isOffline(sender, target)) {
            return true;
        }

        TagAPI.refreshPlayer((Player) target);
        data.removeCooldown((Player) target);
        player.sendMessage(ChatColor.RED + "Refreshed cooldowns for " + target.getName());
        return true;
    }
}
