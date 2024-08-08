package me.liamgiraldo.chunkblock.Commands;

import me.liamgiraldo.chunkblock.Controllers.IslandController;
import me.liamgiraldo.chunkblock.Models.IslandModel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class IslandCommands implements CommandExecutor, TabCompleter {
    private IslandController islandController;

    public IslandCommands(IslandController islandController){
        this.islandController = islandController;
    }


    /**
     * Available commands are:
     *
     * create
     * invite
     * visit
     * accept
     * cancel
     * decline
     * leave
     * disband
     *
     * */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        int size = args.length;
        if (sender instanceof Player){
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            if (size >= 1) {
                String sub = args[0];
                //joining (Think of this like the connection command)
                if (size >= 2) {
                    if (sub.equalsIgnoreCase("join") || sub.equalsIgnoreCase("j")) {
                        //We can take a time "cost" or a memory "cost" for caching names tied to UUIDS, Imma just do the former for now.
                        String name = args[1];
                        UUID target = fromName(name);
                        IslandModel model = findFirstOption(target, uuid);
                        if (model == null) {
                            player.sendMessage(ChatColor.RED + "You not part of an island owned by " + name);
                            player.sendMessage(ChatColor.YELLOW + "To join your own private island, just type /island or /island join");
                            return false;
                        }
                        islandController.addPlayer(model, player);
                        player.sendMessage(ChatColor.GREEN + "Sending you to the desired island");
                        return true;
                    }

                }
            }
            //Default case
            IslandModel privateIsland = findPrivateIsland(uuid);
            if (privateIsland == null){
                player.sendMessage(ChatColor.RED + "You do not have a private island");
                player.sendMessage(ChatColor.YELLOW + "If you wanted to go to a friends island, do /island j [name-of-friend]");
                return false;
            }
            islandController.addPlayer(privateIsland,player);
            player.sendMessage(ChatColor.GREEN + "Sending you to your island");
            return true;
        }else sender.sendMessage(ChatColor.RED + "Maybe only players use this command?");
        return false;
    }

    /**
     * Finds the first IslandModel that is owned by the provided owner UUID and that the provided joiner UUID is a member of
     * @param owner the owner of the island
     * @param joiner the person trying to join
     * @return the first matching IslandModel or null if none exist
     */

    private IslandModel findFirstOption(UUID owner, UUID joiner){
        //In the future we might not want to keep all island objects loaded at the same time
        //so it may be better to read data from the file directly but that wouldn't break too much
        Collection<IslandModel> models = islandController.islands.values();
        for (IslandModel model : models){
            if (model.isCoop() && model.getLeader().equals(owner) && model.getMembers().contains(joiner))
                return model;
        }
        return null;
    }

    private IslandModel findPrivateIsland(UUID owner){
        //See findFirstOption() to see my commentary about this part
        Collection<IslandModel> models =  islandController.islands.values();
        for (IslandModel model : models){
            if (!model.isCoop() && model.getLeader().equals(owner)) return model;
        }
        return null;
    }
    /**
     * Gets a player's UUID from a player name. Currently, uuids are not cached and tied to names, so all offline players are looped through
     * @param name The name of the player you want the UUID of
     * @return the UUID of the player with the name provided, null if one doesn't exist
     */
    private UUID fromName(String name){
        OfflinePlayer[] offlines = Bukkit.getOfflinePlayers();
        for (OfflinePlayer offline : offlines){
            if (offline.getName().equalsIgnoreCase(name)) return offline.getUniqueId();
        }
        return null;
    }




    /**
     * Reads player meta data
     * @param player the player to check
     * @return the Island that the player is player for, null if they are on no island
     */
    private IslandModel islandOn(Player player){
        if (!player.hasMetadata("islandId")) return null;
        String id = player.getMetadata("islandId").get(0).asString();
        return islandController.islands.getOrDefault(id, null);
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        int size = args.length;
        if (size == 1) return subs(args[0]);
        return null;
    }


    private List<String> subs(String match){
        List<String> subs = new ArrayList<>();
        subs.add("create");
        subs.add("join");
        subs.add("visit");
        subs.add("accept");
        subs.add("decline");
        subs.add("leave");
        subs.add("disband");
        return subs;
    }
}
