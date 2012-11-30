package me.TfT02.Assassin.util;

import java.util.ArrayList;
import java.util.HashMap;

import me.TfT02.Assassin.Assassin;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerData {

	Assassin plugin;

	public PlayerData(Assassin instance) {
		plugin = instance;
	}

//	/* Toggles */
//	private boolean loaded;
//	private boolean assassin;

//	public long cooldown = plugin.getConfig().getLong("Assassin.cooldown_length"); //30 sec
	public long cooldown = 30L; //30 sec
	public long currenttime = System.currentTimeMillis() / 1000L;

	public static HashMap<String, String> playerData = new HashMap<String, String>();
	public static HashMap<String, Long> playerCooldown = new HashMap<String, Long>();
	public static HashMap<String, Location> playerLocation = new HashMap<String, Location>();
	public ArrayList<String> assassins = new ArrayList<String>();

	public void addAssassin(Player player) {
		playerData.put(player.getName(), "Assassin");
	}

	public void setNeutral(Player player) {
		playerData.put(player.getName(), "Neutral");
	}

	public void addCurrentTimestamp(Player player) {
		long timestamp = currenttime;
		playerCooldown.put(player.getName(), timestamp);
	}

	public void addTimestamp(Player player) {
		long timestamp = currenttime + cooldown;
		playerCooldown.put(player.getName(), timestamp);
	}

	public long getTimestamp(Player player) {
		long timestamp = 0;
		if (playerData.containsKey(player.getName())) {
			timestamp = playerCooldown.get(player.getName());
		} else {

		}
		return timestamp;
	}

	public boolean isReady(Player player) {
		long timestamp = getTimestamp(player);
		if (timestamp < currenttime) {
			return true;
		}
		return false;
	}

	public long getCooldownTime(Player player) {
		long timestamp = getTimestamp(player);
		long cooldownleft = 0;
		if (!isReady(player)) {
			cooldownleft = timestamp - currenttime;
		}
		return cooldownleft;
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
				else if (playerData.get(playername).equalsIgnoreCase("Assassin")) status = "Assassin";
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

		if (statusfirst && statussecond) {
			return true;
		}
		return false;
	}

	public String[] getOnlineAssassins() {
		String[] assassins = new String[playerData.size()];
		assassins = (playerData.keySet().toArray(assassins));
		return assassins;
	}

}