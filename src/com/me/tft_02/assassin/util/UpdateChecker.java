package com.me.tft_02.assassin.util;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.me.tft_02.assassin.Assassin;

public class UpdateChecker {
    Assassin plugin;

    public UpdateChecker(Assassin instance) {
        plugin = instance;
    }

    public static boolean updateAvailable() throws Exception {
        String checkType = "release";
        if (Assassin.getInstance().getConfig().getBoolean("General.prefer_beta")) {
            checkType = "latest";
        }
        String version = Assassin.getInstance().getDescription().getVersion();
        URL url = new URL("http://api.bukget.org/api2/bukkit/plugin/assassin/" + checkType);
        InputStreamReader isr;
        try {
            isr = new InputStreamReader(url.openStream());
        }
        catch (UnknownHostException e) {
            return false;
        }

        String newVersion;
        try {
            JSONParser jp = new JSONParser();
            Object o = jp.parse(isr);

            if (!(o instanceof JSONObject)) {
                isr.close();
                return false;
            }

            JSONObject jo = (JSONObject) o;
            jo = (JSONObject) jo.get("versions");
            newVersion = (String) jo.get("version");

            String[] oldTokens = version.split("[.]");
            String[] newTokens = newVersion.split("[.]");

            for (int i = 0; i < 3; i++) {
                Integer newVer = Integer.parseInt(newTokens[i]);
                Integer oldVer = Integer.parseInt(oldTokens[i]);
                if (oldVer < newVer) {
                    isr.close();
                    return true;
                }
            }
            return false;
        }
        catch (ParseException e) {
            isr.close();
            return false;
        }
    }
}
