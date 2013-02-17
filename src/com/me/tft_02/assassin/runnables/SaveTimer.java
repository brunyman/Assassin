package com.me.tft_02.assassin.runnables;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.util.Data;

public class SaveTimer implements Runnable {
    Assassin plugin;

    public SaveTimer(Assassin instance) {
        plugin = instance;
    }
    @Override
    public void run() {
        saveData();
    }

    private void saveData() {
        Data.saveData();
    }
}