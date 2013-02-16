package com.me.tft_02.assassin.runnables;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.AssassinMode;
import com.me.tft_02.assassin.util.PlayerData;

public class ActiveTimer implements Runnable {
    Assassin plugin;

    public ActiveTimer(Assassin instance) {
        plugin = instance;
    }

    private PlayerData data = new PlayerData(plugin);
    private AssassinMode assassin = new AssassinMode(plugin);

    @Override
    public void run() {
        updateActiveTime();
        updateAssassinStatus();
    }

    private void updateActiveTime() {
        for (Player players : Bukkit.getServer().getOnlinePlayers()) {
            if (data.isAssassin(players)) {
                data.addLogoutTime(players);
                data.saveActiveTime(players);
                data.addLoginTime(players);
            }
        }
    }

    private void updateAssassinStatus() {
        for (Player players : Bukkit.getServer().getOnlinePlayers()) {
            long activetime = data.getActiveTime(players);
            long maxactivetime = Assassin.getInstance().getConfig().getLong("Assassin.active_length");
            long warntime = Assassin.getInstance().getConfig().getLong("Assassin.warn_time_almost_up");

            if (activetime >= maxactivetime) {
                if (data.isAssassin(players)) {
                    assassin.deactivateAssassin(players);
                    data.resetActiveTime(players);
                    if (plugin.debug_mode) {
                        System.out.println(players + " status set to Neutral. Active time reached max.");
                    }
                }
            } else if (warntime > 0) {
                if (activetime + warntime >= maxactivetime) {
                    if (data.isAssassin(players)) {
                        players.sendMessage(ChatColor.GOLD + "ASSASSIN MODE WILL GET DEACTIVATED SHORTLY");
                        if (Assassin.getInstance().getConfig().getBoolean("Assassin.particle_effects")) {
                            players.getWorld().playEffect(players.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
                        }
                        if (plugin.debug_mode) {
                            System.out.println(players + " has received a warning because his Assassin mode is running out.");
                        }
                    }
                }
            }
        }
    }
}