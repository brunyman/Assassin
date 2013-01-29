package com.me.tft_02.assassin.util;

import org.bukkit.Location;

public class Misc {

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
}
