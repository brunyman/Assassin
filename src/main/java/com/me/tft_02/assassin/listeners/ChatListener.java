package com.me.tft_02.assassin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.config.Config;
import com.me.tft_02.assassin.datatypes.player.AssassinPlayer;
import com.me.tft_02.assassin.util.assassin.MessageScrambler;
import com.me.tft_02.assassin.util.Misc;
import com.me.tft_02.assassin.util.player.PlayerData;
import com.me.tft_02.assassin.util.player.UserManager;

public class ChatListener implements Listener {

    private PlayerData data = new PlayerData();
    private MessageScrambler scrambler = new MessageScrambler();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        String message = event.getMessage();

        if (message == null) {
            return;
        }

        Player player = event.getPlayer();
        int number = data.getAssassinNumber(player);

        AssassinPlayer assassinPlayer = UserManager.getPlayer(player);
        if (!assassinPlayer.isAssassin()) {
            return;
        }

        if (!assassinPlayer.isAssassinChatEnabled()) {
            // When not in Assassin chat, show normal message
            // with playerName formatting:  event.setFormat(playerName + message);
            return;
        }

        String prefix = ChatColor.DARK_RED + "(#" + number + ") " + ChatColor.RESET;

        for (Player assassin : data.getOnlineAssassins()) {
            assassin.sendMessage(prefix + message);
        }

        event.setCancelled(true);

        if (!Misc.activationSuccessful(Config.getInstance().getChatEavesdropChance())) {
            return;
        }

        // When in chatting in Assassin chat, other players who are near can hear scrambled chat.
        double chatDistance = Config.getInstance().getChatEavesdropDistance();

        if (chatDistance <= 0) {
            return;
        }

        for (Player players : Assassin.p.getServer().getOnlinePlayers()) {
            if (UserManager.getPlayer(players).isAssassin() || !Misc.isNear(players.getLocation(), player.getLocation(), chatDistance)) {
                event.getRecipients().remove(players);
            }

            // Show scrambled chat messages
            String playerName = ChatColor.DARK_RED + "[ASSASSIN " + number + "]: " + ChatColor.RESET;
            String scrambled = scrambler.Scrambled(message);

            players.sendMessage(playerName + scrambled);
        }
    }
}
