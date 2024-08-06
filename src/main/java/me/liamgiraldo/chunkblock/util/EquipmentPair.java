package me.liamgiraldo.chunkblock.util;

import org.bukkit.inventory.ItemStack;

/**
 * Used to store player armor and inventory content while preserving slot order
 */
public class EquipmentPair {

    private final ItemStack[] content, armor;

    public EquipmentPair(ItemStack[] content, ItemStack[] armor){
        this.armor = armor;
        this.content = content;
    }
    public ItemStack[] armor(){ return armor; }
    public ItemStack[] content(){ return content; }
}
