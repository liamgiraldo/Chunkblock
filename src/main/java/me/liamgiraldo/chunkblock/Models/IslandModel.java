package me.liamgiraldo.chunkblock.Models;

import me.liamgiraldo.chunkblock.util.BoundingBox;
import me.liamgiraldo.chunkblock.util.EquipmentPair;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Array;
import java.util.*;

public class IslandModel {
    private final UUID id;
    private Set<UUID> members;
    //Cached player inventories for when they leave their island
    private Map<UUID, EquipmentPair> inventories;

    private UUID leader;

    private int radius;
    private int maxRadius = 5000;
    private Location center, spawn;
    private BoundingBox bounds;

    public IslandModel(UUID id, UUID leader, Set<UUID> members, int radius, Location center) {
        this.id = id;
        this.leader = leader;
        this.members = members != null ? members : new HashSet<>();
        this.radius = radius;
        this.center = center;
        this.spawn = center.getWorld().getHighestBlockAt(center.getBlockX(), center.getBlockZ()).getLocation().add(0.5,0,0.5);
        inventories = new HashMap<>();

    }


    public IslandModel(UUID id, UUID leader, int radius, Location center) {
        this (id, leader, new HashSet<>(), radius, center);
    }



    public UUID id(){ return id; }
    public UUID getLeader() {
        return leader;
    }

    public void setLeader(Player leader) {
        this.setLeader(leader.getUniqueId());
    }

    public void setLeader(UUID uuid){
        this.leader = uuid;
    }

    /**
     * Sets the radius around the center block that the player has access to
     * Will update the island's BoundingBox
     * @param radius the new radius
     */
    public void setRadius(int radius){
        this.radius = Math.min(radius,maxRadius);
        updateBounds();

    }

    public World getWorld(){ return center.getWorld(); }

    public Set<UUID> getMembers() {
        return members;
    }

    public Map<UUID, EquipmentPair> inventories(){ return inventories; }

    /**
     * @return A bounding box for the current size
     */
    public BoundingBox bounds(){
        return bounds;
    }

    public void updateBounds(){
        bounds = new BoundingBox(center, radius);
    }



}
