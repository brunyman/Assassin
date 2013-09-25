package com.me.tft_02.assassin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.AssassinMode;
import com.me.tft_02.assassin.util.CommandUtils;
import com.me.tft_02.assassin.util.Permissions;
import com.me.tft_02.assassin.util.player.PlayerData;
import com.me.tft_02.assassin.util.player.UserManager;

public class DeactivateCommand implements CommandExecutor {

    private AssassinMode assassin = new AssassinMode();
    private PlayerData data = new PlayerData();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        Player player = (Player) sender;

        if (!Permissions.deactivate(player)) {
            player.sendMessage(command.getPermissionMessage());
            return true;
        }

        OfflinePlayer target = player;
        if (args.length == 2) {
            target = Bukkit.getServer().getOfflinePlayer(args[1]);
        }

        if (CommandUtils.isOffline(sender, target)) {
            return true;
        }

        if (data.isAssassin(UserManager.getPlayer(target))) {
            assassin.deactivateAssassin((Player) target);
            data.resetActiveTime((Player) target);
            return true;
        }
        else {
            player.sendMessage(ChatColor.RED + target.getName() + " is not an Assassin.");
            return true;
        }
    }
}
