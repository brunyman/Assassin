package com.me.tft_02.assassin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.util.PlayerData;
import org.kitteh.tag.TagAPI;

public class RefreshCommand implements CommandExecutor {

    private PlayerData data = new PlayerData();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (!player.hasPermission("assassin.commands.refresh")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        if (args.length == 2) {
            Player target = Bukkit.getServer().getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.GOLD + args[1] + ChatColor.RED + " is not online!");
                return false;
            }
            else {
                TagAPI.refreshPlayer(target);
                data.removeCooldown(target);
                player.sendMessage(ChatColor.RED + "Refreshed cooldowns for " + target.getName());
                return true;
            }
        }
        else {
            TagAPI.refreshPlayer(player);
            data.removeCooldown(player);
            player.sendMessage(ChatColor.RED + "Refreshed cooldowns for " + player.getName());
            return true;
        }
    }
}
