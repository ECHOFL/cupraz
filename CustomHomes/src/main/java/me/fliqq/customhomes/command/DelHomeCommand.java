package me.fliqq.customhomes.command;

import me.fliqq.customhomes.manager.HomeManager;
import me.fliqq.customhomes.object.ConfirmDeletionHolder;
import me.fliqq.customhomes.object.Home;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class DelHomeCommand implements CommandExecutor, TabCompleter {
    private final HomeManager homeManager;
    private final Map<UUID, String> pendingDeletions = new HashMap<>();

    public DelHomeCommand(HomeManager homeManager) {
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
            player.sendMessage(homeManager.getConfigManager().getMessage("specify_home_to_delete"));
            return false;
        }

        String homeName = args[0];

        if (homeManager.getHome(player.getUniqueId(), homeName) == null) {
            player.sendMessage(homeManager.getConfigManager().getMessage("home_not_found").replace("%home%", homeName));
            return true;
        }

        pendingDeletions.put(player.getUniqueId(), homeName);
        openConfirmationGUI(player, homeName);
        return true;
    }

    @SuppressWarnings("deprecation")
    private void openConfirmationGUI(Player player, String homeName) {
        ConfirmDeletionHolder holder = new ConfirmDeletionHolder(homeName);
        Inventory gui = Bukkit.createInventory(holder, 27, "Suppression de: §a" + homeName);

        ItemStack redGlass = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta redMeta = redGlass.getItemMeta();
        redMeta.setDisplayName("§cAnnule la suppression");
        redGlass.setItemMeta(redMeta);

        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, redGlass);
        }

        ItemStack greenGlass = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta greenMeta = greenGlass.getItemMeta();
        greenMeta.setDisplayName("§aConfirme la suppression");
        greenGlass.setItemMeta(greenMeta);

        gui.setItem(13, greenGlass);

        player.openInventory(gui);
    }

    public void handleConfirmDelete(Player player, String homeName) {
        UUID playerId = player.getUniqueId();
        if (!pendingDeletions.containsKey(playerId) || !pendingDeletions.get(playerId).equals(homeName)) {
            player.sendMessage("§cAucune de requête en cours.");
            return;
        }
        pendingDeletions.remove(playerId);
        homeManager.removeHome(playerId, homeName);
        player.sendMessage(homeManager.getConfigManager().getMessage("home_deleted").replace("%home%", homeName));
    }

    public void handleCancelDelete(Player player) {
        UUID playerId = player.getUniqueId();
        if (!pendingDeletions.containsKey(playerId)) {
            player.sendMessage("§cAucune de requête en cours.");
            return;
        }
        pendingDeletions.remove(playerId);
        player.sendMessage(homeManager.getConfigManager().getMessage("cancel_deletation"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        Player player = (Player) sender;
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            UUID playerId = player.getUniqueId();
            List<Home> homes = homeManager.getHomesMap().get(playerId);
            if (homes != null) {
                for (Home home : homes) {
                    completions.add(home.getName());
                }
            }
        }

        return completions;
    }
}
