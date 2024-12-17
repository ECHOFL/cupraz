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
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(homeManager.getConfigManager().getMessage("specify_home_to_set"));
            return false; 
        }

        String homeName = args[0];

        if (!isValidHomeName(homeName)) {
            player.sendMessage(homeManager.getConfigManager().getMessage("invalid_home_name")); 
            return false; 
        }

        homeManager.setHome(player.getUniqueId(), homeName, player.getLocation());

        return true; 
    }

    private boolean isValidHomeName(String name) {
        return name.matches("^[a-zA-Z0-9]+$");
    }
}
