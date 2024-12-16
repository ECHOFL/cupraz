package me.fliqq.customhomes.command;

import me.fliqq.customhomes.manager.HomeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {
    private final HomeManager homeManager;

    public SetHomeCommand(HomeManager homeManager) {
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
            player.sendMessage(homeManager.getConfigManager().getMessage("specify_home_to_set"));
            return false; // Show usage message
        }

        String homeName = args[0];

        // Check if the home name is valid (only alphanumeric characters)
        if (!isValidHomeName(homeName)) {
            player.sendMessage(homeManager.getConfigManager().getMessage("invalid_home_name")); // Use custom message
            return false; // Invalid home name
        }

        // Call the HomeManager to set the home
        homeManager.setHome(player.getUniqueId(), homeName, player.getLocation());

        return true; // Command executed successfully
    }

    private boolean isValidHomeName(String name) {
        // Check if the name matches the regex for valid characters (alphanumeric only)
        return name.matches("^[a-zA-Z0-9]+$");
    }
}
