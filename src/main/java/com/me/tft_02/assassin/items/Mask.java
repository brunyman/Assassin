package com.me.tft_02.assassin.items;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.me.tft_02.assassin.config.Config;
import com.me.tft_02.assassin.util.Misc;

public class Mask {

    public ItemStack getMask(int amount, boolean plain) {
        MaterialData maskItem = getMaterialData();
        ItemStack itemStack = maskItem.toItemStack(amount);
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

    public ItemStack getMaskPlain() {
        return getMask(1, true);
    }

    public static Recipe getRecipe() {
        ShapedRecipe AssassinMask = new ShapedRecipe(new Mask().getMask(1, false));
        AssassinMask.shape("XXX", "X X");
        AssassinMask.setIngredient('X', Misc.getMaterialData(Config.getInstance().getMaskRecipeItem()));

        return AssassinMask;
    }

    public static MaterialData getMaterialData() {
        return Misc.getMaterialData(Config.getInstance().getMaskResultItem());
    }

    public static Material getMaterial() {
        return getMaterialData().getItemType();
    }
}
