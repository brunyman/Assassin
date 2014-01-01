package com.me.tft_02.assassin.listeners;

import java.util.ArrayList;
import java.util.List;

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
import com.me.tft_02.assassin.locale.LocaleLoader;
import com.me.tft_02.assassin.runnables.EndCooldownTimer;
import com.me.tft_02.assassin.runnables.player.ApplyPotionsTask;
import com.me.tft_02.assassin.runnables.player.UpdateInventoryTask;
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
            player.sendMessage(LocaleLoader.getString("UpdateChecker.Outdated"));
            player.sendMessage(LocaleLoader.getString("UpdateChecker.New_Available"));
        }

        if (assassinPlayer.isAssassin()) {
            event.setJoinMessage(LocaleLoader.getString("Assassin.Join"));
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
        Player player = event.getPlayer();
        AssassinPlayer assassinPlayer = UserManager.getPlayer(player);

        if (assassinPlayer.isAssassin()) {
            assassin.applyMaskForce(player);

            if (!Config.getInstance().getPotionEffectsEnabled()) {
                return;
            }

            List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
            potionEffects.add(new PotionEffect(PotionEffectType.BLINDNESS, Misc.TICK_CONVERSION_FACTOR * 5, 1));
            potionEffects.add(new PotionEffect(PotionEffectType.SLOW, Misc.TICK_CONVERSION_FACTOR * 30, 1));

            new ApplyPotionsTask(player, potionEffects).runTaskLater(Assassin.p, 5);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (Misc.isNPCEntity(player)) {
            return;
        }

        AssassinPlayer assassinPlayer = UserManager.getPlayer(player);

        if (assassinPlayer.isAssassin()) {
            event.setQuitMessage(LocaleLoader.getString("Assassin.Leave"));
            assassinPlayer.actualizeLogoutTime();
            assassinPlayer.actualizeActiveTime();
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
                if (!BlockChecks.abilityBlockCheck(block)) {
                    return;
                }

                if (!itemcheck.isMask(inHand)) {
                    return;
                }

                event.setCancelled(true);
                new UpdateInventoryTask(player).runTask(Assassin.p);

                if (!Permissions.maskUse(player)) {
                    player.sendMessage(ChatColor.RED + "You haven't got permission.");
                    return;
                }

                if (!data.cooledDown(player)) {
                    player.sendMessage(ChatColor.RED + "You need to wait before you can use that again...");
                    return;
                }

                if (UserManager.getPlayer(player).isAssassin()) {
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDeathLowest(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!UserManager.getPlayer(player).isAssassin() || !Config.getInstance().getPreventMaskDropOnDeath()) {
            return;
        }

        List<ItemStack> drops = event.getDrops();
        List<ItemStack> toRemove = new ArrayList<ItemStack>();
        for (ItemStack drop : drops) {
            if (itemcheck.isMask(drop)) {
                toRemove.add(drop);
            }
        }

        for (ItemStack remove : toRemove) {
            drops.remove(remove);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeathHighest(PlayerDeathEvent event) {
        Player player = event.getEntity();
        EntityDamageEvent lastDamageCause = player.getLastDamageCause();
        String deathmessage = event.getDeathMessage();

        if (UserManager.getPlayer(player).isAssassin()) {
            deathmessage = deathmessage.replaceAll(player.getName(), LocaleLoader.getString("Assassin.Name.Tag"));
        }

        if (lastDamageCause instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) lastDamageCause;
            Entity damager = entityDamageByEntityEvent.getDamager();

            if (damager instanceof Projectile) {
                damager = ((Projectile) damager).getShooter();
            }

            if (damager instanceof Player) {
                if (UserManager.getPlayer((Player) damager).isAssassin()) {
                    String damagername = ((Player) damager).getName();
                    deathmessage = deathmessage.replaceAll(damagername, LocaleLoader.getString("Assassin.Name.Tag"));
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

        if (UserManager.getPlayer(player).isAssassin() && blockedCmds.contains(command)) {
            player.sendMessage(LocaleLoader.getString("Commands.CantUseAsAssassin", command));
            event.setCancelled(true);
        }
    }
}
