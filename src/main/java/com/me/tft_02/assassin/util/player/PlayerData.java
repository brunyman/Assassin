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
    public static List<String> assassins = new ArrayList<String>();

    // Non persistent data
    private static HashMap<String, Integer> assassinNumber = new HashMap<String, Integer>();
    private static HashSet<Integer> takenNumbers = new HashSet<Integer>();

    private final Random random = new Random();

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
}
