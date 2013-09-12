package com.me.tft_02.assassin.util.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.config.Config;
import com.me.tft_02.assassin.datatypes.Status;
import com.me.tft_02.assassin.datatypes.player.AssassinPlayer;
import com.me.tft_02.assassin.util.LocationData;
import com.me.tft_02.assassin.util.Misc;
import com.me.tft_02.assassin.util.StringUtils;

public class PlayerData {

    // Persistent data
    public static HashSet<String> playerCooldown = new HashSet<String>();
    public static HashMap<String, Integer> playerLoginTime = new HashMap<String, Integer>();
    public static HashMap<String, Integer> playerLogoutTime = new HashMap<String, Integer>();
    public static HashMap<String, Integer> playerActiveTime = new HashMap<String, Integer>();
    public static List<String> assassins = new ArrayList<String>();
    public static HashMap<String, String> playerLocationData = new HashMap<String, String>();
    public static HashMap<String, Integer> killCount = new HashMap<String, Integer>();
    public static HashMap<String, Integer> bountyCollected = new HashMap<String, Integer>();

    // Non persistent data
    private static HashSet<String> assassinChatSet = new HashSet<String>();
    private static HashMap<String, Integer> assassinNumber = new HashMap<String, Integer>();
    private static HashSet<Integer> takenNumbers = new HashSet<Integer>();
    private static HashSet<String> playerNear = new HashSet<String>();

    private final Random random = new Random();

    public void addLoginTime(Player player) {
        playerLoginTime.put(player.getName(), Misc.getSystemTime());
    }

    public void addLogoutTime(Player player) {
        playerLogoutTime.put(player.getName(), Misc.getSystemTime());
    }

    public void saveActiveTime(Player player) {
        int loginTime = playerLoginTime.get(player.getName());
        int logoutTime = playerLogoutTime.get(player.getName());
        int activeTime = logoutTime - loginTime;
        int previousTime = 0;
        if (playerActiveTime.containsKey(player.getName())) {
            previousTime = playerActiveTime.get(player.getName());
        }

        int totalActiveTime = previousTime + activeTime;
        playerActiveTime.put(player.getName(), totalActiveTime);
    }

    public static int getActiveTime(Player player) {
        int activetime = 0;
        if (PlayerData.playerActiveTime.containsKey(player.getName())) {
            activetime = PlayerData.playerActiveTime.get(player.getName());
        }
        return activetime;
    }

    public static int getActiveTimeLeft(Player player) {
        int activetime = getActiveTime(player);
        int maxactive = Config.getInstance().getActiveLength();
        return maxactive - activetime;
    }

    public void resetActiveTime(Player player) {
        PlayerData.playerActiveTime.put(player.getName(), 0);
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
            Assassin.p.debug("No location data found for " + player + "!");
            Assassin.p.debug("Perhaps 'Assassin/data.dat' has been deleted?");
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

    public boolean isAssassin(AssassinPlayer assassinPlayer) {
        return assassinPlayer.getProfile().getStatus() == Status.ASSASSIN;
    }

    public boolean isHostile(AssassinPlayer assassinPlayer) {
        return assassinPlayer.getProfile().getStatus() == Status.HOSTILE;
    }

    boolean isNeutral(AssassinPlayer assassinPlayer) {
        return assassinPlayer.getProfile().getStatus() == Status.NORMAL;
    }

    public String getStatus(Player player) {
        return StringUtils.getCapitalized(UserManager.getPlayer(player).getProfile().getStatus().toString());
    }

    /**
     * Check if two players are both Neutral.
     *
     * @param firstPlayer  The first player
     * @param secondPlayer The second player
     *
     * @return true if they are both Neutral, false otherwise
     */
    public boolean bothNeutral(Player firstPlayer, Player secondPlayer) {
        return bothNeutral(UserManager.getPlayer(firstPlayer), UserManager.getPlayer(secondPlayer));
    }

    public boolean bothNeutral(AssassinPlayer firstPlayer, AssassinPlayer secondPlayer) {
        return isNeutral(firstPlayer) && isNeutral(secondPlayer);
    }

    public List<String> getAssassins() {
        return assassins;
    }

    public List<Player> getOnlineAssassins() {
        Player[] onlinePlayers = Assassin.p.getServer().getOnlinePlayers();
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

        if (!assassinNumber.containsKey(playername)) {
            assassinNumber.put(playername, generateRandomNumber());
        }

        return assassinNumber.get(playername);
    }

    int generateRandomNumber() {
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
        int maxAllowed = Config.getInstance().getMaxAllowed();
        int amount = assassins.size();

        return (maxAllowed > 0 && maxAllowed >= amount);
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
