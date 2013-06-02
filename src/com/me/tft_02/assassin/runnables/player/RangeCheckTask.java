package com.me.tft_02.assassin.runnables.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.util.PlayerData;

public class RangeCheckTask extends BukkitRunnable {

    private PlayerData data = new PlayerData(Assassin.getInstance());

    @Override
    public void run() {
        checkIfAssassinNear();
    }

    public void checkIfAssassinNear() {
        double distance = Assassin.getInstance().getConfig().getDouble("Assassin.messages_distance");
        for (Player players : Assassin.getInstance().getServer().getOnlinePlayers()) {
            for (Player assassin : data.getOnlineAssassins()) {
                if (Assassin.getInstance().debug_mode)
                    System.out.println("Checking if Assassin near.");
                if (distance > 0) {
                    if (players.getWorld().equals(assassin.getWorld()) && players.getLocation().distance(assassin.getLocation()) < distance) {
                        if (Assassin.getInstance().debug_mode) {
                            System.out.println("data.isAssassin(players) " + data.isAssassin(players));
                            System.out.println("data.firstTimeNear(players) " + data.firstTimeNear(players));
                        }
                        if (!data.isAssassin(players) && data.firstTimeNear(players)) {
                            players.sendMessage(ChatColor.DARK_RED + "ASSASSIN SIGHTED!");
                            data.addNearSent(players);
                        }
                        else {
                            //Message already been sent, dont send it again.
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