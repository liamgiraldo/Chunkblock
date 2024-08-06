package me.liamgiraldo.chunkblock.Commands;

import me.liamgiraldo.chunkblock.Controllers.IslandController;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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

        }else sender.sendMessage(ChatColor.RED + "Maybe only players use this command?");
        return false;
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
