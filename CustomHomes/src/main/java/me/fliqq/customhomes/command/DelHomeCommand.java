package me.fliqq.customhomes.command;

import me.fliqq.customhomes.manager.HomeManager;
import me.fliqq.customhomes.object.Home;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DelHomeCommand implements CommandExecutor, TabCompleter {
    private final HomeManager homeManager;
    private final Map<UUID, String> pendingConfirmations = new HashMap<>(); // Track players pending confirmation

    public DelHomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true; // Command executed successfully
        }
    
        Player player = (Player) sender;
    
        // Check for correct number of arguments
        if (args.length != 1) {
            player.sendMessage(homeManager.getConfigManager().getMessage("specify_home_to_delete")); // Use the new message here
            return false; // Show usage message
        }
    
        String homeName = args[0];
    
        // Check if the home exists
        if (homeManager.getHome(player.getUniqueId(), homeName) == null) {
            player.sendMessage(homeManager.getConfigManager().getMessage("home_not_found").replace("%home%", homeName));
            return true; // Home not found
        }
    
        // If there's already a pending confirmation for this player, proceed with deletion
        if (pendingConfirmations.containsKey(player.getUniqueId())) {
            if (pendingConfirmations.get(player.getUniqueId()).equals(homeName)) {
                // Confirm deletion
                homeManager.removeHome(player.getUniqueId(), homeName); // Call removeHome method here
                player.sendMessage(homeManager.getConfigManager().getMessage("home_deleted").replace("%home%", homeName));
                pendingConfirmations.remove(player.getUniqueId()); // Clear confirmation
                return true; // Command executed successfully
            } else {
                // Cancel previous confirmation
                pendingConfirmations.remove(player.getUniqueId());
            }
        }
    
        // Ask for confirmation
        player.sendMessage("Êtes-vous sûr de vouloir supprimer le home '" + homeName + "' ? Tapez /delhome " + homeName + " à nouveau pour confirmer.");
        pendingConfirmations.put(player.getUniqueId(), homeName); // Set pending confirmation
    
        return true; // Command executed successfully
    }    

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return null; // Only players can use tab completion
        }

        Player player = (Player) sender;
        List<String> completions = new ArrayList<>();

        // If there are no arguments yet, show all homes for the player
        if (args.length == 1) {
            UUID playerId = player.getUniqueId();
            List<Home> homes = homeManager.getHomesMap().get(playerId);
            if(homes!=null){
                for (Home home : homes) {
                    completions.add(home.getName());
                }
            }
        }

        return completions; // Return the list of completions
    }
}

