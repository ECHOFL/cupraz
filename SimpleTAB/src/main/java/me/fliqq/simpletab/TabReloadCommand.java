package me.fliqq.simpletab;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TabReloadCommand implements CommandExecutor {
    private final SimpleTabManager simpleTabManager;

    public TabReloadCommand(SimpleTabManager simpleTabManager) {
        this.simpleTabManager = simpleTabManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("simpletab.reload")) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }
        if (args.length != 0) {
            return false;
        }
        simpleTabManager.updateTabList();
        simpleTabManager.updateAllPlayerPrefixes();
        return true;
    }
    
}
