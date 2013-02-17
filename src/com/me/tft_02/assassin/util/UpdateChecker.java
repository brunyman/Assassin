package com.me.tft_02.assassin.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import com.me.tft_02.assassin.Assassin;

public class UpdateChecker {
    Assassin plugin;

    public UpdateChecker(Assassin instance) {
        plugin = instance;
    }

    public boolean getUpdate() throws Exception {
        String version = plugin.getDescription().getVersion();
        String urlString = "http://dev.bukkit.org/server-mods/assassin/files.rss";
        URL url = new URL(urlString);
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(url.openStream());
        } catch (UnknownHostException e) {
            return false; //Cannot connect
        }
        BufferedReader in = new BufferedReader(isr);
        String line;
        int lineNum = 0;
        while ((line = in.readLine()) != null) {
            if (line.length() != line.replace("<title>", "").length()) {
                line = line.replaceAll("\\s+(?i)(<title.*?>)(.+?)(</title>)", "$2").substring(1);
                if (lineNum == 1) {
                    String[] newTokens = line.split("[.]");
                    String[] oldTokens = version.split("[.]");

                    for (int i = 0; i < 3; i++) {
                        Integer newVer = Integer.parseInt(newTokens[i]);
                        Integer oldVer = Integer.parseInt(oldTokens[i]);
                        if (oldVer < newVer) {
                            return true; //They are using an old version
                        }
                    }
                    return false; //They are up to date!
                }
                lineNum = lineNum + 1;
            }
        }
        in.close();
        return false;
    }
}
