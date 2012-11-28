package me.TfT02.Assassin.Listeners;

import org.bukkit.event.Listener;

import me.TfT02.Assassin.Assassin;

public class BlockListener implements Listener {
	Assassin plugin;

	public BlockListener(final Assassin instance) {
		plugin = instance;
	}
	//TODO Perhaps prevent block breaks from Assassins?
	// This might be a simple way to prevent griefers to become anonymous and destroying stuff
}
