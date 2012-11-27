package me.TfT02.Assassin.util;

import java.util.HashMap;

import me.TfT02.Assassin.Assassin;

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
	public static HashMap<String, Long> playerCooldown = new HashMap<String, Long>();

	public void addAssassin(Player player) {
		playerData.put(player.getName(), "Assassin");
	}

	public void setNeutral(Player player) {
		playerData.put(player.getName(), "Neutral");
	}

	public void addTimestamp(Player player) {
		long timestamp = System.currentTimeMillis() / 1000L;
		playerCooldown.put(player.getName(), timestamp);
	}

	public long getTimestamp(Player player) {
		long timestamp = 0;
		if (playerData.containsKey(player.getName())) {
			if (playerData.get(player.getName()) == null) {
				timestamp = 0;
			} else 
				timestamp = playerCooldown.get(player.getName());
		}
		return timestamp;
	}
	public boolean isReady(Player player){
		long timestamp = getTimestamp(player);
		long cooldown = 600; //10 min
		long currenttime =  System.currentTimeMillis() / 1000L;
		if ((timestamp + cooldown) < currenttime){
			return true;
		}
		return false;
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

	/**
	 * Check if two players are in the same party.
	 * 
	 * @param firstPlayer The first player
	 * @param secondPlayer The second player
	 * @return true if they are in the same party, false otherwise
	 */
	public boolean bothNeutral(Player firstPlayer, Player secondPlayer) {
		boolean statusfirst = isNeutral(firstPlayer);
		boolean statussecond = isNeutral(secondPlayer);

		if (statusfirst == statussecond) {
			return true;
		}
		return false;
	}

}