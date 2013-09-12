package com.me.tft_02.assassin.util.player;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.datatypes.player.AssassinPlayer;
import com.me.tft_02.assassin.util.Misc;

public final class UserManager {
    private final static Map<String, AssassinPlayer> players = new HashMap<String, AssassinPlayer>();

    private UserManager() {
    }

    /**
     * Add a new user.
     *
     * @param player The player to create a user record for
     *
     * @return the player's {@link AssassinPlayer} object
     */
    public static AssassinPlayer addUser(Player player) {
        String playerName = player.getName();
        AssassinPlayer assassinPlayer = players.get(playerName);

        if (assassinPlayer != null) {
            assassinPlayer.setPlayer(player); // The player object is different on each reconnection and must be updated
        }
        else {
            assassinPlayer = new AssassinPlayer(player);
            players.put(playerName, assassinPlayer);
        }

        return assassinPlayer;
    }

    /**
     * Remove a user.
     *
     * @param playerName The name of the player to remove
     */
    public static void remove(String playerName) {
        players.remove(playerName);
    }

    /**
     * Clear all users.
     */
    public static void clearAll() {
        players.clear();
    }

    /**
     * Save all users.
     */
    public static void saveAll() {
        for (AssassinPlayer assassinPlayer : players.values()) {
            assassinPlayer.getProfile().save();
        }
    }

    public static Set<String> getPlayerNames() {
        return players.keySet();
    }

    public static Collection<AssassinPlayer> getPlayers() {
        return players.values();
    }

    /**
     * Get the AssassinPlayer of a player by a partial name.
     *
     * @param playerName The partial name of the player whose AssassinPlayer to retrieve
     *
     * @return the player's AssassinPlayer object
     */
    public static AssassinPlayer getPlayer(String playerName) {
        List<String> matches = Misc.matchPlayer(playerName);

        for (String match : matches) {
            System.out.println(match);
        }
        if (matches.size() == 1) {
            playerName = matches.get(0);
        }

        return players.get(playerName);
    }

    /**
     * +     * Get the AssassinPlayer of a player by the exact name.
     * +     *
     * +     * @param playerName The exact name of the player whose AssassinPlayer to retrieve
     * +     * @return the player's {@link AssassinPlayer} object
     * +
     */
    public static AssassinPlayer getPlayerExact(String playerName) {
        return players.get(playerName);
    }

    /**
     * Get the AssassinPlayer of a player.
     *
     * @param player The player whose AssassinPlayer to retrieve
     *
     * @return the player's {@link AssassinPlayer} object
     */
    public static AssassinPlayer getPlayer(OfflinePlayer player) {
        return players.get(player.getName());
    }
}
