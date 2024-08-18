package me.liamgiraldo.chunkblock.Models;

import me.liamgiraldo.chunkblock.util.BoundingBox;
import me.liamgiraldo.chunkblock.util.EquipmentPair;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class IslandModel {
    private final UUID id;
    private Set<UUID> members;
    //Cached player inventories for when they leave their island
    private Map<UUID, EquipmentPair> inventories;

    private UUID leader;

    private int radius;
    private int maxRadius;
    private Location center, spawn;
    private BoundingBox bounds;

    public IslandModel(UUID id, UUID leader, Set<UUID> members, int radius, Location center, Location spawn) {
        this.id = id;
        this.leader = leader;
        this.members = members != null ? members : new HashSet<>();
        this.radius = radius;
        this.center = center;
        this.spawn = spawn;
        maxRadius = 5000;
        inventories = new HashMap<>();

    }

    public IslandModel(UUID id, UUID leader, Set<UUID> members, int radius, Location center) {
        this(id,leader,members,radius,center,center.getWorld().getHighestBlockAt(center.getBlockX(), center.getBlockZ()).getLocation().add(0.5,0,0.5));
    }


    public IslandModel(UUID id, UUID leader, int radius, Location center) {
        this (id, leader, new HashSet<>(), radius, center);

        //I figured the leader should be a member of their own island
        members.add(leader);
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

    /**
     * Sets the spawnpoint for the island
     * @param spawn the new spawnpoint
     */
    public void setSpawn(Location spawn){
        this.spawn = spawn;
    }

    public UUID getId(){ return id; }
    public UUID getLeader() {
        return leader;
    }

    public int getRadius(){ return radius; }

    public World getWorld(){ return center.getWorld(); }

    public Location getCenter(){ return center; }

    public Location getSpawn(){ return spawn; }

    public Set<UUID> getMembers() {
        return members;
    }

    public boolean isMember(UUID uuid){
        return members.contains(uuid);
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

    /**
     * @return true if the model is a Co-op island, false if otherwise
     *  An island is co-op if it has 1+ members (excluding the owner)
     */
    public boolean isCoop(){ return !members.isEmpty(); }


}
