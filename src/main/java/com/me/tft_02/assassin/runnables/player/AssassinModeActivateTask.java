package com.me.tft_02.assassin.runnables.player;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.me.tft_02.assassin.util.Misc;

public class AssassinModeActivateTask extends BukkitRunnable {
    private Player player;

    public AssassinModeActivateTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        player.sendMessage(ChatColor.DARK_RED + "YOU ARE NOW AN ASSASSIN");
        player.sendMessage(ChatColor.GOLD + "Time left in Assassin Mode = " + ChatColor.DARK_RED + Misc.getStringTimeLeft(player));
        player.getWorld().playSound(player.getLocation(), Sound.PISTON_RETRACT, 1.0f, 1.0f);
    }
}
