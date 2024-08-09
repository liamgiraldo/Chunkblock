package me.liamgiraldo.chunkblock.Commands;

import me.liamgiraldo.chunkblock.Chunkblock;
import me.liamgiraldo.chunkblock.Controllers.IslandController;
import me.liamgiraldo.chunkblock.Models.IslandModel;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class IslandCommands implements CommandExecutor, TabCompleter {
    private final Chunkblock plugin;
    private IslandController islandController;


    public IslandCommands(IslandController islandController){
        this.plugin = JavaPlugin.getPlugin(Chunkblock.class);
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
     * kick
     *
     * */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        int size = args.length;
        if (sender instanceof Player){
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            if (size >= 1) {
                String sub = args[0].toLowerCase();
                //Quiting (Like leaving the server... but not)
                if (sub.equals("quit") || sub.equals("q")) {
                    //the island the player is playing on
                    IslandModel island = islandOn(player);
                    if (island == null){
                        player.sendMessage(ChatColor.RED + "You are not playing on an island right now");
                        return false;
                    }
                    Location sendTo;
                    if (plugin.reroute == null)
                        sendTo = Bukkit.getWorlds().getFirst().getSpawnLocation();
                    else sendTo = plugin.reroute;
                    islandController.removePlayer(island,player);
                    player.sendMessage(ChatColor.GREEN + "Leaving the game...");
                    player.teleport(sendTo);
                    return true;
                }

                //joining (Think of this like the connection command)
                if (size >= 2) {
                    String arg = args[1];
                    if (sub.equals("join") || sub.equals("j")) {
                        //We can take a time "cost" or a memory "cost" for caching names tied to UUIDS, Imma just do the former for now.
                        //Here arg is a player name
                        UUID target = fromName(arg);
                        IslandModel model = findFirstOption(target, uuid);
                        if (model == null) {
                            player.sendMessage(ChatColor.RED + "You not part of an island owned by " + arg);
                            player.sendMessage(ChatColor.YELLOW + "To join your own private island, just type /island or /island join");
                            return false;
                        }
                        IslandModel current = islandOn(player);
                        if (current != null) islandController.removePlayer(current,player);
                        islandController.addPlayer(model, player);
                        player.sendMessage(ChatColor.GREEN + "Sending you to the desired island");
                        return true;
                    }

                    if (sub.equals("invite") || sub.equals("i")){
                        //arg is a player name
                        UUID target = fromName(arg);
                        if (target == null){
                            player.sendMessage(ChatColor.RED + "Could not find player with the name " + arg);
                            return false;
                        }
                        IslandModel current = islandOn(player);
                        if (!canInvite(player.getUniqueId(),current)){
                            player.sendMessage(ChatColor.RED + "You cannot invite players to this island because you already have a co-op island! There can only be one!");
                            return false;
                        }
                        player.sendMessage(ChatColor.GREEN + "Invited " + arg + " to your island");
                        islandController.invites.put(target,current);
                        OfflinePlayer offline = Bukkit.getOfflinePlayer(target);
                        if (offline.isOnline()) {
                            Player p = offline.getPlayer();
                            p.sendMessage(ChatColor.GREEN + "You've been invited to " + player.getName() + "'s " + "island");
                        }
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

    /**
     * Checks if the provided UUID already owns a co-op island or not
     * @param owner the owner of the provided island
     * @param island the island that the owner wants to invite someone to
     * @return true if the owner has no islands with members besides the one provided (or if no islands have a member) and false otherwise
     */
    private boolean canInvite(UUID owner, IslandModel island){
        //See findFirstOption() to see my commentary about this part
        Collection<IslandModel> models = islandController.islands.values();
        models.remove(island);
        //check if owner already has a coop island
        for (IslandModel model : models){
            if (model.isCoop() && model.getLeader().equals(owner) && !island.equals(model)) return false;
        }
        //check if owner has already invited someone to a different island
        Collection<IslandModel> invited = islandController.invites.values();
        for (IslandModel model : invited){
            if (owner.equals(model.getLeader()) && !model.equals(island)) return false;
        }
        return true;
    }

    /**
     * Finds a players personal island
     * @param owner the UUID of the desired player owner
     * @return an IslandModel whose leader has a UUID equal to the provided UUID
     */
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
