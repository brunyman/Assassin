package com.me.tft_02.assassin.datatypes.player;

import org.bukkit.entity.Player;

import com.me.tft_02.assassin.Assassin;

public class AssassinPlayer {
    private Player player;
    private PlayerProfile profile;

    private long loginTime;
    private long logoutTime;

    public AssassinPlayer(Player player) {
        String playerName = player.getName();

        this.player = player;
        profile = Assassin.getDatabaseManager().loadPlayerProfile(playerName, true);
    }

    /*
     * Players & Profiles
     */

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public PlayerProfile getProfile() {
        return profile;
    }
}
