package me.liamgiraldo.chunkblock;

import me.liamgiraldo.chunkblock.util.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Chunkblock extends JavaPlugin {
    public ConfigFile islands;

    @Override
    public void onEnable() {
        // Plugin startup logic
        islands = new ConfigFile(this,"islands");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    /**
     * Takes a Location and throws out a string formatted as "world,x,y,z,yaw,pitch
     * @param loc the location to turn into a String
     * @return a String formatted as world,x,y,z,yaw,pitch from loc values
     */
    public String fromLocation(Location loc){
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
    }

    /**
     * Takes a Location and throws out a string formatted as "world,x,y,z converting the x,y,z values into integers
     * @param loc the location to turn into a String
     * @return a String formatted as world,x,y,z from block location values
     */
    public String fromBLoc(Location loc){
        return loc.getWorld() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    /**
     *
     * @param str String formatted as "world,x,y,z" or "world,x,y,z,yaw,pitch"
     * @return a Location parsed from the given string
     */
    public Location fromString(String str){
        String[] unbox = str.split(",");
        String sWorld = unbox[0];
        World world = Bukkit.getWorld(sWorld);
        if (world == null){
            world = new WorldCreator(sWorld).createWorld();
        }
        double x = Double.parseDouble(unbox[1]);
        double y = Double.parseDouble(unbox[2]);
        double z = Double.parseDouble(unbox[3]);
        if (unbox.length >= 6){
            float yaw = Float.parseFloat(unbox[4]);
            float pitch = Float.parseFloat(unbox[5]);
            return new Location(world, x,y,z,yaw,pitch);
        }else return new Location(world,x,y,z);
    }







}
