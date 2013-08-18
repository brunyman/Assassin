package com.me.tft_02.assassin.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationData {

    private Location location;

    public LocationData(Location location) {
        this.location = location;
    }

    public String convertToString() {
        return location.getWorld().getName() + ", " + String.valueOf(location.getX()) + ", " + String.valueOf(location.getY()) + ", " + String.valueOf(location.getZ());
    }

    public static LocationData convertFromString(String locationData) {
        locationData = locationData.replaceAll("\\s", "");
        String[] locationArray = locationData.split(",");
        String locationWorldName = locationArray[0];
        double locX = Double.parseDouble(locationArray[1]);
        double locY = Double.parseDouble(locationArray[2]);
        double locZ = Double.parseDouble(locationArray[3]);

        return new LocationData(new Location(Bukkit.getWorld(locationWorldName), locX, locY, locZ));
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location l) {
        this.location = l;
    }
}
