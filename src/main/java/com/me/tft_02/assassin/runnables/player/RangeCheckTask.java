package com.me.tft_02.assassin.runnables.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.config.Config;
import com.me.tft_02.assassin.datatypes.player.AssassinPlayer;
import com.me.tft_02.assassin.util.Misc;
import com.me.tft_02.assassin.util.player.PlayerData;
import com.me.tft_02.assassin.util.player.UserManager;

public class RangeCheckTask extends BukkitRunnable {

    private PlayerData data = new PlayerData();

    @Override
    public void run() {
        checkIfAssassinNear();
    }

    protected void checkIfAssassinNear() {
        double distance = Config.getInstance().getMessageDistance();

        if (distance <= 0) {
            return;
        }

        for (Player player : Assassin.p.getServer().getOnlinePlayers()) {
            AssassinPlayer assassinPlayer = UserManager.getPlayer(player);
            if (assassinPlayer.isAssassin()) {
                break;
            }

            for (Player assassin : data.getOnlineAssassins()) {
                if (Misc.isNear(player.getLocation(), assassin.getLocation(), distance) && assassinPlayer.firstTimeNear()) {
                    player.sendMessage(ChatColor.DARK_RED + "ASSASSIN SIGHTED!");
                    assassinPlayer.setNearSent(true);
                }
                else {
                    assassinPlayer.setNearSent(false);
                }
            }
        }
    }
}
