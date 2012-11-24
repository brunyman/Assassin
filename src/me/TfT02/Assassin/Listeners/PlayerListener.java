package me.TfT02.Assassin.Listeners;

import org.bukkit.event.Listener;

import me.TfT02.Assassin.Assassin;

public class PlayerListener implements Listener {
	Assassin plugin;

	public PlayerListener(final Assassin instance) {
		plugin = instance;
	}
}
