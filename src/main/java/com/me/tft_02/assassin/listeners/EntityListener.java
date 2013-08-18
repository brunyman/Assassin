package com.me.tft_02.assassin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.config.Config;
import com.me.tft_02.assassin.util.Misc;
import com.me.tft_02.assassin.util.PlayerData;

public class EntityListener implements Listener {

    private PlayerData data = new PlayerData(Assassin.p);

    /**
     * Monitor EntityDamageByEntity events.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamage() <= 0) {
            return;
        }

        Entity attacker = event.getDamager();
        Entity defender = event.getEntity();

        if (Misc.isNPCEntity(attacker) || Misc.isNPCEntity(defender)) {
            return;
        }

        if (attacker instanceof Projectile) {
            attacker = ((Projectile) attacker).getShooter();
        }
        else if (attacker instanceof Tameable) {
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
                if (Config.getInstance().getParticleEffectsEnabled()) {
                    defendingPlayer.getWorld().playEffect(defendingPlayer.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
                }
            }
        }
    }

    /**
     * Check EntityDamageByEntity events.
     *
     * @param event The event to check
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onEntityDamageByEntityHighest(EntityDamageByEntityEvent event) {
        if (event.getDamage() <= 0) {
            return;
        }

        Entity attacker = event.getDamager();
        Entity defender = event.getEntity();

        if (Misc.isNPCEntity(attacker) || Misc.isNPCEntity(defender)) {
            return;
        }

        if (attacker instanceof Projectile) {
            attacker = ((Projectile) attacker).getShooter();
        }
        else if (attacker instanceof Tameable) {
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
                if (data.bothNeutral(defendingPlayer, (Player) attacker) && Config.getInstance().getPreventPVP()) {
                    ((Player) attacker).sendMessage(ChatColor.DARK_RED + "You are not an Assassin.");
                    event.setCancelled(true);
                }
                else {
                    if (event.isCancelled() && Config.getInstance().getOverridePVP()) {
                        event.setCancelled(false);
                    }
                }
            }
        }
    }
}
