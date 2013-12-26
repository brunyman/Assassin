package com.me.tft_02.assassin.runnables.player;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class ApplyPotionsTask extends BukkitRunnable {
    private Player player;
    private List<PotionEffect> potionEffects;

    public ApplyPotionsTask(Player player, List<PotionEffect> potionEffects) {
        this.player = player;
        this.potionEffects = potionEffects;
    }

    @Override
    public void run() {
        for (PotionEffect potionEffect : potionEffects) {
            player.addPotionEffect(potionEffect);
        }
    }
}
