package me.liamgiraldo.chunkblock.Controllers;

import me.liamgiraldo.chunkblock.Models.IslandModel;
import me.liamgiraldo.chunkblock.util.BoundingBox;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public class MapGenerator {

    /**
     * This is the world where maps are going to be generated
     * */
    private final World world;

    private final Material grass;
    private final Material dirt;
    private final IslandController controller;

    public MapGenerator(World world, IslandController controller){
        this.world = world;
        dirt = Material.DIRT;
        grass = Material.GRASS;
        this.controller = controller;
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

    /**
     * Finds an empty island position that is a certain distance away from other islands
     * @param center the location to start at, should be a copy of a Location, not the original since it will be edited
     * @param distance how much distance should be between each island
     * @param maxIslandRad how large will the islands be able to get
     *
     * @return A valid center position for a new island
     */
    public Vector findValidPos(Location center, int distance, int maxIslandRad){
        Location base = center.clone();
        BlockFace[] directions = new BlockFace[]{BlockFace.NORTH,BlockFace.EAST,BlockFace.SOUTH,BlockFace.WEST, BlockFace.NORTH_EAST,BlockFace.NORTH_WEST,BlockFace.SOUTH_EAST,BlockFace.SOUTH_WEST};
        Collection<IslandModel> islands = controller.islands.values();
        BlockFace dir = directions[ThreadLocalRandom.current().nextInt(directions.length)];
        BoundingBox box = new BoundingBox(center,maxIslandRad);
        boolean pass = false;
        while (!pass) {
            /*Used to make sure that the pass boolean is only marked true if the IslandModel loop
            is able to run through all its elements (var box doesn't overlap with any islands)
             */
            pass_label:
            {
                for (IslandModel island : islands) {
                    if (box.overlaps(island.bounds())) {
                        base.add(dir.getModX() * (maxIslandRad + distance), dir.getModY() * (maxIslandRad + distance), dir.getModZ() * (maxIslandRad + distance));
                        //may consider allowing you edit bounding box values to limit this object creation
                        box = new BoundingBox(base, maxIslandRad);
                        BlockFace newDir = directions[ThreadLocalRandom.current().nextInt(directions.length)];
                        //Kind of icky, but functionally, this is only supposed to cover an edge case in randomness
                        while (dir.getOppositeFace() == newDir)
                            dir = directions[ThreadLocalRandom.current().nextInt(directions.length)];
                        break pass_label;
                    }
                }
                pass = true;
            }
        }
        return base.toVector();
    }

    private double distSquared(Vector pos1, Vector pos2){
        return Math.pow(pos2.getX() - pos1.getX(), 2) + Math.pow(pos2.getZ() - pos1.getZ(), 2);
    }
}
