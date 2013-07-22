package com.me.tft_02.assassin.config;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import com.me.tft_02.assassin.Assassin;

public class Config {

    static FileConfiguration config = Assassin.p.getConfig();

    /* @formatter:off */
    /* GENERAL SETTINGS */
    public static boolean getStatsTrackingEnabled() { return config.getBoolean("General.Stats_Tracking_Enabled", true); }
    public static boolean getUpdateCheckEnabled() { return config.getBoolean("General.Update_Check_Enabled", true); }
    public static boolean getPreferBeta() { return config.getBoolean("General.Update_Prefer_Beta", true); }
    public static boolean getDebugModeEnabled() { return config.getBoolean("General.Debug_Mode_Enabled", false); }
    public static int getSaveInterval() { return config.getInt("General.Save_Interval", 15); }

    /* ASSASSIN SETTINGS */
    public static boolean getPreventPVP() { return config.getBoolean("Assassin.Prevent_Neutral_PVP", true); }
    public static int getActiveLength() { return config.getInt("Assassin.Assassin_Mode_Duration", 3600); }
    public static List<String> getBlockedCommands() { return config.getStringList("Assassin.Blocked_Commands"); }

    public static int getWarningTimeDeactivate() { return config.getInt("Assassin.Warn_Time_Almost_Up", 10); }
    public static boolean getTeleportOnDeactivate() { return config.getBoolean("Assassin.Teleport_On_Deactivate", true); }
    public static boolean getReturnMask() { return config.getBoolean("Assassin.Return_Mask", false); }

    public static int getCooldownLength() { return config.getInt("Assassin.Cooldown_Length", 600); }
    public static double getActivationCost() { return config.getDouble("Assassin.Activation_Cost", 0); }

    public static boolean getWarnOnActivate() { return config.getBoolean("Assassin.Warn_Others_On_Activation", true); }
    public static boolean getWarnWhenNear() { return config.getBoolean("Assassin.Warn_Others_When_Near", true); }
    public static int getMessageDistance() { return config.getInt("Assassin.Message_Distance", 250); }

    public static boolean getParticleEffectsEnabled() { return config.getBoolean("Assassin.Particle_Effects_Enabled", true); }
    public static boolean getPotionEffectsEnabled() { return config.getBoolean("Assassin.Potion_Effects_Enabled", true); }

//    public static int getMaxAllowed() { return config.getInt("Assassin.Max_Allowed", 5); }

    public static int getBountyAmount() { return config.getInt("Assassin.Bounty_Increase_Amount", 10); }
    public static String getCurrencyIcon() { return config.getString("Assassin.Bounty_Currency", "$"); }
    /* @formatter:on */
}
