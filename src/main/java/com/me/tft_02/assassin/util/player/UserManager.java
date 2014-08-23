package com.me.tft_02.assassin.util.player;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.datatypes.player.AssassinPlayer;

import com.google.common.collect.ImmutableList;

public final class UserManager {

    private UserManager() {}

    /**
     * Add a new user.
     *
     * @param player The player to create a user record for
     *
     * @return the player's {@link AssassinPlayer} object
     */
    public static AssassinPlayer addUser(Player player) {
        AssassinPlayer AssassinPlayer = new AssassinPlayer(player);
        player.setMetadata(Assassin.playerDataKey, new FixedMetadataValue(Assassin.p, AssassinPlayer));

        return AssassinPlayer;
    }

    /**
     * Remove a user.
     *
     * @param player The Player object
     */
    public static void remove(Player player) {
        player.removeMetadata(Assassin.playerDataKey, Assassin.p);
    }

    /**
     * Clear all users.
     */
    public static void clearAll() {
        for (Player player : Assassin.p.getServer().getOnlinePlayers()) {
            remove(player);
        }
    }

    /**
     * Save all users.
     */
    public static void saveAll() {
        ImmutableList<Player> onlinePlayers = ImmutableList.copyOf(Assassin.p.getServer().getOnlinePlayers());
        Assassin.p.debug("Saving AssassinPlayers... (" + onlinePlayers.size() + ")");

        for (Player player : onlinePlayers) {
            getPlayer(player).getProfile().save();
        }
    }

    public static Collection<AssassinPlayer> getPlayers() {
        Collection<AssassinPlayer> playerCollection = new ArrayList<AssassinPlayer>();

        for (Player player : Assassin.p.getServer().getOnlinePlayers()) {
            playerCollection.add(getPlayer(player));
        }

        return playerCollection;
    }

    /**
     * Get the AssassinPlayer of a player by name.
     *
     * @param playerName The name of the player whose AssassinPlayer to retrieve
     *
     * @return the player's AssassinPlayer object
     */
    public static AssassinPlayer getPlayer(String playerName) {
        return retrieveAssassinPlayer(playerName, false);
    }

    public static AssassinPlayer getOfflinePlayer(OfflinePlayer player) {
        if (player instanceof Player) {
            return getPlayer((Player) player);
        }

        return retrieveAssassinPlayer(player.getName(), true);
    }

    public static AssassinPlayer getOfflinePlayer(String playerName) {
        return retrieveAssassinPlayer(playerName, true);
    }

    public static AssassinPlayer getPlayer(Player player) {
        return (AssassinPlayer) player.getMetadata(Assassin.playerDataKey).get(0).value();
    }

    private static AssassinPlayer retrieveAssassinPlayer(String playerName, boolean offlineValid) {
        Player player = Assassin.p.getServer().getPlayerExact(playerName);

        if (player == null) {
            if (!offlineValid) {
                Assassin.p.getLogger().warning("A valid AssassinPlayer object could not be found for " + playerName + ".");
            }

            return null;
        }

        return getPlayer(player);
    }

    public static boolean hasPlayerDataKey(Entity entity) {
        return entity != null && entity.hasMetadata(Assassin.playerDataKey);
    }
}
