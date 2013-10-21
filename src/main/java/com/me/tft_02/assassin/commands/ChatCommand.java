package com.me.tft_02.assassin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.locale.LocaleLoader;
import com.me.tft_02.assassin.util.CommandUtils;
import com.me.tft_02.assassin.util.Permissions;
import com.me.tft_02.assassin.util.player.PlayerData;
import com.me.tft_02.assassin.util.player.UserManager;

public class ChatCommand implements CommandExecutor {

    private PlayerData data = new PlayerData();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        Player player = (Player) sender;

        if (!Permissions.chat(player)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        if (!data.isAssassin(UserManager.getPlayer(player))) {
            player.sendMessage(LocaleLoader.getString("Commands.NotAnAssassin", player.getName()));
            return true;
        }

        toggleAssassinChat(player);
        return true;
    }

    private void toggleAssassinChat(Player player) {
        if (!data.getAssassinChatMode(player)) {
            data.enterAssassinChat(player);
            player.sendMessage(LocaleLoader.getString("Commands.Chat.On"));
        }
        else {
            data.leaveAssassinChat(player);
            player.sendMessage(LocaleLoader.getString("Commands.Chat.Off"));
        }
    }
}
