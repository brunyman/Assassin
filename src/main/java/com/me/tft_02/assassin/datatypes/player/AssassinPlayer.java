package com.me.tft_02.assassin.datatypes.player;

import org.bukkit.entity.Player;

import com.me.tft_02.assassin.datatypes.Status;

public class AssassinPlayer {
    private Player player;

    private Status status;

    public AssassinPlayer(Player player) {
        this.player = player;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
