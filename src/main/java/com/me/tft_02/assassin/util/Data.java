package com.me.tft_02.assassin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.me.tft_02.assassin.Assassin;

public class Data {

    /*
     * Credits to BlahBerrys
     */

    public static void saveData() {
        File f = new File(Assassin.p.getDataFolder(), "data.dat");
        try {
            if (!f.exists()) {
                Assassin.p.getDataFolder().mkdirs();
                f.createNewFile();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            oos.writeObject(PlayerData.playerData);
            oos.writeObject(PlayerData.playerCooldown);
            oos.writeObject(PlayerData.playerLoginTime);
            oos.writeObject(PlayerData.playerLogoutTime);
            oos.writeObject(PlayerData.playerActiveTime);
            oos.writeObject(PlayerData.assassins);
            oos.writeObject(PlayerData.playerLocationData);
            oos.writeObject(PlayerData.killCount);
            oos.writeObject(PlayerData.bountyCollected);
            oos.flush();
            oos.close();
            Assassin.p.getLogger().log(Level.INFO, "Saved data successfully.");
        }
        catch (Exception e) {
            Assassin.p.getLogger().log(Level.INFO, "Failed to save data.");
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked"})
    public static void loadData() {
        File f = new File(Assassin.p.getDataFolder(), "data.dat");
        if (f.exists()) {
            try {
                @SuppressWarnings("resource")
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                PlayerData.playerData = (HashMap<String, String>) ois.readObject();
                PlayerData.playerCooldown = (HashSet<String>) ois.readObject();
                PlayerData.playerLoginTime = (HashMap<String, Integer>) ois.readObject();
                PlayerData.playerLogoutTime = (HashMap<String, Integer>) ois.readObject();
                PlayerData.playerActiveTime = (HashMap<String, Integer>) ois.readObject();
                PlayerData.assassins = (ArrayList<String>) ois.readObject();
                PlayerData.playerLocationData = (HashMap<String, String>) ois.readObject();
                PlayerData.killCount = (HashMap<String, Integer>) ois.readObject();
                PlayerData.bountyCollected = (HashMap<String, Integer>) ois.readObject();
                Assassin.p.getLogger().log(Level.INFO, "Loaded data successfully.");
            }
            catch (Exception e) {
                Assassin.p.getLogger().log(Level.INFO, "Failed to load data.");
                e.printStackTrace();
                Bukkit.getServer().getPluginManager().disablePlugin(Assassin.p);
            }
        }
    }
}
