package com.me.tft_02.assassin.datatypes.player;

import org.bukkit.entity.Player;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.datatypes.Status;
import com.me.tft_02.assassin.util.StringUtils;

public class AssassinPlayer {
    private Player player;
    private PlayerProfile profile;

    private boolean assassinChatMode;

    private int loginTime;
    private int logoutTime;

    public AssassinPlayer(Player player) {
        String playerName = player.getName();

        this.player = player;
        profile = Assassin.getDatabaseManager().loadPlayerProfile(playerName, true);
    }

    /*
     * Players & Profiles
     */

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public PlayerProfile getProfile() {
        return profile;
    }

    public boolean isAssassin() {
        return profile.getStatus() == Status.ASSASSIN;
    }

    public boolean isHostile() {
        return profile.getStatus() == Status.HOSTILE;
    }

    public boolean isNeutral() {
        return profile.getStatus() == Status.NORMAL;
    }

    public String getStatus() {
        return StringUtils.getCapitalized(profile.getStatus().toString());
    }

    /*
     * Chat modes
     */

    public boolean isAssassinChatEnabled() {
        return assassinChatMode;
    }

    public void disableAssassinChat() {
        assassinChatMode = false;
    }

    public void enableAssassinChat() {
        assassinChatMode = true;
    }

    public void toggleAssassinChat() {
        assassinChatMode = !assassinChatMode;
        return;
    }

    public int getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(int loginTime) {
        this.loginTime = loginTime;
    }

    public void actualizeLoginTime() {
        setLoginTime((int) System.currentTimeMillis() / 1000);
    }

    public int getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(int logoutTime) {
        this.logoutTime = logoutTime;
    }

    public void actualizeLogoutTime() {
        setLogoutTime((int) System.currentTimeMillis() / 1000);
    }

    public void actualizeActiveTime() {
        int activeTime = getLogoutTime() - getLoginTime();
        int previousTime = profile.getActiveTime();

        profile.setActiveTime(previousTime + activeTime);
    }

    public void resetActiveTime() {
        profile.setActiveTime(0);
    }
}
