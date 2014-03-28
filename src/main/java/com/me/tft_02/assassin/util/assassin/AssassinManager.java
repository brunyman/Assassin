package com.me.tft_02.assassin.util.assassin;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.config.Config;
import com.me.tft_02.assassin.datatypes.Status;
import com.me.tft_02.assassin.datatypes.player.AssassinPlayer;
import com.me.tft_02.assassin.datatypes.player.PlayerProfile;
import com.me.tft_02.assassin.items.Mask;
import com.me.tft_02.assassin.runnables.player.AssassinModeActivateTask;
import com.me.tft_02.assassin.runnables.player.UpdateInventoryTask;
import com.me.tft_02.assassin.util.Misc;
import com.me.tft_02.assassin.util.player.PlayerData;
import com.me.tft_02.assassin.util.player.UserManager;

import org.kitteh.tag.TagAPI;

public class AssassinManager {

    private Mask mask = new Mask();
    private PlayerData data = new PlayerData();

    /**
     * Applies all the Assassin traits,
     * such as a different display name, nametag and helmet item.
     *
     * @param player Player whom will be given the traits.
     */
    public void applyTraits(final Player player) {
        UserManager.getPlayer(player).actualizeLoginTime();
        data.getAssassins().add(player.getName());

        new AssassinModeActivateTask(player).runTaskLater(Assassin.p, 1 * Misc.TICK_CONVERSION_FACTOR);

        player.setDisplayName(ChatColor.DARK_RED + "[ASSASSIN]" + ChatColor.RESET);
        player.setPlayerListName(ChatColor.DARK_RED + "ASSASSIN [" + data.getAssassinNumber(player) + "]");
        TagAPI.refreshPlayer(player);
    }

    /**
     * Activate Assassin mode.
     *
     * @param player Player who's mode will be changed.
     */
    public void activateAssassin(Player player) {
        Assassin.p.debug("Activating AssassinMode for " + player.getName());
        AssassinPlayer assassinPlayer = UserManager.getPlayer(player);
        PlayerProfile profile = assassinPlayer.getProfile();
        Location location = player.getLocation();

        profile.setStatus(Status.ASSASSIN);
        profile.setLocation(location);

        applyTraits(player);

        player.getWorld().playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 1.0f, 1.0f);

        sendWarningMessage(player);

        applyMask(player);

        assassinPlayer.updateLastMaskUse();

        if (Config.getInstance().getParticleEffectsEnabled()) {
            player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 1);
        }
    }

    private void sendWarningMessage(Player player) {
        if (!Config.getInstance().getWarnOnActivate() || Config.getInstance().getMessageDistance() <= 0) {
            return;
        }

        for (Player players : player.getWorld().getPlayers()) {
            if (players != player && Misc.isNear(players.getLocation(), player.getLocation(), Config.getInstance().getMessageDistance())) {
                players.sendMessage(ChatColor.DARK_RED + "SOMEONE JUST PUT A MASK ON!");
            }
        }
    }

    /**
     * Activate Hostile mode.
     *
     * @param player Player who's mode will be changed.
     */
    public void activateHostileMode(Player player) {
        data.getAssassins().remove(player.getName());
        AssassinPlayer assassinPlayer = UserManager.getPlayer(player);
        assassinPlayer.getProfile().setStatus(Status.HOSTILE);
        assassinPlayer.disableAssassinChat();

        player.sendMessage(ChatColor.GRAY + "ASSASSIN MODE DEACTIVATED");

        resetName(player);

        removeMask(player);
        player.getWorld().playSound(player.getLocation(), Sound.PISTON_EXTEND, 1.0f, 1.0f);
        player.getWorld().playSound(player.getLocation(), Sound.FIREWORK_TWINKLE, 1.0f, 1.0f);
    }

    /**
     * Reset a players display name and TagAPI nameplate.
     *
     * @param player Player who's name will be reset.
     */
    private void resetName(Player player) {
        String playername = player.getName();

        player.setDisplayName(playername);
        player.setPlayerListName(playername);
        TagAPI.refreshPlayer(player);
    }

    /**
     * Deactivate Assassin mode.
     *
     * @param player Player who's mode will be changed.
     */
    public void deactivateAssassin(Player player) {
        data.getAssassins().remove(player.getName());
        AssassinPlayer assassinPlayer = UserManager.getPlayer(player);

        assassinPlayer.disableAssassinChat();
        assassinPlayer.getProfile().setStatus(Status.NORMAL);
        player.sendMessage(ChatColor.GRAY + "ASSASSIN MODE DEACTIVATED");

        resetName(player);

        removeMask(player);

        if (Config.getInstance().getTeleportOnDeactivate()) {
            Location previousLocation = assassinPlayer.getProfile().getLocation();
            if (previousLocation == null) {
                player.sendMessage(ChatColor.RED + "Location not found!");
            }
            else {
                player.teleport(previousLocation);
            }
        }

        player.getWorld().playSound(player.getLocation(), Sound.PISTON_EXTEND, 1.0f, 1.0f);
        player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
    }

    /**
     * Applies a mask on the players head.
     * Also gives back the helmet the player was wearing, if any.
     *
     * @param player Player who will get a mask.
     */
    protected void applyMask(Player player) {
        applyMask(player, false);
    }

    public void applyMask(Player player, boolean force) {
        PlayerInventory inventory = player.getInventory();

        if (!force) {
            ItemStack itemHead = inventory.getHelmet();
            int amountInHand = inventory.getItemInHand().getAmount();
            int newMaskAmount = 0;

            if (amountInHand > 1) {
                newMaskAmount = amountInHand - 1;
            }

            if (itemHead != null) {
                inventory.setItem(inventory.firstEmpty(), itemHead.clone());
            }

            if (newMaskAmount > 0) {
                inventory.setItemInHand(mask.getMask(newMaskAmount, false));
            }
        }

        inventory.setHelmet(mask.getMaskPlain());
        new UpdateInventoryTask(player).runTask(Assassin.p);
    }

    /**
     * Removes a mask on the players head.
     * Also puts back the helmet on the player, if any.
     *
     * @param player Player who will lose a mask.
     */
    protected void removeMask(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack itemHead = inventory.getHelmet();
        if (itemHead != null) {
            inventory.setHelmet(null);
        }

        // Gives back the mask if config says so
        if (Config.getInstance().getReturnMask()) {
            spawnMask(player, 1);
        }

        // If the player was wearing a helmet, put it back on
        int helmetindex = -1;
        if (inventory.contains(Material.DIAMOND_HELMET)) {
            helmetindex = inventory.first(Material.DIAMOND_HELMET);
        }
        else if (inventory.contains(Material.IRON_HELMET)) {
            helmetindex = inventory.first(Material.IRON_HELMET);
        }
        else if (inventory.contains(Material.GOLD_HELMET)) {
            helmetindex = inventory.first(Material.GOLD_HELMET);
        }
        else if (inventory.contains(Material.LEATHER_HELMET)) {
            helmetindex = inventory.first(Material.LEATHER_HELMET);
        }
        if (helmetindex >= 0) {
            ItemStack helmet = inventory.getItem(helmetindex);
            inventory.setItem(helmetindex, null);
            inventory.setHelmet(helmet);
        }

        new UpdateInventoryTask(player).runTask(Assassin.p);
    }

    /**
     * Spawns a mask in inventory.
     *
     * @param player Player who will receive a mask.
     */
    public void spawnMask(Player player, int amount) {
        PlayerInventory inventory = player.getInventory();
        ItemStack assassinMask = mask.getMask(amount, false);
        int emptySlot = inventory.firstEmpty();
        inventory.setItem(emptySlot, assassinMask);
        new UpdateInventoryTask(player).runTask(Assassin.p);
    }
}
