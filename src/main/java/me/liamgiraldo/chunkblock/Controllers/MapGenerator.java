package me.liamgiraldo.chunkblock.Controllers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MapGenerator {

    /**
     * This is the world where maps are going to be generated
     * */
    private World world;

    private Material grass = Material.GRASS;
    private Material dirt = Material.DIRT;

    public MapGenerator(World world){
        this.world = world;
    }


    public void generateSkyblock(int x, int y, int z){
        generate3x3Cube(this.world, x+1, y+1, z+2, grass, dirt, dirt);
        generate3x3Cube(this.world, x-2, y+1, z+2, grass, dirt, dirt);
        generate3x3Cube(this.world, x-2, y+1, z-1, grass, dirt, dirt);
        //up 3 on z
        //-2 on x
        //up 2 on y
        this.world.generateTree(new Location(this.world, x-2, y+2, z+3), TreeType.TREE);
        Block block = this.world.getBlockAt(x-2, y+2, z-2);
        block.setType(Material.CHEST);
        Chest chest = (Chest) block.getState();

        chest.getInventory().addItem(new ItemStack(Material.ICE, 2));
        chest.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET, 1));
    }

    /**
     * Generates a 3x3 cube of blocks
     * */
    private void generate3x3Cube(World world ,int xLoc, int yLoc, int zLoc, Material top, Material middle, Material bottom){
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block block = world.getBlockAt(xLoc, yLoc, zLoc);
                    if (y == 1) {
                        block.setType(top);
                    } else if (y == 0) {
                        block.setType(middle);
                    } else {
                        block.setType(bottom);
                    }
                }
            }
        }
    }
}
