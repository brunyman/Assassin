package me.TfT02.Assassin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.logging.Level;

import me.TfT02.Assassin.Assassin;

import org.bukkit.Bukkit;
import org.bukkit.Location;

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
		} catch (Exception e) {
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
		File f = new File(Assassin.getInstance().getDataFolder(), "data.bin");
		try {
			if (!f.exists()) {
				Assassin.getInstance().getDataFolder().mkdirs();
				f.createNewFile();
			}
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(PlayerData.playerData);
			oos.writeObject(PlayerData.playerCooldown);
			oos.writeObject(PlayerData.playerLocation);
			oos.flush();
			oos.close();
			Assassin.getInstance().getLogger().log(Level.INFO, "Saved data successfully.");
		} catch (Exception e) {
			Assassin.getInstance().getLogger().log(Level.INFO, "Failed to save data.");
			e.printStackTrace();
			return;
		}
	}

	@SuppressWarnings({ "unchecked" })
	public static void loadData() {
		File f = new File(Assassin.getInstance().getDataFolder(), "data.bin");
		if (f.exists()) {
			try {
				@SuppressWarnings("resource") ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
				PlayerData.playerData = (HashMap<String, String>) ois.readObject();
				PlayerData.playerCooldown = (HashMap<String, Long>) ois.readObject();
				PlayerData.playerLocation = (HashMap<String, Location>) ois.readObject();
				Assassin.getInstance().getLogger().log(Level.INFO, "Loaded data successfully.");
			} catch (Exception e) {
				Assassin.getInstance().getLogger().log(Level.INFO, "Failed to load data.");
				e.printStackTrace();
				Bukkit.getServer().getPluginManager().disablePlugin(Assassin.getInstance());
				return;
			}
		}
	}

}