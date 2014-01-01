package com.me.tft_02.assassin.datatypes.player;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.datatypes.ScoreType;
import com.me.tft_02.assassin.datatypes.Status;

public class PlayerProfile {
    private final String playerName;
    private boolean loaded;
    private boolean changed;

    private Status status;
    private int cooldown;
    private int activeTime;
    private Location location;

    private int killAmount;
    private int bountyAmount;

    private final Map<ScoreType, Integer> scoreStats = new HashMap<ScoreType, Integer>();

    public PlayerProfile(String playerName) {
        this.playerName = playerName;

        this.status = Status.NORMAL;
        this.cooldown = 0;
        this.activeTime = 0;

        for (ScoreType scoreType : ScoreType.values()) {
            this.scoreStats.put(scoreType, 0);
        }
    }

    public PlayerProfile(String playerName, boolean isLoaded) {
        this(playerName);
        this.loaded = isLoaded;
    }

    /**
     * Calling this constructor is considered loading the profile.
     */
    public PlayerProfile(String playerName, Map<ScoreType, Integer> argScoreStats, Status argStatus, int argActiveTime, int argCooldown, Location argLocation) {
        this(playerName, true);

        this.scoreStats.putAll(argScoreStats);

        this.status = argStatus;
        this.activeTime = argActiveTime;
        this.cooldown = argCooldown;
        this.location = argLocation;

        loaded = true;
    }

    public void save() {
        if (!changed) {
            return;
        }

        Assassin.getDatabaseManager().saveUser(this);
        changed = false;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        changed = true;

        this.status = status;
    }

    public int getScore(ScoreType scoreType) {
        return scoreStats.get(scoreType);
    }

    public void setScore(ScoreType scoreType, int i) {
        changed = true;

        this.scoreStats.put(scoreType, i);
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        changed = true;

        this.cooldown = cooldown;
    }

    public int getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(int activeTime) {
        changed = true;

        this.activeTime = activeTime;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        changed = true;

        this.location = location;
    }

    public int getKillAmount() {
        return killAmount;
    }

    public void setKillAmount(int killAmount) {
        this.killAmount = killAmount;
    }

    public void increaseKillAmount() {
        setKillAmount(getKillAmount() + 1);
    }

    public int getBountyAmount() {
        return bountyAmount;
    }

    public void setBountyAmount(int bountyAmount) {
        this.bountyAmount = bountyAmount;
    }

    public void addBountyAmount(int bountyAmount) {
        setBountyAmount(getBountyAmount() + bountyAmount);
    }
}
