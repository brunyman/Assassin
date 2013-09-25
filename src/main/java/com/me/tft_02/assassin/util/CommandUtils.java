package com.me.tft_02.assassin.util;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.locale.LocaleLoader;

import com.google.common.collect.ImmutableList;

public final class CommandUtils {
    public static final List<String> TRUE_FALSE_OPTIONS = ImmutableList.of("on", "off", "true", "false", "enabled", "disabled");
    public static final List<String> RESET_OPTIONS = ImmutableList.of("clear", "reset");

    private CommandUtils() {}

    public static boolean noConsoleUsage(CommandSender sender) {
        if (sender instanceof Player) {
            return false;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.NoConsole"));
        return true;
    }

    public static boolean isOffline(CommandSender sender, OfflinePlayer player) {
        if (player.isOnline()) {
            return false;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.Offline"));
        return true;
    }

    public static boolean shouldEnableToggle(String arg) {
        return arg.equalsIgnoreCase("on") || arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("enabled");
    }

    public static boolean shouldDisableToggle(String arg) {
        return arg.equalsIgnoreCase("off") || arg.equalsIgnoreCase("false") || arg.equalsIgnoreCase("disabled");
    }
}
