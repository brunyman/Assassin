package me.TfT02.Assassin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import me.TfT02.Assassin.Assassin;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerData {

	Assassin plugin;

	public PlayerData(Assassin instance) {
		plugin = instance;
	}

	public static HashMap<String, String> playerData = new HashMap<String, String>();
	public static HashSet<String> playerCooldown = new HashSet<String>();
	public static HashMap<String, Long> playerLoginTime = new HashMap<String, Long>();
	public static HashMap<String, Long> playerLogoutTime = new HashMap<String, Long>();
	public static HashMap<String, Long> playerActiveTime = new HashMap<String, Long>();

	public static List<String> assassins = new ArrayList<String>();
	public static HashSet<String> assassinChatSet = new HashSet<String>();
	public static HashMap<String, Integer> assassinNumber = new HashMap<String, Integer>();
	public static HashSet<Integer> takenNumbers = new HashSet<Integer>();
	public static HashSet<String> playerNear = new HashSet<String>();
	public static HashMap<String, String> playerLocationData = new HashMap<String, String>();

//	public ArrayList<String> assassinsList = new ArrayList<String>();
	private final Random random = new Random();

	public void addAssassin(Player player) {
		playerData.put(player.getName(), "Assassin");
		assassins.add(player.getName());
	}

	public void setNeutral(Player player) {
		playerData.put(player.getName(), "Neutral");
		assassins.remove(player.getName());
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

	public void saveActiveTime(Player player) {
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

	public Long getActiveTimeLeft(Player player) {
		long activetime = getActiveTime(player);
		long maxactive = Assassin.getInstance().getConfig().getLong("Assassin.active_length");
		long activetimeleft = maxactive - activetime;
		return activetimeleft;
	}

	public String getStringTimeLeft(Player player) {
		long time = getActiveTimeLeft(player);
		int hours = (int) time / 3600;
		int remainder = (int) time - hours * 3600;
		int mins = remainder / 60;
		remainder = remainder - mins * 60;
		int secs = remainder;
		if (mins == 0 && hours == 0) return secs + "s";
		if (hours == 0)
			return mins + "m " + secs + "s";
		else
			return hours + "h " + mins + "m " + secs + "s";
	}

	public void resetActiveTime(Player player) {
		long activetime = 0;
		PlayerData.playerActiveTime.put(player.getName(), activetime);
	}

	public void addCooldownTimer(Player player) {
		playerCooldown.add(player.getName());
	}

	public void removeCooldown(Player player) {
		playerCooldown.remove(player.getName());
	}

	public boolean cooledDown(Player player) {
		if (playerCooldown.contains(player.getName())) {
			return false;
		}
		return true;
	}

	public void addLocation(Player player, Location location) {
		playerLocationData.put(player.getName(), new LocationData(location).convertToString());
	}

	public Location getLocation(Player player) {
		if (playerLocationData.containsKey(player.getName())) {
			String locationdata = playerLocationData.get(player.getName());
			Location location = LocationData.convertFromString(locationdata).getLocation();
			return location;
		} else {
			System.out.println("No location data found for " + player + "!");
			System.out.println("Perhaps 'Assassin/data.dat' has been deleted?");
			return null;
		}
	}

	public void addNearSent(Player player) {
		playerNear.add(player.getName());
	}

	public void removeNearSent(Player player) {
		playerNear.remove(player.getName());
	}

	public boolean firstTimeNear(Player player) {
		if (playerNear.contains(player.getName())) {
			return false;
		}
		return true;
	}

	public boolean isAssassin(Player player) {
		if (playerData.containsKey(player.getName())) {
			if (playerData.get(player.getName()) == null) {
			} else if (playerData.get(player.getName()).equalsIgnoreCase("Assassin")) {
				return true;
			}
		}
		return false;
	}

	public boolean isNeutral(Player player) {
		String playername = player.getName();
		if (playerData.containsKey(playername)) {
			if (playerData.get(playername) == null) {
			} else if (playerData.get(playername).equalsIgnoreCase("Neutral")) {
				return true;
			}
		}
		return false;
	}

	public String getStatus(Player player) {
		String playername = player.getName();
		String status = "null";
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
	 * Check if two players are both Neutral.
	 * 
	 * @param firstPlayer The first player
	 * @param secondPlayer The second player
	 * @return true if they are both Neutral, false otherwise
	 */
	public boolean bothNeutral(Player firstPlayer, Player secondPlayer) {
		boolean statusfirst = isNeutral(firstPlayer);
		boolean statussecond = isNeutral(secondPlayer);

		if (statusfirst && statussecond) {
			return true;
		}
		return false;
	}

	public List<String> getAssassins() {
		return assassins;
	}

	public List<Player> getOnlineAssassins() {
		Player[] onlinePlayers = Assassin.getInstance().getServer().getOnlinePlayers();
		List<Player> onlineAssassins = new ArrayList<Player>();

		for (Player onlinePlayer : onlinePlayers) {
			if (assassins.contains(onlinePlayer.getName())) {
				onlineAssassins.add(onlinePlayer);
			}
		}
		return onlineAssassins;
	}

	public void enterAssassinChat(Player player) {
		assassinChatSet.add(player.getName());
	}

	public void leaveAssassinChat(Player player) {
		assassinChatSet.remove(player.getName());
	}

	public boolean getAssassinChatMode(Player player) {
		if (assassinChatSet.contains(player.getName())) {
			return true;
		}
		return false;
	}

	public int getAssassinNumber(Player player) {
		String playername = player.getName();
		int number;

		if (assassinNumber.containsKey(playername)) {
			if (assassinNumber.get(playername) == null) {
				number = 0;
			} else {
				number = assassinNumber.get(playername);
			}
		} else {
			assassinNumber.put(playername, generateRandomNumber());
			number = assassinNumber.get(playername);
		}
		return number;
	}

	public int generateRandomNumber() {
		int randomNumber = random.nextInt(1000);
		boolean check = false;
		while(check == false){
		    if(takenNumbers.contains(randomNumber)){
		        randomNumber = random.nextInt(1000);
		    }else{
		        check = true;
		        takenNumbers.add(randomNumber);
		    }
		}
		return randomNumber;
	}

	/**
	 * Check if the maximum amount of assassins is reached
	 * 
	 * @return true if maximum is reached, false otherwise
	 */	
	public boolean assassinMaximumReached(){
		int maxamount = Assassin.getInstance().getConfig().getInt("Assassin.max_allowed");
		int amount = assassins.size();
		if (maxamount > 0){
			if (maxamount >= amount) {
				return true;
			}
		}
		return false;
	}
}