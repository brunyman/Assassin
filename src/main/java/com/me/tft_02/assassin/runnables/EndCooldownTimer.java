package com.me.tft_02.assassin.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import com.me.tft_02.assassin.util.player.UserManager;

public class EndCooldownTimer extends BukkitRunnable {
    private final String playerName;

    public EndCooldownTimer(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void run() {
        UserManager.getPlayer(playerName).setCooledDown(true);
    }
}
