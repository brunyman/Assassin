package com.me.tft_02.assassin.datatypes.database;

public class PlayerStat {
    public String name;
    public int statVal = 0;

    public PlayerStat(String name, int value) {
        this.name = name;
        this.statVal = value;
    }
}
