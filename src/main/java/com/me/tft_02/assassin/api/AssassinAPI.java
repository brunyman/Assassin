package com.me.tft_02.assassin.api;

import org.bukkit.entity.Player;

import com.me.tft_02.assassin.util.player.UserManager;

public final class AssassinAPI {

    /**
     * Check if the player is an Assassin.
     *
     * @param player The player to check
     *
     * @return true if player is an Assassin.
     */
    public static boolean isAssassin(Player player) {
        return UserManager.getPlayer(player).isAssassin();
    }

    /**
     * Get the Bounty collected by a player
     *
     * @param player The player to check
     *
     * @return bounty collected
     */
    public static int getBountyCollected(Player player) {
        return UserManager.getPlayer(player).getProfile().getBountyAmount();
    }

    /**
     * Get the Kill count of a player
     *
     * @param player The player to check
     *
     * @return kill count
     */
    public static int getKillCount(Player player) {
        return UserManager.getPlayer(player).getProfile().getKillAmount();
    }
}
