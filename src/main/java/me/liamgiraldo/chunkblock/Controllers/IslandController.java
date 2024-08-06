package me.liamgiraldo.chunkblock.Controllers;

import me.liamgiraldo.chunkblock.Chunkblock;
import me.liamgiraldo.chunkblock.Models.IslandModel;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.stream.Collectors;

public class IslandController implements Listener {
    private final Chunkblock plugin;
    private Map<String, IslandModel> islands = new HashMap<>();
    private MapGenerator generator;

    public IslandController(Chunkblock plugin){
        // Load islands from config
        this.plugin = plugin;
    }







    public void reloadIslands(){
        if (!plugin.islands.getConfig().contains("islands")) return;
        Set<String> keys = plugin.islands.getConfig().getConfigurationSection("islands").getKeys(false);
        for (String key : keys){
            IslandModel model = loadModel(key);
            if (model != null) islands.put(key, model);
        }
        // Load islands from config
    }

    private IslandModel loadModel(String id){
        String path = "islands." + id;
        if (!plugin.islands.getConfig().contains(path)) return null;
        Location center = plugin.fromString(plugin.islands.getConfig().getString(path + ".center"));
        int size = plugin.islands.getConfig().getInt(path + ".size");
        UUID leader = UUID.fromString(plugin.islands.getConfig().getString(path + ".owner"));
        //getting a little crazy here
        Set<UUID> members = Arrays.stream(plugin.islands.getConfig().getString(path + ".members")
                .split(","))
                .map(UUID::fromString)
                .collect(Collectors.toSet());
        IslandModel model = new IslandModel(UUID.fromString(id), leader, members, size, center);

        return model;
    }

    private IslandModel createNewIsland(Location center, UUID playerUUID){
        UUID id = UUID.randomUUID();
        while(plugin.islands.getConfig().contains("islands." + id)) id = UUID.randomUUID();
        IslandModel island = new IslandModel(id, playerUUID, 30, center);
        islands.put(id.toString(), island);

        return island;
    }
}
