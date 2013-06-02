package com.me.tft_02.assassin.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import com.me.tft_02.assassin.util.Data;

public class SaveTimerTask extends BukkitRunnable {

    @Override
    public void run() {
        Data.saveData();
    }
}
