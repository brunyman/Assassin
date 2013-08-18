package com.me.tft_02.assassin.runnables.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.util.PlayerData;

public class RangeCheckTask extends BukkitRunnable {

    private PlayerData data = new PlayerData(Assassin.p);

    @Override
    public void run() {
        checkIfAssassinNear();
    }

    public void checkIfAssassinNear() {
        double distance = Assassin.p.getConfig().getDouble("Assassin.messages_distance");
        for (Player players : Assassin.p.getServer().getOnlinePlayers()) {
            for (Player assassin : data.getOnlineAssassins()) {
                Assassin.p.debug("Checking if Assassin near.");
                if (distance > 0) {
                    if (players.getWorld().equals(assassin.getWorld()) && players.getLocation().distance(assassin.getLocation()) < distance) {
                        Assassin.p.debug("data.isAssassin(players) " + data.isAssassin(players));
                        Assassin.p.debug("data.firstTimeNear(players) " + data.firstTimeNear(players));

                        if (!data.isAssassin(players) && data.firstTimeNear(players)) {
                            players.sendMessage(ChatColor.DARK_RED + "ASSASSIN SIGHTED!");
                            data.addNearSent(players);
                        }
                    }
                    if (players.getWorld().equals(assassin.getWorld()) && players.getLocation().distance(assassin.getLocation()) > distance) {
                        if (!data.isAssassin(players) && !data.firstTimeNear(players)) {
                            data.removeNearSent(players);
                        }
                    }
                }
            }
        }
    }
}
