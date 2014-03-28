package com.me.tft_02.assassin.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.config.Config;
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

    public static List<String> getOnlinePlayerNames(CommandSender sender) {
        Player player = sender instanceof Player ? (Player) sender : null;
        List<String> onlinePlayerNames = new ArrayList<String>();

        for (Player onlinePlayer : Assassin.p.getServer().getOnlinePlayers()) {
            if (player != null && player.canSee(onlinePlayer)) {
                onlinePlayerNames.add(onlinePlayer.getName());
            }
        }

        return onlinePlayerNames;
    }

    /**
     * Attempts to match any player names with the given name, and returns a list of all possibly matches.
     * <p/>
     * This list is not sorted in any particular order.
     * If an exact match is found, the returned list will only contain a single result.
     *
     * @param partialName Name to match
     *
     * @return List of all possible names
     */
    public static List<String> matchPlayer(String partialName) {
        List<String> matchedPlayers = new ArrayList<String>();

        for (OfflinePlayer offlinePlayer : Assassin.p.getServer().getOfflinePlayers()) {
            String playerName = offlinePlayer.getName();

            if (partialName.equalsIgnoreCase(playerName)) {
                // Exact match
                matchedPlayers.clear();
                matchedPlayers.add(playerName);
                break;
            }
            if (playerName.toLowerCase().contains(partialName.toLowerCase())) {
                // Partial match
                matchedPlayers.add(playerName);
            }
        }

        return matchedPlayers;
    }

    /**
     * Get a matched player name if one was found in the database.
     *
     * @param partialName Name to match
     *
     * @return Matched name or {@code partialName} if no match was found
     */
    public static String getMatchedPlayerName(String partialName) {
        if (Config.getInstance().getMatchOfflinePlayers()) {
            List<String> matches = matchPlayer(partialName);

            if (matches.size() == 1) {
                partialName = matches.get(0);
            }

        }
        else {
            Player player = Assassin.p.getServer().getPlayer(partialName);
            if (player != null) {
                partialName = player.getName();
            }
        }

        return partialName;
    }
}
