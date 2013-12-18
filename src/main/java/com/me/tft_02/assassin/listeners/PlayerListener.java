package com.me.tft_02.assassin.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.AssassinMode;
import com.me.tft_02.assassin.Bounty;
import com.me.tft_02.assassin.config.Config;
import com.me.tft_02.assassin.datatypes.player.AssassinPlayer;
import com.me.tft_02.assassin.runnables.EndCooldownTimer;
import com.me.tft_02.assassin.util.BlockChecks;
import com.me.tft_02.assassin.util.ItemChecks;
import com.me.tft_02.assassin.util.Misc;
import com.me.tft_02.assassin.util.Permissions;
import com.me.tft_02.assassin.util.player.PlayerData;
import com.me.tft_02.assassin.util.player.UserManager;

import net.milkbowl.vault.economy.EconomyResponse;

public class PlayerListener implements Listener {
    private AssassinMode assassin = new AssassinMode();
    private Bounty bounty = new Bounty();
    private PlayerData data = new PlayerData();
    private ItemChecks itemcheck = new ItemChecks();

    /**
     * Monitor PlayerJoin events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (Misc.isNPCEntity(player)) {
            return;
        }

        AssassinPlayer assassinPlayer = UserManager.addUser(player);

        if (Assassin.p.isUpdateAvailable() && player.isOp()) {
            player.sendMessage(ChatColor.DARK_RED + "[Assassin]: " + ChatColor.GOLD + "New version available on BukkitDev!");
            player.sendMessage(ChatColor.DARK_RED + "[Assassin]: " + ChatColor.AQUA + "http://dev.bukkit.org/server-mods/Assassin/");
        }

        if (data.isAssassin(assassinPlayer)) {
            event.setJoinMessage(ChatColor.DARK_RED + "AN ASSASSIN JOINED THE GAME");
            assassin.applyTraits(player);
            assassin.applyMaskForce(player);
        }

        if (!data.cooledDown(player)) {
            long cooldowntime = Config.getInstance().getCooldownLength();
            new EndCooldownTimer(player.getName()).runTaskLater(Assassin.p, cooldowntime);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        if (data.isAssassin(UserManager.getPlayer(player))) {
            assassin.applyMaskForce(player);

            if (!Config.getInstance().getPotionEffectsEnabled()) {
                return;
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(Assassin.p, new Runnable() {
                @Override
                public void run() {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Misc.TICK_CONVERSION_FACTOR * 5, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Misc.TICK_CONVERSION_FACTOR * 30, 1));
                }
            }, 5);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (Misc.isNPCEntity(player)) {
            return;
        }

        AssassinPlayer assassinPlayer = UserManager.getPlayer(player);

        if (data.isAssassin(assassinPlayer)) {
            event.setQuitMessage(ChatColor.DARK_RED + "AN ASSASSIN LEFT THE GAME");
            data.addLogoutTime(player);
            data.saveActiveTime(player);
            data.leaveAssassinChat(player);
        }

        assassinPlayer.getProfile().save();
        UserManager.remove(player.getName());
    }

    /**
     * Check PlayerInteract events.
     *
     * @param event The event to check
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block block = event.getClickedBlock();
        ItemStack inHand = player.getItemInHand();

        switch (action) {
            case RIGHT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
                if ((inHand.getTypeId() != 35) || !BlockChecks.abilityBlockCheck(block)) {
                    return;
                }

                if (!itemcheck.isMask(inHand)) {
                    return;
                }

                event.setCancelled(true);

                if (!Permissions.maskUse(player)) {
                    player.sendMessage(ChatColor.RED + "You haven't got permission.");
                    return;
                }

                if (!data.cooledDown(player)) {
                    player.sendMessage(ChatColor.RED + "You need to wait before you can use that again...");
                    return;
                }

                if (data.isAssassin(UserManager.getPlayer(player))) {
                    player.sendMessage(ChatColor.RED + "You already are an Assassin.");
                    return;
                }

                double activation_cost = Config.getInstance().getActivationCost();
                if (Assassin.p.vaultEnabled && activation_cost > 0) {
                    EconomyResponse r = Assassin.econ.withdrawPlayer(player.getName(), activation_cost);

                    if (r.transactionSuccess()) {
                        player.sendMessage(String.format(ChatColor.RED + "You were charged %s %s", Assassin.econ.format(r.amount), Assassin.econ.currencyNamePlural()));
                    }
                    else {
                        player.sendMessage(String.format("An error occured: %s", r.errorMessage));
                        return;
                    }
                }

                Assassin.p.debug("Activating AssassinMode for " + player.getName());
                assassin.activateAssassin(player);
                long cooldowntime = Config.getInstance().getCooldownLength();
                new EndCooldownTimer(player.getName()).runTaskLater(Assassin.p, cooldowntime);

                return;
            default:
                break;
        }
    }

    //
    //	@EventHandler
    //	public void onItemDrop(PlayerDropItemEvent event) {
    //		ItemStack droppeditem = event.getItemDrop().getItemStack();
    //		if (itemcheck.isMask(droppeditem)) {
    //			event.setCancelled(true);
    //			event.getPlayer().sendMessage(ChatColor.RED + "You're not allowed to drop masks.");
    //		}
    //	}

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDeathLowest(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!data.isAssassin(UserManager.getPlayer(player))) {
            return;
        }

        List<ItemStack> drops = event.getDrops();
        for (ItemStack drop : drops) {
            if (itemcheck.isMask(drop)) {
                drops.remove(drop);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeathHighest(PlayerDeathEvent event) {
        Player player = event.getEntity();
        EntityDamageEvent lastDamageCause = player.getLastDamageCause();
        String deathmessage = event.getDeathMessage();

        if (data.isAssassin(UserManager.getPlayer(player))) {
            deathmessage = deathmessage.replaceAll(player.getName(), ChatColor.DARK_RED + "[ASSASSIN]" + ChatColor.RESET);
        }

        if (lastDamageCause instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) lastDamageCause;
            Entity damager = entityDamageByEntityEvent.getDamager();

            if (damager instanceof Projectile) {
                damager = ((Projectile) damager).getShooter();
            }

            if (damager instanceof Player) {
                if (data.isAssassin(UserManager.getPlayer((Player) damager))) {
                    String damagername = ((Player) damager).getName();
                    deathmessage = deathmessage.replaceAll(damagername, ChatColor.DARK_RED + "[ASSASSIN]" + ChatColor.RESET);
                }
            }
        }
        event.setDeathMessage(deathmessage);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (killer != null && player != killer) {
            bounty.handleBounties(player, killer);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();
        List<String> blockedCmds = Config.getInstance().getBlockedCommands();

        if (data.isAssassin(UserManager.getPlayer(player)) && blockedCmds.contains(command)) {
            player.sendMessage(ChatColor.RED + "You're not allowed to use " + ChatColor.GOLD + command + ChatColor.RED + " command while an Assassin.");
            event.setCancelled(true);
        }
    }
}
