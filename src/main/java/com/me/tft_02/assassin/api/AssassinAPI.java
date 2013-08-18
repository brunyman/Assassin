package com.me.tft_02.assassin.api;

import org.bukkit.entity.Player;

import com.me.tft_02.assassin.util.PlayerData;

public final class AssassinAPI {

    private AssassinAPI() {
    }

    private PlayerData data = new PlayerData();

    /**
     * Check if the player is an Assassin.
     *
     * @param player The player to check
     * @return true if player is an Assassin.
     */
    public boolean isAssassin(Player player) {
        return data.isAssassin(player);
    }

    /**
     * Get the Bounty collected by a player
     *
     * @param player The player to check
     * @return bounty collected
     */
    public int getBountyCollected(Player player) {
        return data.getBountyCollected(player);
    }

    /**
     * Get the Kill count of a player
     *
     * @param player The player to check
     * @return kill count
     */
    public int getKillCount(Player player) {
        return data.getKillCount(player);
    }
}
