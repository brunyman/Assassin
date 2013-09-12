package com.me.tft_02.assassin.runnables.database;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.database.SQLDatabaseManager;
import com.me.tft_02.assassin.util.player.UserManager;

public class SQLReconnectTask extends BukkitRunnable {
    @Override
    public void run() {
        if (((SQLDatabaseManager) Assassin.getDatabaseManager()).checkConnected()) {
            UserManager.saveAll();  // Save all profiles
            UserManager.clearAll(); // Clear the profiles

            for (Player player : Assassin.p.getServer().getOnlinePlayers()) {
                UserManager.addUser(player); // Add in new profiles, forcing them to 'load' again from MySQL
            }
        }
    }
}
