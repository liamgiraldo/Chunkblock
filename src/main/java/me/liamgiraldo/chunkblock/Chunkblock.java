package me.liamgiraldo.chunkblock;

import me.liamgiraldo.chunkblock.Controllers.IslandController;
import me.liamgiraldo.chunkblock.util.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

public final class Chunkblock extends JavaPlugin {
    public IslandController islandController;
    public ConfigFile islands;
    public ConfigFile itemStorage;
    public ConfigFile settings;
    //Where should players go when they quit the game or quit skyblock?
    public Location reroute;

    @Override
    public void onEnable() {
        // Plugin startup logic
        islands = new ConfigFile(this,"islands");
        itemStorage = new ConfigFile(this, "item-storage");
        settings = new ConfigFile(this,"settings");
        this.reroute = loadReroute();
        islandController = new IslandController(this);
        islandController.reloadIslands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        islandController.disable();
    }


    /**
     * Takes a Location and throws out a string formatted as "world,x,y,z,yaw,pitch
     * @param loc the location to turn into a String
     * @return a String formatted as world,x,y,z,yaw,pitch from loc values
     */
    public String fromLoc(Location loc){
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

    /**
     * Takes a byte array and formats it as "byte,byte,byte,etc..."
     * @param bytes the byte array to turn into a String
     * @return String formatted as "byte,byte,byte,etc..."
     */
    public String writeByteArray(byte[] bytes){
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes){
            builder.append(b);
            builder.append(',');
        }
        builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }

    /**
     * Takes a String formatted as "byte,byte,byte,etc..." and turns it into a byte array
     * @param str String formatted as "byte,byte,byte,etc..."
     * @return byte array whose values are read from str
     */
    public byte[] readBytesFromStr(String str){
        String[] unbox = str.split(",");
        int length = unbox.length;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++){
            bytes[i] = Byte.parseByte(unbox[i]);
        }
        return bytes;
    }

    public Location loadReroute(){
        String content = this.settings.getConfig().getString("reroute");
        if (content == null) return null;
        else return this.fromString(content);
    }

    public String[] getLinkedWorlds(){
        return this.settings.getConfig().getStringList("linked-worlds").toArray(new String[0]);
    }
}
