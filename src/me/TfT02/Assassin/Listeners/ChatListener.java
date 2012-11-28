package me.TfT02.Assassin.Listeners;

import me.TfT02.Assassin.Assassin;
import me.TfT02.Assassin.util.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
	Assassin plugin;

	public ChatListener(final Assassin instance) {
		plugin = instance;
	}
	private final PlayerData data = new PlayerData(plugin);
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player player = event.getPlayer();
		String msg = event.getMessage();

		if (msg == null)
			return;
		
		if (data.isAssassin(player)){
			event.setFormat(ChatColor.DARK_RED + "[ASSASSIN] " +  ChatColor.RESET + msg);
		}
//		double chatDistance = 250;
//		// Chat Distance Stuff
//		if (chatDistance > 0)
//			for (Player players : plugin.getServer().getOnlinePlayers()) {
//				if (players.getWorld() != player.getWorld() || players.getLocation().distance(player.getLocation()) > chatDistance) {
//					event.getRecipients().remove(players);
//				}
//			}
	}
}
