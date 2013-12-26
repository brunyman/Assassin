package com.me.tft_02.assassin.hooks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.me.tft_02.assassin.util.player.UserManager;
import com.me.tft_02.ghosts.events.tomb.TombCreateEvent;

public class GhostsListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreateTomb(TombCreateEvent event) {
        Player player = event.getPlayer();

        if (UserManager.getPlayer(player).isAssassin()) {
            event.setCancelled(true);
        }
    }
}
