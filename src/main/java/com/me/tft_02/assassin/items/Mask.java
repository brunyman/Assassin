package com.me.tft_02.assassin.items;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import com.me.tft_02.assassin.config.Config;

public class Mask {

    public static ItemStack getMask(int amount, boolean plain) {
        String resultItem = Config.getInstance().getMaskResultItem();
        MaterialData maskItem = getMaterialData(resultItem);

        ItemStack itemStack = maskItem.toItemStack(amount);

        String[] itemInfo = resultItem.split("[|]");
        if (maskItem.getItemType() == Material.SKULL_ITEM) {
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            String owner = (itemInfo.length == 3) ? itemInfo[2] : "Notch";
            skullMeta.setOwner(owner);
            itemStack.setItemMeta(skullMeta);
        }

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

    public static ItemStack getMaskPlain() {
        return getMask(1, true);
    }

    public static Recipe getRecipe() {
        ShapedRecipe AssassinMask = new ShapedRecipe(getMask(1, false));
        AssassinMask.shape("XXX", "X X");
        AssassinMask.setIngredient('X', getMaterialData(Config.getInstance().getMaskRecipeItem()));

        return AssassinMask;
    }

    public static Material getMaterial() {
        return getMaskPlain().getType();
    }

    public static MaterialData getMaterialData(String string) {
        String[] itemInfo = string.split("[|]");

        Material itemMaterial = Material.matchMaterial(itemInfo[0]);
        byte blockData = (itemInfo.length == 2 || itemInfo.length == 3) ? Byte.valueOf(itemInfo[1]) : 0;

        MaterialData itemMaterialData = new MaterialData(itemMaterial, blockData);

        return itemMaterialData;
    }
}
