package com.me.tft_02.assassin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.me.tft_02.assassin.Assassin;

public class Data {
    static Assassin plugin;

    public Data(Assassin instance) {
        plugin = instance;
    }

    /*
     * Credits to BlahBerrys
     */

    private static void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                deleteDir(new File(dir, children[i]));
            }
        }
        dir.delete();
    }

    public static void createFiles() throws Exception {
        File configFile = new File(Assassin.getInstance().getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            copy(Assassin.getInstance().getResource("config.yml"), configFile);
            Assassin.getInstance().getLogger().log(Level.INFO, "'config.yml' didn't exist. Created it.");
        }
    }

    public static void saveData() {
        File f = new File(Assassin.getInstance().getDataFolder(), "data.dat");
        try {
            if (!f.exists()) {
                Assassin.getInstance().getDataFolder().mkdirs();
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
            Assassin.getInstance().getLogger().log(Level.INFO, "Saved data successfully.");
        }
        catch (Exception e) {
            Assassin.getInstance().getLogger().log(Level.INFO, "Failed to save data.");
            e.printStackTrace();
        }
    }

    @SuppressWarnings({ "unchecked" })
    public static void loadData() {
        File f = new File(Assassin.getInstance().getDataFolder(), "data.dat");
        if (f.exists()) {
            try {
                @SuppressWarnings("resource")
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                PlayerData.playerData = (HashMap<String, String>) ois.readObject();
                PlayerData.playerCooldown = (HashSet<String>) ois.readObject();
                PlayerData.playerLoginTime = (HashMap<String, Long>) ois.readObject();
                PlayerData.playerLogoutTime = (HashMap<String, Long>) ois.readObject();
                PlayerData.playerActiveTime = (HashMap<String, Long>) ois.readObject();
                PlayerData.assassins = (ArrayList<String>) ois.readObject();
                PlayerData.playerLocationData = (HashMap<String, String>) ois.readObject();
                PlayerData.killCount = (HashMap<String, Integer>) ois.readObject();
                PlayerData.bountyCollected = (HashMap<String, Integer>) ois.readObject();
                Assassin.getInstance().getLogger().log(Level.INFO, "Loaded data successfully.");
            }
            catch (Exception e) {
                Assassin.getInstance().getLogger().log(Level.INFO, "Failed to load data.");
                e.printStackTrace();
                Bukkit.getServer().getPluginManager().disablePlugin(Assassin.getInstance());
            }
        }
    }
}
