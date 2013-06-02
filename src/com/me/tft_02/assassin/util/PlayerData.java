package com.me.tft_02.assassin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.Assassin;

public class PlayerData {

    Assassin plugin;

    public PlayerData(Assassin instance) {
        plugin = instance;
    }

    // Persistent data
    public static HashMap<String, String> playerData = new HashMap<String, String>();
    public static HashSet<String> playerCooldown = new HashSet<String>();
    public static HashMap<String, Long> playerLoginTime = new HashMap<String, Long>();
    public static HashMap<String, Long> playerLogoutTime = new HashMap<String, Long>();
    public static HashMap<String, Long> playerActiveTime = new HashMap<String, Long>();
    public static List<String> assassins = new ArrayList<String>();
    public static HashMap<String, String> playerLocationData = new HashMap<String, String>();
    public static HashMap<String, Integer> killCount = new HashMap<String, Integer>();
    public static HashMap<String, Integer> bountyCollected = new HashMap<String, Integer>();

    // Non persistent data
    public static HashSet<String> assassinChatSet = new HashSet<String>();
    public static HashMap<String, Integer> assassinNumber = new HashMap<String, Integer>();
    public static HashSet<Integer> takenNumbers = new HashSet<Integer>();
    public static HashSet<String> playerNear = new HashSet<String>();

    private final Random random = new Random();

    public void addAssassin(Player player) {
        playerData.put(player.getName(), "Assassin");
        assassins.add(player.getName());
    }

    public void setHostile(Player player) {
        playerData.put(player.getName(), "Hostile");
        assassins.remove(player.getName());
    }

    public void setNeutral(Player player) {
        playerData.put(player.getName(), "Neutral");
        assassins.remove(player.getName());
    }

    public void addLoginTime(Player player) {
        long timestamp = System.currentTimeMillis() / 1000L;
        playerLoginTime.put(player.getName(), timestamp);
    }

    public void addLogoutTime(Player player) {
        long timestamp = System.currentTimeMillis() / 1000L;
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

    public static Long getActiveTime(Player player) {
        long activetime = 0;
        if (PlayerData.playerActiveTime.containsKey(player.getName()))
            activetime = PlayerData.playerActiveTime.get(player.getName());
        return activetime;
    }

    public static Long getActiveTimeLeft(Player player) {
        long activetime = getActiveTime(player);
        long maxactive = Assassin.getInstance().getConfig().getLong("Assassin.active_length");
        return maxactive - activetime;
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
        return !playerCooldown.contains(player.getName());
    }

    public void addLocation(Player player, Location location) {
        playerLocationData.put(player.getName(), new LocationData(location).convertToString());
    }

    public Location getLocation(Player player) {
        if (playerLocationData.containsKey(player.getName())) {
            String locationdata = playerLocationData.get(player.getName());
            return LocationData.convertFromString(locationdata).getLocation();
        }
        else {
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
        return !playerNear.contains(player.getName());
    }

    public boolean isAssassin(Player player) {
        String playername = player.getName();

        if (!playerData.containsKey(playername) || playerData.get(playername) == null) {
            return false;
        }
        return playerData.get(playername).equalsIgnoreCase("Assassin");
    }

    public boolean isHostile(Player player) {
        String playername = player.getName();

        if (!playerData.containsKey(playername) || playerData.get(playername) == null) {
            return false;
        }
        return playerData.get(playername).equalsIgnoreCase("Hostile");
    }

    public boolean isNeutral(Player player) {
        String playername = player.getName();

        if (!playerData.containsKey(playername) || playerData.get(playername) == null) {
            return false;
        }
        return playerData.get(playername).equalsIgnoreCase("Neutral");
    }

    public String getStatus(Player player) {
        String playername = player.getName();
        String status = "null";
        if (!playerData.containsKey(playername)) {
            return status;
        }
        if (playerData.get(playername) == null) {
            status = "null";
            return status;
        }

        if (playerData.get(playername).equalsIgnoreCase("Neutral")) {
            return status = ChatColor.GREEN + "Neutral";
        }
        else if (playerData.get(playername).equalsIgnoreCase("Assassin")) {
            return status = ChatColor.DARK_RED + "Assassin";
        }
        else if (playerData.get(playername).equalsIgnoreCase("Hostile")) {
            return status = ChatColor.RED + "Hostile";
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

        return statusfirst && statussecond;
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
        return assassinChatSet.contains(player.getName());
    }

    public int getAssassinNumber(Player player) {
        String playername = player.getName();
        int number;

        if (assassinNumber.containsKey(playername)) {
            if (assassinNumber.get(playername) == null) {
                number = 0;
            }
            else {
                number = assassinNumber.get(playername);
            }
        }
        else {
            assassinNumber.put(playername, generateRandomNumber());
            number = assassinNumber.get(playername);
        }
        return number;
    }

    public int generateRandomNumber() {
        int randomNumber = random.nextInt(1000);
        boolean check = false;
        while (!check) {
            if (takenNumbers.contains(randomNumber)) {
                randomNumber = random.nextInt(1000);
            }
            else {
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
    public boolean assassinMaximumReached() {
        int maxamount = Assassin.getInstance().getConfig().getInt("Assassin.max_allowed");
        int amount = assassins.size();
        if (maxamount > 0) {
            if (maxamount >= amount) {
                return true;
            }
        }
        return false;
    }

    public int getKillCount(Player player) {
        String playerName = player.getName();
        int kills;

        if (killCount.containsKey(playerName)) {
            kills = killCount.get(playerName);
        }
        else {
            kills = 0;
        }
        return kills;
    }

    public void increaseKillCount(Player player) {
        String playerName = player.getName();
        int kills = getKillCount(player);

        kills = kills + 1;
        killCount.put(playerName, kills);
    }

    public void resetKillCount(Player player) {
        String playerName = player.getName();
        int kills = getKillCount(player);

        if (kills > 0) {
            killCount.put(playerName, 0);
        }
    }

    public int getBountyCollected(Player player) {
        String playerName = player.getName();
        int bounty;
        if (bountyCollected.containsKey(playerName)) {
            bounty = bountyCollected.get(playerName);
        }
        else {
            bounty = 0;
        }
        return bounty;
    }

    public void addBountyCollected(Player player, int amount) {
        String playerName = player.getName();
        int bounty = getBountyCollected(player);

        bounty = bounty + amount;

        bountyCollected.put(playerName, bounty);
    }
}
