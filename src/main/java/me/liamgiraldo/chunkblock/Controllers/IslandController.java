package me.liamgiraldo.chunkblock.Controllers;

import me.liamgiraldo.chunkblock.Chunkblock;
import me.liamgiraldo.chunkblock.Models.IslandModel;
import me.liamgiraldo.chunkblock.util.EquipmentPair;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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










    private void addPlayer(IslandModel island, Player player){
        player.setMetadata("islandId", new FixedMetadataValue(plugin,island.id().toString()));
        Map<UUID, EquipmentPair> cachedInvs = island.inventories();
        UUID uuid = player.getUniqueId();
        if (cachedInvs.containsKey(uuid)){
            EquipmentPair pair = cachedInvs.get(uuid);
            player.getInventory().setContents(pair.content());
            player.getInventory().setArmorContents(pair.armor());
            cachedInvs.remove(uuid);
        }
    }
    private void removePlayer(IslandModel island, Player player){
        player.removeMetadata("islandId",plugin);
        PlayerInventory inv = player.getInventory();
        EquipmentPair items = new EquipmentPair(inv.getContents(),inv.getArmorContents());
        island.inventories().put(player.getUniqueId(),items);
    }

    /**
     * Reads player meta data
     * @param player the player to check
     * @return the Island that the player is currently on, null if they are on no island
     */
    private IslandModel islandOn(Player player){
        if (!player.hasMetadata("islandId")) return null;
        String id = player.getMetadata("islandId").get(0).asString();
        return islands.getOrDefault(id, null);
    }

    /*
    Island loading / Creation
     */
    public void reloadIslands(){
        if (!plugin.islands.getConfig().contains("islands")) return;
        // Load islands from config
        Set<String> keys = plugin.islands.getConfig().getConfigurationSection("islands").getKeys(false);
        for (String key : keys){
            IslandModel model = loadModel(key);
            if (model != null) islands.put(key, model);
        }

    }



    private IslandModel loadModel(String id){
        String path = "islands." + id;
        if (!plugin.islands.getConfig().contains(path)) return null;
        Location center = plugin.fromString(plugin.islands.getConfig().getString(path + ".center"));
        int size = plugin.islands.getConfig().getInt(path + ".size");
        UUID leader = UUID.fromString(plugin.islands.getConfig().getString(path + ".owner"));
        //getting a little crazy here, but basically we're grabbing UUIDs separated by commas and collecting them in a HashSet
        Set<UUID> members = Arrays.stream(plugin.islands.getConfig().getString(path + ".members")
                .split(","))
                .map(UUID::fromString)
                .collect(Collectors.toCollection(HashSet::new));
        return new IslandModel(UUID.fromString(id), leader, members, size, center);
    }

    private IslandModel createNewIsland(Location center, UUID playerUUID){
        UUID id = UUID.randomUUID();
        //Very unlikely, but just making sure the uuid isn't already in use
        while(plugin.islands.getConfig().contains("islands." + id)) id = UUID.randomUUID();
        IslandModel island = new IslandModel(id, playerUUID, 30, center);
        islands.put(id.toString(), island);

        return island;
    }


    /*
    Island loading / Creation
     */
    public byte[] writeItemsToBytes(ItemStack[] items){
        try{
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOut = new BukkitObjectOutputStream(outputStream);
            int size = items.length;
            //Writing the total length
            dataOut.writeInt(size);
            for (int i = 0; i < size; i++) {
                dataOut.writeInt(i); //Writing the slot
                dataOut.writeObject(items[i]); //Writing the item
            }
            dataOut.close();
            return outputStream.toByteArray();
        }catch (Exception e){
            plugin.getLogger().warning("Something went wrong when saving a player's inventory! (BAD NEWS BEARS)");
            e.printStackTrace(); //No warning!
        }

        return null;
    }

    public ItemStack[] loadItemsFromBytes(byte[] bytes){
        try{
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            BukkitObjectInputStream dataIn = new BukkitObjectInputStream(inputStream);
            int size = dataIn.readInt();
            ItemStack[] items = new ItemStack[size];
            for (int i = 0; i < size; i++){
                int slot = dataIn.readInt();
                Object readObj = dataIn.readObject();
                if (readObj instanceof ItemStack){
                    ItemStack item = (ItemStack) readObj;
                    items[i] = item;
                }
            }
            return items;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void saveInventories(IslandModel island){
        Map<UUID, EquipmentPair> invs = island.inventories();
        Set<UUID> keySet = invs.keySet();
        /*
        (can delete)
        map-id:
            player-id:
                content: rjjfwrfjfl
                armor: adakfakfjairqiq
         */
        for (UUID uuid : keySet){
            //Saves ea
            String path = island.id() + "." + uuid;
            EquipmentPair pair = invs.get(uuid);
            byte[] content = writeItemsToBytes(pair.content());
            byte[] armor = writeItemsToBytes(pair.armor());
            plugin.itemStorage.getConfig().set(path + ".content", plugin.writeByteArray(content));
            plugin.itemStorage.getConfig().set(path + ".armor",plugin.writeByteArray(armor));
        }
    }

    /*
    Inventory Loading / Saving
     */


}
