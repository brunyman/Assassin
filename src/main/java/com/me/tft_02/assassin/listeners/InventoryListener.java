package com.me.tft_02.assassin.listeners;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import com.me.tft_02.assassin.AssassinMode;
import com.me.tft_02.assassin.util.ItemChecks;

public class InventoryListener implements Listener {
    private AssassinMode assassin = new AssassinMode();
    private ItemChecks itemcheck = new ItemChecks();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        ItemStack itemstack = event.getCurrentItem();

        if (event.getSlotType() == InventoryType.SlotType.ARMOR && itemcheck.isMask(itemstack)) {
            assassin.activateHostileMode((Player) player);
        }
    }
}
