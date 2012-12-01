package me.TfT02.Assassin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import me.TfT02.Assassin.Assassin;

import org.bukkit.ChatColor;
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

//	public long cooldown = Assassin.getInstance().getConfig().getLong("Assassin.cooldown_length"); //30 sec
	public long cooldown = 30L; //30 sec

	public static HashMap<String, String> playerData = new HashMap<String, String>();
//	public static HashMap<String, Long> playerCooldown = new HashMap<String, Long>();
	public static HashSet<String> playerCooldown = new HashSet<String>();
	public static HashMap<String, Location> playerLocation = new HashMap<String, Location>();
	public static HashMap<String, LocationData> playerLocationData = new HashMap<String, LocationData>();

	public static HashMap<String, Long> playerLoginTime = new HashMap<String, Long>();
	public static HashMap<String, Long> playerLogoutTime = new HashMap<String, Long>();
	public static HashMap<String, Long> playerActiveTime = new HashMap<String, Long>();

	public ArrayList<String> assassins = new ArrayList<String>();

	public void addAssassin(Player player) {
		playerData.put(player.getName(), "Assassin");
	}

	public void setNeutral(Player player) {
		playerData.put(player.getName(), "Neutral");
	}

	public void addLoginTime(Player player) {
		long currenttime = System.currentTimeMillis() / 1000L;
		long timestamp = currenttime;
		playerLoginTime.put(player.getName(), timestamp);
	}

	public void addLogoutTime(Player player) {
		long currenttime = System.currentTimeMillis() / 1000L;
		long timestamp = currenttime;
		playerLogoutTime.put(player.getName(), timestamp);
	}

	public void saveActiveTime(Player player){
		long loginTime = playerLoginTime.get(player.getName());
		long logoutTime = playerLogoutTime.get(player.getName());
		long activeTime = logoutTime - loginTime;
		long previousTime = 0;
		if (playerActiveTime.containsKey(player.getName())) {
			previousTime = playerActiveTime.get(player.getName());
		}
		long timeStamp = previousTime + activeTime;
		playerActiveTime.put(player.getName(), timeStamp);
	}

	public Long getActiveTime(Player player) {
		long activetime = 0;
		if (PlayerData.playerActiveTime.containsKey(player.getName())) activetime = PlayerData.playerActiveTime.get(player.getName());
		return activetime;
	}

	public void resetActiveTime(Player player) {
		long activetime = 0;
		PlayerData.playerActiveTime.put(player.getName(), activetime);
	}
	public void addCooldownTimer(Player player){
		playerCooldown.add(player.getName());
	}
	public void removeCooldown(Player player){
		playerCooldown.remove(player.getName());
	}
	public boolean cooledDown(Player player){
		if(playerCooldown.contains(player.getName())) {
			return false;
		}
		return true;
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