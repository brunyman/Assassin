package com.me.tft_02.assassin.runnables;

import java.util.HashSet;

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

    private static HashSet<String> warned = new HashSet<String>();

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
        for (Player assassins : data.getOnlineAssassins()) {
            long activetime = data.getActiveTime(assassins);
            long maxactivetime = Assassin.getInstance().getConfig().getLong("Assassin.active_length");
            long warntime = Assassin.getInstance().getConfig().getLong("Assassin.warn_time_almost_up");

            if (activetime >= maxactivetime) {
                assassin.deactivateAssassin(assassins);
                data.resetActiveTime(assassins);
                if (plugin.debug_mode) {
                    System.out.println(assassins + " status set to Neutral. Active time reached max.");
                }

            } else {
                if (warntime > 0 && (activetime + warntime >= maxactivetime) && !hasBeenWarned(assassins)) {
                    assassins.sendMessage(ChatColor.GOLD + "ASSASSIN MODE WILL GET DEACTIVATED SHORTLY");
                    warned.add(assassins.getName());
                    if (Assassin.getInstance().getConfig().getBoolean("Assassin.particle_effects")) {
                        assassins.getWorld().playEffect(assassins.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
                    }
                    if (plugin.debug_mode) {
                        System.out.println(assassins + " has received a warning because his Assassin mode is running out.");
                    }
                }
            }
        }
    }

    private boolean hasBeenWarned(Player player) {
        if (warned.contains(player.getName())) {
            return true;
        }
        return false;
    }
}