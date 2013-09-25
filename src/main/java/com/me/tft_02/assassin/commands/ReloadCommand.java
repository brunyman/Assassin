package com.me.tft_02.assassin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.locale.LocaleLoader;
import com.me.tft_02.assassin.util.Permissions;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.reload(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        Assassin.p.reloadConfig();
        sender.sendMessage(LocaleLoader.getString("Commands.Reload.Success"));
        return true;
    }
}
