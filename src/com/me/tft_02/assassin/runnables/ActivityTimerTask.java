package com.me.tft_02.assassin.runnables;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.AssassinMode;
import com.me.tft_02.assassin.util.PlayerData;

public class ActivityTimerTask extends BukkitRunnable {

    private PlayerData data = new PlayerData(Assassin.getInstance());
    private AssassinMode assassin = new AssassinMode(Assassin.getInstance());

    private static HashSet<String> warned = new HashSet<String>();

    @Override
    public void run() {
        updateActiveTime();
        updateAssassinStatus();
    }

    private void updateActiveTime() {
        for (Player players : Assassin.getInstance().getServer().getOnlinePlayers()) {
            if (data.isAssassin(players)) {
                data.addLogoutTime(players);
                data.saveActiveTime(players);
                data.addLoginTime(players);
            }
        }
    }

    private void updateAssassinStatus() {
        long maxactivetime = Assassin.getInstance().getConfig().getLong("Assassin.active_length");
        long warntime = Assassin.getInstance().getConfig().getLong("Assassin.warn_time_almost_up");

        for (Player player : Assassin.getInstance().getServer().getOnlinePlayers()) {
            long activetime = data.getActiveTime(player);

            if (activetime >= maxactivetime) {
                if (data.isAssassin(player)) {
                    assassin.deactivateAssassin(player);
                }
                else if (data.isHostile(player)) {
                    data.setNeutral(player);
                }
                data.resetActiveTime(player);
                if (Assassin.getInstance().debug_mode) {
                    System.out.println(player + " status set to Neutral. Active time reached max.");
                }
            }
            else {
                if ((data.isAssassin(player) || data.isHostile(player)) && warntime > 0) {
                    if (activetime + warntime >= maxactivetime) {
                        if (!hasBeenWarned(player)) {
                            player.sendMessage(ChatColor.GOLD + "ASSASSIN MODE WILL GET DEACTIVATED SHORTLY");
                            warned.add(player.getName());
                            if (Assassin.getInstance().getConfig().getBoolean("Assassin.particle_effects")) {
                                player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
                            }
                            if (Assassin.getInstance().debug_mode) {
                                System.out.println(player + " has received a warning because his Assassin mode is running out.");
                            }
                        }
                    }
                    else {
                        warned.remove(player.getName());
                    }
                }
            }
        }
    }

    private boolean hasBeenWarned(Player player) {
        return warned.contains(player.getName());
    }
}
