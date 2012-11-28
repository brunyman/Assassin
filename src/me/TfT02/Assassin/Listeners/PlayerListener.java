package me.TfT02.Assassin.Listeners;

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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerJoinEvent;

import me.TfT02.Assassin.Assassin;
import me.TfT02.Assassin.util.PlayerData;

public class PlayerListener implements Listener {
	Assassin plugin;

	public PlayerListener(final Assassin instance) {
		plugin = instance;
	}

	private final PlayerData data = new PlayerData(plugin);

	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!data.isAssassin(player)) {
			data.setNeutral(player);
		}
		String status = data.getStatus(player);
		player.sendMessage(ChatColor.YELLOW + "Your status = " + ChatColor.RED + status);
	}

	@EventHandler
	void onInventoryClick(InventoryClickEvent event) {
		HumanEntity player = event.getWhoClicked();
		if (data.isAssassin((Player) player)) {
			SlotType slotType = event.getSlotType();
			switch (slotType) {
			case ARMOR:
				event.setCancelled(true);
			default:
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		String name = player.getDisplayName();
		EntityDamageEvent de = player.getLastDamageCause();

		boolean isEntityInvolved = false;
		if (de instanceof EntityDamageByEntityEvent) {
			isEntityInvolved = true;
		}
		DamageCause cause = de.getCause();
		if (isEntityInvolved) {
			EntityDamageByEntityEvent edbe = (EntityDamageByEntityEvent) de;
			Entity damager = edbe.getDamager();
			Projectile projectile = (Projectile) damager;

			boolean isAssassinInvolved = false;
			if (!data.bothNeutral(player, (Player) damager)) {
				isAssassinInvolved = true;
			}
			if (isAssassinInvolved){

				String deathmessage = event.getDeathMessage();
				String damagername = ((Player) damager).getDisplayName();
				if (deathmessage.contains(damagername)){
					damagername = ChatColor.DARK_RED + "[ASSASSIN]";
				}


//				if (cause == DamageCause.ENTITY_ATTACK && damager instanceof Player) {
////					event.setDeathMessage("Player " + name + " killed by" + damager);
//					event.setDeathMessage(damager + " Assassin has killed" + name);
//				}
//				else if (cause == DamageCause.PROJECTILE && projectile instanceof Arrow) {
//					if (projectile.getShooter() instanceof Player) {
//						Player shooter = (Player) projectile.getShooter();
////						event.setDeathMessage("Player " + name + " got shot by " + other.getDisplayName());
//						event.setDeathMessage(shooter.getDisplayName() + " Assassin has killed" + name);
//					}
//				}
			}
		}
	}
}