package com.me.tft_02.assassin.util;

import org.bukkit.block.Block;

public class BlockChecks {

    /**
     * Check if a block should allow for the activation of masks.
     * 
     * @param block Block to check
     * @return true if the block should allow mask activation, false otherwise
     */
    public static boolean abilityBlockCheck(Block block) {
        if (block == null) {
            return true;
        }
        switch (block.getType()) {
            case BED_BLOCK:
            case BREWING_STAND:
            case BOOKSHELF:
            case BURNING_FURNACE:
            case CAKE_BLOCK:
            case CHEST:
            case SIGN:
            case DISPENSER:
            case ENCHANTMENT_TABLE:
            case ENDER_CHEST:
            case FENCE_GATE:
            case FURNACE:
            case IRON_DOOR_BLOCK:
            case JUKEBOX:
            case LEVER:
            case NOTE_BLOCK:
            case STONE_BUTTON:
            case WOOD_BUTTON:
            case TRAP_DOOR:
            case WALL_SIGN:
            case WOODEN_DOOR:
            case WORKBENCH:
            case BEACON:
            case ANVIL:
                return false;
            default:
                return true;
        }
    }
}
