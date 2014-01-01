package com.me.tft_02.assassin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.config.Config;
import com.me.tft_02.assassin.datatypes.player.AssassinPlayer;
import com.me.tft_02.assassin.datatypes.player.PlayerProfile;
import com.me.tft_02.assassin.util.player.UserManager;

import org.kitteh.tag.TagAPI;

public class Bounty {

    public void handleBounties(Player player, Player killer) {
        AssassinPlayer assassinKiller = UserManager.getPlayer(killer);
        PlayerProfile killerProfile = assassinKiller.getProfile();

        if (hasBounty(assassinKiller)) {
            // Collect bounty from target
            PlayerProfile playerProfile = UserManager.getPlayer(player).getProfile();
            killerProfile.addBountyAmount(playerProfile.getKillAmount());
            playerProfile.setKillAmount(0);

            killer.sendMessage(ChatColor.GREEN + "You have collected the bounty! Current bounty collected: " + killerProfile.getBountyAmount());
            player.sendMessage(ChatColor.DARK_RED + "Your bounty has been reset!");
        }
        else if (assassinKiller.isAssassin()) {
            // Only increase bounty when attacking a different player without bounty
            killerProfile.increaseKillAmount();
            TagAPI.refreshPlayer(killer);
        }
    }

    protected boolean hasBounty(AssassinPlayer assassinPlayer) {
        return assassinPlayer.getProfile().getKillAmount() > 0;
    }

    public String getBountyCollectedString(Player player) {
        int bounty_collected = UserManager.getPlayer(player).getProfile().getBountyAmount();
        int increase_amount = Config.getInstance().getBountyAmount();
        String currency = Config.getInstance().getCurrencyIcon();

        return bounty_collected * increase_amount + currency;
    }
}
