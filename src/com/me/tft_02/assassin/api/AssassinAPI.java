package com.me.tft_02.assassin.api;

import org.bukkit.entity.Player;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.util.PlayerData;

public final class AssassinAPI {

    Assassin plugin;

    private AssassinAPI(Assassin instance) {
        plugin = instance;
    }

    private PlayerData data = new PlayerData(plugin);

    /**
     * Check if the player is an Assassin.
     *
     * @param player The player to check
     * @return true if player is an Assassin.
     */
    public boolean isAssassin(Player player) {
        if (data.isAssassin(player)) {
            return true;
        }
        return false;
    }
}
