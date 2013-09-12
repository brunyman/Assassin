package com.me.tft_02.assassin.runnables;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.util.UpdateChecker;

/**
 * Async task
 */
public class UpdateCheckerTask implements Runnable {
    @Override
    public void run() {
        try {
            Assassin.p.updateCheckerCallback(UpdateChecker.updateAvailable());
        }
        catch (Exception e) {
            Assassin.p.updateCheckerCallback(false);
        }
    }
}
