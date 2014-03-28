package com.me.tft_02.assassin.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.config.Config;
import com.me.tft_02.assassin.util.player.UserManager;

public class Misc {
    private static Random random = new Random();

    public static final int TIME_CONVERSION_FACTOR = 1000;
    public static final int TICK_CONVERSION_FACTOR = 20;

    public static boolean isNPCEntity(Entity entity) {
        return (entity == null || entity.hasMetadata("NPC"));
    }

    /**
     * Determine if two locations are near each other.
     *
     * @param first       The first location
     * @param second      The second location
     * @param maxDistance The max distance apart
     *
     * @return true if the distance between <code>first</code> and <code>second</code> is less than <code>maxDistance</code>, false otherwise
     */
    public static boolean isNear(Location first, Location second, double maxDistance) {
        if (!first.getWorld().equals(second.getWorld())) {
            return false;
        }

        return first.distanceSquared(second) < (maxDistance * maxDistance);

    }

    public static String getStringTimeLeft(Player player) {
        int activetime = UserManager.getPlayer(player).getProfile().getActiveTime();
        int maxactive = Config.getInstance().getActiveLength();
        long time = maxactive - activetime;

        int hours = (int) time / 3600;
        int remainder = (int) time - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;
        if (mins == 0 && hours == 0) {
            return secs + "s";
        }
        if (hours == 0) {
            return mins + "m " + secs + "s";
        }
        else {
            return hours + "h " + mins + "m " + secs + "s";
        }
    }

    public static <K, V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(Map<K, V> map) {

        List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(map.entrySet());

        Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {
            @Override
            public int compare(Entry<K, V> e1, Entry<K, V> e2) {
                return e2.getValue().compareTo(e1.getValue());
            }
        });

        return sortedEntries;
    }

    public static boolean activationSuccessful(int chance) {
        return (Misc.getRandom().nextInt(100) < chance);
    }

    public static Random getRandom() {
        return random;
    }
}
