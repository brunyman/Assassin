package com.me.tft_02.assassin.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.util.assassin.AssassinManager;
import com.me.tft_02.assassin.datatypes.player.AssassinPlayer;
import com.me.tft_02.assassin.locale.LocaleLoader;
import com.me.tft_02.assassin.util.CommandUtils;
import com.me.tft_02.assassin.util.Permissions;
import com.me.tft_02.assassin.util.player.UserManager;

public class DeactivateCommand implements CommandExecutor {

    private AssassinManager assassin = new AssassinManager();

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

        Player targetPlayer = (Player) target;
        AssassinPlayer assassinPlayer = UserManager.getPlayer(targetPlayer);

        if (assassinPlayer.isAssassin()) {
            assassin.deactivateAssassin(targetPlayer);
            assassinPlayer.resetActiveTime();
            return true;
        }
        else {
            player.sendMessage(LocaleLoader.getString("Commands.NotAnAssassin", target.getName()));
            return true;
        }
    }
}
