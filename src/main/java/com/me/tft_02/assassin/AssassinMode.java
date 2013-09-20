package com.me.tft_02.assassin;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.me.tft_02.assassin.config.Config;
import com.me.tft_02.assassin.datatypes.Status;
import com.me.tft_02.assassin.runnables.player.AssassinModeActivateTask;
import com.me.tft_02.assassin.util.Misc;
import com.me.tft_02.assassin.util.player.PlayerData;
import com.me.tft_02.assassin.util.player.UserManager;
import org.kitteh.tag.TagAPI;

public class AssassinMode {

    private PlayerData data = new PlayerData();

    /**
     * Applies all the Assassin traits,
     * such as a different display name, nametag and helmet item.
     *
     * @param player Player whom will be given the traits.
     */
    public void applyTraits(final Player player) {
        data.addLoginTime(player);

        new AssassinModeActivateTask(player).runTaskLater(Assassin.p, 1 * Misc.TICK_CONVERSION_FACTOR); // Start 1 seconds later.

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
        UserManager.getPlayer(player).getProfile().setStatus(Status.ASSASSIN);

        applyTraits(player);
        Location location = player.getLocation();
        data.addLocation(player, location);
        location.setY(player.getWorld().getMaxHeight() + 30D);
        player.getWorld().strikeLightningEffect(location);

        double messageDistance = Config.getInstance().getMessageDistance();
        if (Config.getInstance().getWarnOnActivate() && messageDistance > 0) {
            for (Player players : player.getWorld().getPlayers()) {
                if (players != player && Misc.isNear(players.getLocation(), location, messageDistance)) {
                    players.sendMessage(ChatColor.DARK_RED + "SOMEONE JUST PUT A MASK ON!");
                }
            }
        }

        applyMask(player);
        data.addCooldownTimer(player);

        if (Config.getInstance().getParticleEffectsEnabled()) {
            player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 1);
        }
    }

    /**
     * Activate Hostile mode.
     *
     * @param player Player who's mode will be changed.
     */
    public void activateHostileMode(Player player) {
        data.leaveAssassinChat(player);
        UserManager.getPlayer(player).getProfile().setStatus(Status.HOSTILE);
        player.sendMessage(ChatColor.GRAY + "ASSASSIN MODE DEACTIVATED");

        resetName(player);

        removeMask(player);
        player.getWorld().playSound(player.getLocation(), Sound.PISTON_EXTEND, 1.0f, 1.0f);
        player.getWorld().playSound(player.getLocation(), Sound.BREATH, 1.0f, 1.0f);
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
        data.leaveAssassinChat(player);
        UserManager.getPlayer(player).getProfile().setStatus(Status.NORMAL);
        player.sendMessage(ChatColor.GRAY + "ASSASSIN MODE DEACTIVATED");

        resetName(player);

        removeMask(player);

        if (Config.getInstance().getTeleportOnDeactivate()) {
            Location previousLocation = data.getLocation(player);
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
        PlayerInventory inventory = player.getInventory();
        ItemStack assassinMask = getMaskPlain();

        ItemStack itemHead = inventory.getHelmet();
        int amountInHand = inventory.getItemInHand().getAmount();
        int amount;
        if (amountInHand > 1) {
            amount = amountInHand - 1;
        }
        else {
            amount = 0;
        }
        ItemStack assassinMasks = getMask(amount, false);

        int emptySlot = inventory.firstEmpty();
        if (itemHead != null) {
            inventory.setItem(emptySlot, itemHead);
            inventory.setItemInHand(assassinMasks);
        }
        else {
            inventory.setItemInHand(assassinMasks);
        }

        inventory.setHelmet(assassinMask);
        player.updateInventory();
    }

    /**
     * Applies a mask on the players head with force.
     *
     * @param player Player who will get a mask.
     */
    public void applyMaskForce(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack assassinMask = getMaskPlain();

        inventory.setHelmet(assassinMask);
        player.updateInventory();
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
        if (itemHead.getTypeId() != 0) {
            inventory.setHelmet(null);
        }
        //Gives back the mask if config says so
        if (Config.getInstance().getReturnMask()) {
            spawnMask(player, 1);
        }

        //If the player was wearing a helmet, put it back on
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
        player.updateInventory();
    }

    /**
     * Spawns a mask in inventory.
     *
     * @param player Player who will receive a mask.
     */
    public void spawnMask(Player player, int amount) {
        PlayerInventory inventory = player.getInventory();
        ItemStack assassinMask = getMask(amount, false);
        int emptySlot = inventory.firstEmpty();
        inventory.setItem(emptySlot, assassinMask);
        player.updateInventory();
    }

    public ItemStack getMask(int amount, boolean plain) {
        MaterialData blackWool = new MaterialData(Material.WOOL, (byte) 15);
        ItemStack itemStack = blackWool.toItemStack(amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.DARK_RED + "Assassin Mask");

        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GRAY + "Allows PVP");
        if (!plain) {
            lore.add("Hold in your hand and right-click");
            lore.add("to activate assassin mode.");
        }

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    protected ItemStack getMaskPlain() {
        return getMask(1, true);
    }
}
