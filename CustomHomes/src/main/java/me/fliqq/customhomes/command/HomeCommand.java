package me.fliqq.customhomes.command;

import me.fliqq.customhomes.manager.HomeManager;
import me.fliqq.customhomes.object.Home;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class HomeCommand implements CommandExecutor, TabCompleter {
    private final HomeManager homeManager;

    public HomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true; // Command executed successfully
        }

        Player player = (Player) sender;

        // Check for correct number of arguments
        if (args.length != 1) {
            player.sendMessage(homeManager.getConfigManager().getMessage("specify_home_to_teleport"));
            return false; // Show usage message
        }

        String homeName = args[0];

        // Get the location of the specified home
        Location location = homeManager.getHome(player.getUniqueId(), homeName);

        if (location != null) {
            player.teleport(location); // Teleport to the specified home
            player.sendMessage(homeManager.getConfigManager().getMessage("teleported_to_home").replace("%home%", homeName));
        } else {
            player.sendMessage(homeManager.getConfigManager().getMessage("home_not_found").replace("%home%", homeName));
        }

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
            List<Home> homes = homeManager.getHomesMap().get(player.getUniqueId());
            if (homes != null) {
                for (Home home : homes) {
                    completions.add(home.getName());
                }
            }
        }

        return completions;
    }
}

