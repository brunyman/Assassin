package com.me.tft_02.assassin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.util.MessageScrambler;
import com.me.tft_02.assassin.util.PlayerData;

public class ChatListener implements Listener {

    private PlayerData data = new PlayerData(Assassin.p);
    private MessageScrambler message = new MessageScrambler(Assassin.p);

    //	private final Random random = new Random();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        int number = data.getAssassinNumber(player);
        String pName = ChatColor.DARK_RED + "[ASSASSIN " + number + "]: " + ChatColor.RESET;
        String msg = event.getMessage();

        if (msg == null) {
            return;
        }
        if (data.isAssassin(player)) {
            if (data.getAssassinChatMode(player)) {
                String prefix = ChatColor.DARK_RED + "(#" + number + ") " + ChatColor.RESET;
                for (Player assassin : data.getOnlineAssassins()) {
                    assassin.sendMessage(prefix + msg);
                    event.setCancelled(true);
                }

                //When in chatting in Assassin chat, other players who are near can hear scrambled chat.

                //				float diceroll = random.nextInt(100);
                //				int chance = plugin.getConfig().getInt("Assassin.messages_chance");
                //				if (chance > 0 && chance < diceroll){
                double chatDistance = 250;
                if (chatDistance > 0) {
                    for (Player players : Assassin.p.getServer().getOnlinePlayers()) {
                        if (players.getWorld() != player.getWorld() || players.getLocation().distance(player.getLocation()) > chatDistance) {
                            event.getRecipients().remove(players);
                        }
                        else {
                            if (!data.isAssassin(players)) {
                                //Assassins have already received unscrambled message
                                //But this isn't nessecary here... I think
                                //								for (Player assassin : data.getOnlineAssassins()) {
                                //									event.getRecipients().remove(assassin);
                                //								}
                                //Show scrambled chat messages
                                String scrambled = message.Scrambled(msg);
                                //								event.setFormat(pName + scrambled);
                                players.sendMessage(pName + scrambled);
                                event.setCancelled(true);
                            }
                        }
                    }

                }
                //If an Assassin chats, but not in Assassin chat, show normal message with pName formatting
            }
            else {
                event.setFormat(pName + msg);
            }
        }
    }
}
