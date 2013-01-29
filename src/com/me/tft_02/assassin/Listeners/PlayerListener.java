package com.me.tft_02.assassin.Listeners;

import java.util.List;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.AssassinMode;
import com.me.tft_02.assassin.runnables.EndCooldownTimer;
import com.me.tft_02.assassin.util.BlockChecks;
import com.me.tft_02.assassin.util.ItemChecks;
import com.me.tft_02.assassin.util.PlayerData;

public class PlayerListener implements Listener {
    Assassin plugin;

    public PlayerListener(Assassin instance) {
        plugin = instance;
    }

    private AssassinMode assassin = new AssassinMode(plugin);
    private PlayerData data = new PlayerData(plugin);
    private ItemChecks itemcheck = new ItemChecks(plugin);

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.needsUpdate && player.isOp()) {
            player.sendMessage(ChatColor.DARK_RED + "Assassin: " + ChatColor.GOLD + "New version available on BukkitDev!");
        }

        if (!data.isAssassin(player)) {
            data.setNeutral(player);
        } else if (data.isAssassin(player)) {
            event.setJoinMessage(ChatColor.DARK_RED + "AN ASSASSIN JOINED THE GAME");
            assassin.applyTraits(player);
            assassin.applyMaskForce(player);
        }
        if (!data.cooledDown(player)) {
            long cooldowntime = Assassin.getInstance().getConfig().getLong("Assassin.cooldown_length");
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new EndCooldownTimer(player.getName()), cooldowntime);
        }
    }

    @EventHandler
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        if (data.isAssassin(player)) {
            assassin.applyMaskForce(player);
            if (Assassin.getInstance().getConfig().getBoolean("Assassin.potion_effects")) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Assassin.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 1));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 30, 1));
                    }
                }, 5);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (data.isAssassin(player)) {
            event.setQuitMessage(ChatColor.DARK_RED + "AN ASSASSIN LEFT THE GAME");
            data.addLogoutTime(player);
            data.saveActiveTime(player);
            data.leaveAssassinChat(player);
        }
    }

    @EventHandler
    void onInventoryClick(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        ItemStack itemstack = event.getCurrentItem();
        SlotType slotType = event.getSlotType();

        switch (slotType) {
        case ARMOR:
            if (data.isAssassin((Player) player)) {
                if (itemcheck.isMask(itemstack)) {
                    event.setCancelled(true);
                }
            } else {
                if (itemcheck.isMask(itemstack)) {
                    PlayerInventory inventory = player.getInventory();
                    inventory.setHelmet(new ItemStack(Material.AIR));
                }
            }
        default:
            break;
        }
    }

    /**
     * Monitor PlayerInteract events.
     * 
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block block = event.getClickedBlock();
        ItemStack inHand = player.getItemInHand();
        @SuppressWarnings("unused") Material material;

        /* Fix for NPE on interacting with air */
        if (block == null) {
            material = Material.AIR;
        } else {
            material = block.getType();
        }

        switch (action) {
        case RIGHT_CLICK_BLOCK:
        case RIGHT_CLICK_AIR:
            int inHandID = inHand.getTypeId();
            if ((inHandID == 35) && BlockChecks.abilityBlockCheck(block)) {
                ItemStack itemHand = player.getInventory().getItemInHand();
                if (itemcheck.isMask(itemHand)) {
                    if (!player.hasPermission("assassin.assassin")) {
                        player.sendMessage(ChatColor.RED + "You haven't got permission.");
                    } else {
                        if (!data.cooledDown(player)) {
                            player.sendMessage(ChatColor.RED + "You need to wait before you can use that again...");
                        } else {
                            if (data.isAssassin(player)) {
                                player.sendMessage(ChatColor.RED + "You already are an Assassin.");
                            } else {
                                double activation_cost = Assassin.getInstance().getConfig().getDouble("Assassin.activation_cost");
                                if (plugin.vaultEnabled && activation_cost > 0) {
                                    EconomyResponse r = Assassin.econ.withdrawPlayer(player.getName(), activation_cost);
                                    if (r.transactionSuccess()) {
                                        if (plugin.debug_mode) System.out.println("Activating assassin for " + player.getName());
                                        assassin.activateAssassin(player);
                                        long cooldowntime = Assassin.getInstance().getConfig().getLong("Assassin.cooldown_length");
                                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new EndCooldownTimer(player.getName()), cooldowntime);
                                        player.sendMessage(String.format(ChatColor.RED + "You were charged %s %s", Assassin.econ.format(r.amount), Assassin.econ.currencyNamePlural()));
                                    } else {
                                        player.sendMessage(String.format("An error occured: %s", r.errorMessage));
                                    }
                                } else {
                                    if (plugin.debug_mode) System.out.println("Activating assassin for " + player.getName());
                                    assassin.activateAssassin(player);
                                    long cooldowntime = Assassin.getInstance().getConfig().getLong("Assassin.cooldown_length");
                                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new EndCooldownTimer(player.getName()), cooldowntime);
                                }
                            }
                        }
                    }
                    event.setCancelled(true);
                }
            }
            break;
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

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (data.isAssassin(player)) {
            for (ItemStack items : event.getDrops()) {
                if (itemcheck.isMask(items)) {
                    event.getDrops().remove(items);
                    return;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();
        List<String> blockedCmds = Assassin.getInstance().getConfig().getStringList("Assassin.blocked_commands");
        if (data.isAssassin(player) && blockedCmds.contains(command)) {
            event.getPlayer().sendMessage(ChatColor.RED + "You're not allowed to use " + ChatColor.GOLD + command + ChatColor.RED + " command while an Assassin.");
            event.setCancelled(true);
        }
    }
}