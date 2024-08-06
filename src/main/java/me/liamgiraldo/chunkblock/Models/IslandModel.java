package me.liamgiraldo.chunkblock.Models;

import me.liamgiraldo.chunkblock.util.BoundingBox;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Array;
import java.util.*;

public class IslandModel {
    private final UUID id;
    private Set<UUID> members;
    private HashMap<UUID, ItemStack[]> inventory;

    private UUID leader;

    private int radius;
    private int maxRadius = 5000;
    private Location center;
    private BoundingBox bounds;

    public IslandModel(UUID id, UUID leader, Set<UUID> members, int radius, Location center) {
        this.id = id;
        this.leader = leader;
        this.members = members != null ? members : new HashSet<>();
        this.radius = radius;
        this.center = center;
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

    public void setRadius(int radius){
        this.radius = Math.min(radius,maxRadius);
        updateBounds();

    }

    public World getWorld(){ return center.getWorld(); }

    public Set<UUID> getMembers() {
        return members;
    }

    /**
     *
     * @return A bounding box for the current size
     */
    public BoundingBox bounds(){
        return bounds;
    }

    public void updateBounds(){
        bounds = new BoundingBox(center, radius);
    }
}
