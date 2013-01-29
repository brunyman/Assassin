package com.me.tft_02.assassin.Listeners;

import org.bukkit.event.Listener;

import com.me.tft_02.assassin.Assassin;

public class BlockListener implements Listener {
    Assassin plugin;

    public BlockListener(Assassin instance) {
        plugin = instance;
    }

//	@EventHandler(priority = EventPriority.HIGHEST)
//	public void onBlockPlace(BlockPlaceEvent event) {
//		}
}
//TODO Perhaps prevent block breaks from Assassins?
// This might be a simple way to prevent griefers to become anonymous and destroying stuff