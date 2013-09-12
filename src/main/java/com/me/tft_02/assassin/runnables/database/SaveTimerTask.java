package com.me.tft_02.assassin.runnables.database;

import org.bukkit.scheduler.BukkitRunnable;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.datatypes.player.AssassinPlayer;
import com.me.tft_02.assassin.runnables.player.PlayerProfileSaveTask;
import com.me.tft_02.assassin.util.player.UserManager;

public class SaveTimerTask extends BukkitRunnable {

    @Override
    public void run() {
        // All player data will be saved periodically through this
        int count = 1;

        for (AssassinPlayer mcMMOPlayer : UserManager.getPlayers()) {
            new PlayerProfileSaveTask(mcMMOPlayer.getProfile()).runTaskLater(Assassin.p, count);
            count++;
        }

    }
}
