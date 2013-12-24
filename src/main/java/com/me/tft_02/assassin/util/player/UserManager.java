package com.me.tft_02.assassin.util.player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.datatypes.player.AssassinPlayer;

public final class UserManager {
    private final static Map<String, AssassinPlayer> players = new HashMap<String, AssassinPlayer>();

    private UserManager() {}

    /**
     * Add a new user.
     *
     * @param player The player to create a user record for
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
     * Get the AssassinPlayer of a player by name.
     *
     * @param playerName The name of the player whose AssassinPlayer to retrieve
     * @return the player's AssassinPlayer object
     */
    public static AssassinPlayer getPlayer(String playerName) {
        return retrieveAssassinPlayer(playerName, false);
    }

    /**
     * Get the AssassinPlayer of a player.
     *
     * @param player The player whose AssassinPlayer to retrieve
     * @return the player's AssassinPlayer object
     */
    public static AssassinPlayer getPlayer(OfflinePlayer player) {
        return retrieveAssassinPlayer(player.getName(), false);
    }

    public static AssassinPlayer getPlayer(OfflinePlayer player, boolean offlineValid) {
        return retrieveAssassinPlayer(player.getName(), offlineValid);
    }

    public static AssassinPlayer getPlayer(String playerName, boolean offlineValid) {
        return retrieveAssassinPlayer(playerName, offlineValid);
    }

    private static AssassinPlayer retrieveAssassinPlayer(String playerName, boolean offlineValid) {
        AssassinPlayer assassinPlayer = players.get(playerName);

        if (assassinPlayer == null) {
            Player player = Assassin.p.getServer().getPlayerExact(playerName);

            if (player == null) {
                if (!offlineValid) {
                    Assassin.p.getLogger().warning("A valid AssassinPlayer object could not be found for " + playerName + ".");
                }

                return null;
            }

            assassinPlayer = new AssassinPlayer(player);
            players.put(playerName, assassinPlayer);
        }

        return assassinPlayer;
    }
}
