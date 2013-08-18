package com.me.tft_02.assassin.runnables.player;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.AssassinMode;
import com.me.tft_02.assassin.config.Config;
import com.me.tft_02.assassin.datatypes.Status;
import com.me.tft_02.assassin.util.PlayerData;
import com.me.tft_02.assassin.util.player.UserManager;

public class ActivityTimerTask extends BukkitRunnable {

    private PlayerData data = new PlayerData();
    private AssassinMode assassin = new AssassinMode();

    private static HashSet<String> warned = new HashSet<String>();

    @Override
    public void run() {
        updateActiveTime();
        updateAssassinStatus();
    }

    private void updateActiveTime() {
        for (Player players : Assassin.p.getServer().getOnlinePlayers()) {
            if (data.isAssassin(players)) {
                data.addLogoutTime(players);
                data.saveActiveTime(players);
                data.addLoginTime(players);
            }
        }
    }

    private void updateAssassinStatus() {
        long maxactivetime = Config.getInstance().getActiveLength();
//TODO Loop assassins instead
        for (Player player : Assassin.p.getServer().getOnlinePlayers()) {
            if (!(data.isAssassin(player) || data.isHostile(player))) {
                continue;
            }

            long activetime = PlayerData.getActiveTime(player);

            if (activetime >= maxactivetime) {
                if (data.isAssassin(player)) {
                    assassin.deactivateAssassin(player);
                }
                else if (data.isHostile(player)) {
                    UserManager.getPlayer(player).setStatus(Status.NORMAL);
                }
                data.resetActiveTime(player);
                Assassin.p.debug(player + " status set to Neutral. Active time reached max.");
            }
            else {
                long warntime = Config.getInstance().getWarningTimeDeactivate();
                if (warntime > 0) {
                    if (activetime + warntime >= maxactivetime && !hasBeenWarned(player)) {
                        player.sendMessage(ChatColor.GOLD + "ASSASSIN MODE WILL GET DEACTIVATED SHORTLY");
                        warned.add(player.getName());

                        if (Config.getInstance().getParticleEffectsEnabled()) {
                            player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
                        }

                        Assassin.p.debug(player + " has received a warning because his Assassin mode is running out.");
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
