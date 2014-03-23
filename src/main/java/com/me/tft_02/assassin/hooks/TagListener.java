package com.me.tft_02.assassin.hooks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.datatypes.player.AssassinPlayer;
import com.me.tft_02.assassin.util.player.UserManager;

import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;

public class TagListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAsyncPlayerReceiveNameTag(AsyncPlayerReceiveNameTagEvent event) {
        Player namedPlayer = event.getNamedPlayer();
        AssassinPlayer assassinPlayer = UserManager.getPlayer(namedPlayer);
        String tag = event.getTag();

        if (!tag.contains("[ASSASSIN]")) {
            assassinPlayer.setPreviousTag(tag);
        }

        if (assassinPlayer.isAssassin()) {
            event.setTag(ChatColor.DARK_RED + "[ASSASSIN] " + assassinPlayer.getProfile().getKillAmount());

            Assassin.p.debug("Changed player tag to [ASSASSIN] for " + namedPlayer.getName());
        }
        else {
            event.setTag(assassinPlayer.getPreviousTag());

            Assassin.p.debug("Reset player tag for " + namedPlayer.getName());
        }

        //		if (!data.isAssassin(player)) {
        //			event.setTag(ChatColor.DARK_GRAY + "PLAYER");
        //			System.out.println("Changed player tag to Player for " + player.getName());
        //		} else {
        //			event.setTag(ChatColor.RESET + player.getDisplayName());
        //			System.out.println("Reset player tag for " + player.getName());
        //		}
        //		for(Player p : getServer().getOnlinePlayers()){
        //		if (event.getPlayer().getName().equals(player)) {
        //			event.setTag(ChatColor.DARK_GRAY + "PLAYER");
        //		}
    }
}
