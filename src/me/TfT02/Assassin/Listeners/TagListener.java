package me.TfT02.Assassin.Listeners;

import me.TfT02.Assassin.Assassin;
import me.TfT02.Assassin.util.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

public class TagListener implements Listener {

	Assassin plugin;

	public TagListener(Assassin instance) {
		plugin = instance;
	}

	private final PlayerData data = new PlayerData(plugin);

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onNameTag(PlayerReceiveNameTagEvent event) {
		Player player = event.getPlayer();
		Player namedPlayer = event.getNamedPlayer();
		if (data.isAssassin(namedPlayer)) {
			event.setTag(ChatColor.DARK_RED + "[ASSASSIN]");
			System.out.println("Changed player tag to [ASSASSIN] for " + namedPlayer.getName());
		} else {
			event.setTag(ChatColor.WHITE + namedPlayer.getDisplayName());
			System.out.println("Reset player tag for " + namedPlayer.getName());
		}
//		for(Player p : getServer().getOnlinePlayers()){
//		if (event.getPlayer().getName().equals(player)) {
//			event.setTag(ChatColor.DARK_GRAY + "PLAYER");
//		}
	}
}