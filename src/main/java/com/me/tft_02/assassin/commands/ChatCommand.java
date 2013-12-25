package com.me.tft_02.assassin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.datatypes.player.AssassinPlayer;
import com.me.tft_02.assassin.locale.LocaleLoader;
import com.me.tft_02.assassin.util.CommandUtils;
import com.me.tft_02.assassin.util.Permissions;
import com.me.tft_02.assassin.util.player.UserManager;

public class ChatCommand implements CommandExecutor {

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

        if (!UserManager.getPlayer(player).isAssassin()) {
            player.sendMessage(LocaleLoader.getString("Commands.NotAnAssassin", player.getName()));
            return true;
        }

        toggleAssassinChat(player);
        return true;
    }

    private void toggleAssassinChat(Player player) {
        AssassinPlayer assassinPlayer = UserManager.getPlayer(player);

        if (!assassinPlayer.isAssassinChatEnabled()) {
            player.sendMessage(LocaleLoader.getString("Commands.Chat.On"));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Commands.Chat.Off"));
        }

        assassinPlayer.toggleAssassinChat();
    }
}
