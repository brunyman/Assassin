package com.me.tft_02.assassin.util.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.Player;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.config.Config;
import com.me.tft_02.assassin.datatypes.player.AssassinPlayer;

public class PlayerData {

    // Persistent data
    public static HashSet<String> playerCooldown = new HashSet<String>();
    public static List<String> assassins = new ArrayList<String>();
    public static HashMap<String, Integer> killCount = new HashMap<String, Integer>();
    public static HashMap<String, Integer> bountyCollected = new HashMap<String, Integer>();

    // Non persistent data
    private static HashMap<String, Integer> assassinNumber = new HashMap<String, Integer>();
    private static HashSet<Integer> takenNumbers = new HashSet<Integer>();
    private static HashSet<String> playerNear = new HashSet<String>();

    private final Random random = new Random();

    public void addCooldownTimer(Player player) {
        playerCooldown.add(player.getName());
    }

    public void removeCooldown(Player player) {
        playerCooldown.remove(player.getName());
    }

    public boolean cooledDown(Player player) {
        return !playerCooldown.contains(player.getName());
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
        return firstPlayer.isNeutral() && secondPlayer.isNeutral();
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
