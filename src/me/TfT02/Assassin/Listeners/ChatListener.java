package me.TfT02.Assassin.Listeners;

import java.util.Random;

import me.TfT02.Assassin.Assassin;
import me.TfT02.Assassin.util.MessageScrambler;
import me.TfT02.Assassin.util.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
	Assassin plugin;

	public ChatListener(Assassin instance) {
		plugin = instance;
	}

	private PlayerData data = new PlayerData(plugin);
	private MessageScrambler message = new MessageScrambler(plugin);
	private final Random random = new Random();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String pName = ChatColor.DARK_RED + "[ASSASSIN]: " + ChatColor.RESET;
		String msg = event.getMessage();

		if (msg == null)
			return;

		if (data.getAssassinChatMode(player)) {
			for (Player assassin : data.getOnlineAssassins()) {
//				String number = data.getAssassinNumber(player);
				String number = "1";
				String prefix = ChatColor.DARK_GRAY + "(" + ChatColor.DARK_RED + number + ChatColor.DARK_GRAY + ") ";
				assassin.sendMessage(prefix + msg);
			}
			float diceroll = random.nextInt(100);
			int chance = 10; //TODO Config
			double chatDistance = 250;
			if (chance > 0 && chance < diceroll){
				if (chatDistance > 0){
					for (Player players : plugin.getServer().getOnlinePlayers()) {
						if (players.getWorld() != player.getWorld() || players.getLocation().distance(player.getLocation()) > chatDistance) {
							event.getRecipients().remove(players);
						}
						else {
							//Show scrambled chat messages
							String scrambled = message.Scrambled(msg);
							event.setFormat(pName + scrambled);
						}
					}
				}
			}
		}
		else {
			//Do nothing with chat
		}
	}



//		else
//			event.setFormat(pName + msg);
//	}
//		if (data.isAssassin(player)){
//			if (data.getAssassinChatMode(player)){
//				float diceroll = random.nextInt(100);
//				int chance = 10; //TODO Config
//				double chatDistance = 250;
//				for (Player players : plugin.getServer().getOnlinePlayers()) {
//					if (players.getWorld() != player.getWorld() || players.getLocation().distance(player.getLocation()) > chatDistance) {
//						if (chance < diceroll){
//							event.getRecipients().remove(players);
//						}
//					}
//				}
//				for (Player assassin : data.getOnlineAssassins()) {
//					event.setFormat(pName + msg);
//				}
//				String scrambled = message.Scrambled(msg);
//				event.setFormat(pName + scrambled);
//				//TODO Assassin must understand it properly without range and shit
//			}


//		double chatDistance = 250;
//		// Chat Distance Stuff
//		if (chatDistance > 0)
//			for (Player players : plugin.getServer().getOnlinePlayers()) {
//				if (players.getWorld() != player.getWorld() || players.getLocation().distance(player.getLocation()) > chatDistance) {
//					event.getRecipients().remove(players);
//				}
//			}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		String name = player.getName();
		EntityDamageEvent de = player.getLastDamageCause();

		boolean isEntityInvolved = false;
		if (de instanceof EntityDamageByEntityEvent) {
			isEntityInvolved = true;
		}
		if (isEntityInvolved) {
			EntityDamageByEntityEvent edbe = (EntityDamageByEntityEvent) de;
			Entity damager = edbe.getDamager();
			if (data.isAssassin(player)){
				String deathmessage = event.getDeathMessage();
				String newmsg = deathmessage.replaceAll(name, ChatColor.DARK_RED + "[ASSASSIN]" + ChatColor.RESET );
				event.setDeathMessage(newmsg);
			}
			if (data.isAssassin((Player) damager)){
				String damagername = ((HumanEntity) damager).getName();
				String deathmessage = event.getDeathMessage();
				String newmsg = deathmessage.replaceAll(damagername, ChatColor.DARK_RED + "[ASSASSIN]" + ChatColor.RESET );
				event.setDeathMessage(newmsg);
			}			
		}
		else if (data.isAssassin(player)){
			String deathmessage = event.getDeathMessage();
			String newmsg = deathmessage.replaceAll(name, ChatColor.DARK_RED + "[ASSASSIN]" + ChatColor.RESET );
			event.setDeathMessage(newmsg);
		}
	}
}