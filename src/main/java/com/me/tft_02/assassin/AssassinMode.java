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
import org.kitteh.tag.TagAPI;

import com.me.tft_02.assassin.runnables.player.AssassinModeActivateTask;
import com.me.tft_02.assassin.util.PlayerData;

public class AssassinMode {

    Assassin plugin;

    public AssassinMode(Assassin instance) {
        plugin = instance;
    }

    private PlayerData data = new PlayerData(plugin);

    /**
     * Applies all the Assassin traits,
     * such as a different display name, nametag and helmet item.
     * 
     * @param player Player whom will be given the traits.
     */
    public void applyTraits(final Player player) {
        data.addLoginTime(player);

        new AssassinModeActivateTask(player).runTaskLater(Assassin.p, 20); // Start 1 seconds later.

        player.setDisplayName(ChatColor.DARK_RED + "[ASSASSIN]" + ChatColor.RESET);
        int number = data.getAssassinNumber(player);
        player.setPlayerListName(ChatColor.DARK_RED + "ASSASSIN [" + number + "]");
        TagAPI.refreshPlayer(player);
    }

    /**
     * Activate Assassin mode.
     * 
     * @param player Player who's mode will be changed.
     */
    public void activateAssassin(Player player) {
        data.addAssassin(player);
        applyTraits(player);
        Location location = player.getLocation();
        data.addLocation(player, location);
        Location loc = player.getLocation();
        loc.setY(player.getWorld().getMaxHeight() + 30D);
        player.getWorld().strikeLightningEffect(loc);
        if (Config.getInstance().getWarnOnActivate()) {
            double messageDistance = Config.getInstance().getMessageDistance();
            for (Player players : player.getWorld().getPlayers()) {
                if (messageDistance > 0) {
                    if (players != player && players.getLocation().distance(player.getLocation()) < messageDistance) {
                        players.sendMessage(ChatColor.DARK_RED + "SOMEONE JUST PUT ON HIS MASK!");
                    }
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
        String playername = player.getName();
        data.leaveAssassinChat(player);
        data.setHostile(player);
        player.sendMessage(ChatColor.GRAY + "ASSASSIN MODE DEACTIVATED");

        player.setDisplayName(playername);
        player.setPlayerListName(playername);
        TagAPI.refreshPlayer(player);
        removeMask(player);
        player.getWorld().playSound(player.getLocation(), Sound.PISTON_EXTEND, 1.0f, 1.0f);
        player.getWorld().playSound(player.getLocation(), Sound.BREATH, 1.0f, 1.0f);
    }

    /**
     * Deactivate Assassin mode.
     * 
     * @param player Player who's mode will be changed.
     */
    public void deactivateAssassin(Player player) {
        String playername = player.getName();
        data.leaveAssassinChat(player);
        data.setNeutral(player);
        player.sendMessage(ChatColor.GRAY + "ASSASSIN MODE DEACTIVATED");

        player.setDisplayName(playername);
        player.setPlayerListName(playername);
        TagAPI.refreshPlayer(player);
        removeMask(player);
        if (Assassin.p.getConfig().getBoolean("Assassin.teleport_on_deactivate")) {
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
    @SuppressWarnings("deprecation")
    public void applyMask(Player player) {
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
        ItemStack assassinMasks = getMask(amount);

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
    @SuppressWarnings("deprecation")
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
    @SuppressWarnings("deprecation")
    public void removeMask(Player player) {
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
    @SuppressWarnings("deprecation")
    public void spawnMask(Player player, int amount) {
        PlayerInventory inventory = player.getInventory();
        ItemStack assassinMask = getMask(amount);
        int emptySlot = inventory.firstEmpty();
        inventory.setItem(emptySlot, assassinMask);
        player.updateInventory();
    }

    public ItemStack getMask(int amount) {
        MaterialData blackWool = new MaterialData(Material.WOOL, (byte) 15);
        ItemStack is = blackWool.toItemStack(amount);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.DARK_RED + "Assassin Mask");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GRAY + "Allows PVP");
        lore.add("Hold in your hand and right-click");
        lore.add("to activate assassin mode.");
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

    public ItemStack getMaskPlain() {
        MaterialData blackWool = new MaterialData(Material.WOOL, (byte) 15);
        ItemStack is = blackWool.toItemStack();
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.DARK_RED + "Assassin Mask");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GRAY + "Allows PVP");
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }
}
