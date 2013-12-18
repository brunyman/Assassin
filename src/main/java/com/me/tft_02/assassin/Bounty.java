package com.me.tft_02.assassin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.config.Config;
import com.me.tft_02.assassin.util.player.PlayerData;
import com.me.tft_02.assassin.util.player.UserManager;

import org.kitteh.tag.TagAPI;

public class Bounty {

    private PlayerData data = new PlayerData();

    public void handleBounties(Player player, Player killer) {
        if (hasBounty(player)) {
            // Collect bounty from target
            data.addBountyCollected(killer, data.getKillCount(player));
            data.resetKillCount(player);
            killer.sendMessage(ChatColor.GREEN + "You have collected the bounty! Current bounty collected: " + data.getBountyCollected(killer));
            player.sendMessage(ChatColor.DARK_RED + "Your bounty has been reset!");
        }
        else {
            if (data.isAssassin(UserManager.getPlayer(killer))) {
                // Only increase bounty when attacking a different player without bounty
                data.increaseKillCount(killer);
                TagAPI.refreshPlayer(killer);
            }
        }
    }

    protected boolean hasBounty(Player player) {
        int killCount = data.getKillCount(player);
        return killCount > 0;
    }

    public String getBountyCollectedString(Player player) {
        int bounty_collected = data.getBountyCollected(player);
        int increase_amount = Config.getInstance().getBountyAmount();
        String currency = Config.getInstance().getCurrencyIcon();

        return bounty_collected * increase_amount + currency;
    }
}
