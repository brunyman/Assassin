package me.TfT02.Assassin;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class PlayerData {

	Assassin plugin;

	public PlayerData(final Assassin instance) {
		plugin = instance;
	}

//	/* Toggles */
//	private boolean loaded;
//	private boolean assassin;

	public static HashMap<String, String> playerData = new HashMap<String, String>();

	public void addAssassin(Player player) {
		playerData.put(player.getName(), "Assassin");
	}

	public void setNeutral(Player player) {
		playerData.put(player.getName(), "Neutral");
	}

	public boolean isAssassin(Player player) {
		if (playerData.containsKey(player.getName())) {
			if (playerData.get(player.getName()) == null) {
				//Null and stuff
			} else if (playerData.get(player.getName()).equalsIgnoreCase("Assassin")) {
				return true;
			}
			//no data here
		}
		return false;
	}

	public boolean isNeutral(Player player) {
		String playername = player.getName();
		if (playerData.containsKey(playername)) {
			if (playerData.get(playername) == null) {
				//Null and stuff
			} else if (playerData.get(playername).equalsIgnoreCase("Neutral")) {
				return true;
			}
			//no data here
		}
		return false;
	}

	public String getStatus(Player player) {
		String playername = player.getName();
		String status = "IDK";
		if (playerData.containsKey(playername)) {
			if (playerData.get(playername) == null) {
				status = "null";
			} else {
				if (playerData.get(playername).equalsIgnoreCase("Neutral"))
					status = "Neutral";
				else if (playerData.get(playername).equalsIgnoreCase("Assassin"))
					status = "Assassin";
			}
		}
		return status;
	}
}