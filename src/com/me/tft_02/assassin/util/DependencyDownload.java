package com.me.tft_02.assassin.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;

public class DependencyDownload {
	/*
	 * Credits: VoidWhisperer
	 * http://forums.bukkit.org/threads/dependency-download-enable.98955/
	 */
	static String uri = "http://dev.bukkit.org/media/files/665/723/TagAPI.jar";
	static String pluginName = "plugins/TagAPI.jar";
	static String pluginActualName = "TagAPI";
	static String pluginOutputTag = "[Assassin]";
	final static int size = 1024;

	public static void download() {
		try {
			URL url = new URL(uri);
			URLConnection urlC = url.openConnection();
			InputStream is = urlC.getInputStream();
			byte[] buffer = new byte[size];
			int ByteRead = 0;
			@SuppressWarnings("resource") OutputStream os = new BufferedOutputStream(new FileOutputStream(pluginName));
			while ((ByteRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, ByteRead);
			}
			os.flush();
			Bukkit.getLogger().info(pluginOutputTag + " Saved file " + pluginName + "... Now attempting to enable!");
			Bukkit.getPluginManager().loadPlugin(new File(pluginName));
			Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().getPlugin(pluginActualName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}