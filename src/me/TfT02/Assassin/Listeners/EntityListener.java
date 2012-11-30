package me.TfT02.Assassin.Listeners;

import me.TfT02.Assassin.Assassin;
import me.TfT02.Assassin.util.PlayerData;

import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class EntityListener implements Listener {

	Assassin plugin;

	public EntityListener(Assassin instance) {
		plugin = instance;
	}

	private PlayerData data = new PlayerData(plugin);

	/**
	 * Monitor EntityDamageByEntity events.
	 * 
	 * @param event The event to monitor
	 */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
//        if (event instanceof FakeEntityDamageByEntityEvent)
//            return;

		if (event.getDamage() <= 0) return;

		Entity attacker = event.getDamager();
		Entity defender = event.getEntity();

		if (attacker.hasMetadata("NPC") || defender.hasMetadata("NPC")) return; // Check if either players is are Citizens NPCs

		if (attacker instanceof Projectile) {
			attacker = ((Projectile) attacker).getShooter();
		} else if (attacker instanceof Tameable) {
			AnimalTamer animalTamer = ((Tameable) attacker).getOwner();

			if (animalTamer instanceof Entity) {
				attacker = (Entity) animalTamer;
			}
		}

		if (defender instanceof Player) {
			Player defendingPlayer = (Player) defender;

			if (!defendingPlayer.isOnline()) {
				return;
			}

			if (attacker instanceof Player) {
				if (data.bothNeutral(defendingPlayer, (Player) attacker)) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}
