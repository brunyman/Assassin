package com.me.tft_02.assassin.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.Assassin;

public class Misc {

    Assassin plugin;

    public Misc(Assassin instance) {
        plugin = instance;
    }

    private PlayerData data = new PlayerData(plugin);

    /**
     * Determine if two locations are near each other.
     * 
     * @param first The first location
     * @param second The second location
     * @param maxDistance The max distance apart
     * @return true if the distance between <code>first</code> and <code>second</code> is less than <code>maxDistance</code>, false otherwise
     */
    public static boolean isNear(Location first, Location second, double maxDistance) {
        if (!first.getWorld().equals(second.getWorld())) {
            return false;
        }

        if (first.distanceSquared(second) < (maxDistance * maxDistance)) {
            return true;
        }

        return false;
    }

    public String getStringTimeLeft(Player player) {
        long time = data.getActiveTimeLeft(player);
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

    static <K, V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(Map<K, V> map) {

        List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(map.entrySet());

        Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {
            @Override
            public int compare(Entry<K, V> e1, Entry<K, V> e2) {
                return e2.getValue().compareTo(e1.getValue());
            }
        });

        return sortedEntries;
    }
}
