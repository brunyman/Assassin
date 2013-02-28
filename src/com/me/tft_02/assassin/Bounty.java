package com.me.tft_02.assassin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.kitteh.tag.TagAPI;

import com.me.tft_02.assassin.util.PlayerData;

public class Bounty {

    Assassin plugin;

    public Bounty(Assassin instance) {
        plugin = instance;
    }

    private PlayerData data = new PlayerData(plugin);

    public void handleBounties(Player player, Player killer) {
        if (hasBounty(player)) {
            // Collect bounty from target
            data.addBountyCollected(killer, data.getKillCount(player));
            data.resetKillCount(player);
            killer.sendMessage(ChatColor.GREEN + "You have collected the bounty! Current bounty collected: " + data.getBountyCollected(killer));
            player.sendMessage(ChatColor.DARK_RED + "Your bounty has been reset!");
        }
        else {
            if (data.isAssassin(killer)) {
                // Only increase bounty when attacking a different player without bounty
                data.increaseKillCount(killer);
                TagAPI.refreshPlayer(killer);
            }
        }
    }

    public boolean hasBounty(Player player) {
        int killCount = data.getKillCount(player);
        return killCount > 0;
    }

    public String getBountyCollectedString(Player player) {
        int bounty_collected = data.getBountyCollected(player);
        int increase_amount = Assassin.getInstance().getConfig().getInt("Assassin.bounty_increase_amount");
        String currency = Assassin.getInstance().getConfig().getString("Assassin.bounty_currency");

        return bounty_collected * increase_amount + currency;
    }
}
