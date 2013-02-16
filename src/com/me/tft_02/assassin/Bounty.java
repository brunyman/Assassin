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
        System.out.println(player.getName() + " data.getKillCount(player) " + data.getKillCount(player));
        System.out.println(killer.getName() + " data.getKillCount(killer) " + data.getKillCount(killer));

        if (data.getKillCount(player) > 0) {
            // Collect bounty from target
            data.addBountyCollected(killer, data.getKillCount(player));
            data.resetKillCount(player);
            killer.sendMessage(ChatColor.GREEN + "You have collected the bounty! Current bounty collected: " + data.getBountyCollected(killer));
            player.sendMessage(ChatColor.DARK_RED + "Your bounty has been reset!");
        } else {
            if (data.isAssassin(killer)) {
                // Only increase bounty when attacking a different player without bounty
                data.increaseKillCount(killer);
                TagAPI.refreshPlayer(killer);
            }
        }
    }
}
