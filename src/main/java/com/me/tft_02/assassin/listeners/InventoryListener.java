package com.me.tft_02.assassin.listeners;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.me.tft_02.assassin.AssassinMode;
import com.me.tft_02.assassin.util.ItemChecks;

public class InventoryListener implements Listener {
    private AssassinMode assassin = new AssassinMode();
    private ItemChecks itemcheck = new ItemChecks();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();

        if (!(humanEntity instanceof Player)) {
            return;
        }

        Player player = (Player) humanEntity;

        ItemStack currentItemStack = event.getCurrentItem();

        switch (event.getAction()) {
            //DEACTIVATE
            case PICKUP_ALL:
                if (event.getSlot() != 39 || !itemcheck.isMask(currentItemStack)) {
                    return;
                }

                assassin.activateHostileMode(player);
                return;

            default:
                return;
        }
    }
}
