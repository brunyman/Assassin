package com.me.tft_02.assassin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.AssassinMode;
import com.me.tft_02.assassin.util.PlayerData;

public class DeactivateCommand implements CommandExecutor {
    Assassin plugin;

    public DeactivateCommand(Assassin instance) {
        plugin = instance;
    }

    private AssassinMode assassin = new AssassinMode(plugin);
    private PlayerData data = new PlayerData(plugin);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (!player.hasPermission("assassin.commands.deactivate")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        if (args.length == 2) {
            Player target = Bukkit.getServer().getPlayer(args[1]);
            if (data.isAssassin(target)) {
                assassin.deactivateAssassin(target);
                data.resetActiveTime(target);
                return true;
            }
            else {
                player.sendMessage(ChatColor.RED + "Not an Assassin.");
                return true;
            }
        }
        else {
            if (data.isAssassin(player)) {
                assassin.deactivateAssassin(player);
                data.resetActiveTime(player);
                return true;
            }
            else {
                player.sendMessage(ChatColor.RED + "You aren't an assassin.");
                return true;
            }
        }
    }
}
