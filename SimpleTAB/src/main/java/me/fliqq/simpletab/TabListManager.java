package me.fliqq.simpletab;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.md_5.bungee.api.ChatColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TabListManager {
    private final GroupsRetriever GroupsRetriever;
    private final PlayerGroupRetriever playerGroupRetriever;
    private final LuckPerms luckPerms;

    public TabListManager(LuckPerms luckPerms) {
        this.luckPerms=luckPerms;
        this.GroupsRetriever = new GroupsRetriever(luckPerms);
        this.playerGroupRetriever = new PlayerGroupRetriever(luckPerms);
    }

    public void updateTabList() {
        // Map to store player weights
        Map<Player, Integer> playerWeights = new HashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // Retrieve and process player groups
        for (Player player : Bukkit.getOnlinePlayers()) {
            CompletableFuture<Void> future = playerGroupRetriever.getPlayerPrimaryGroup(player.getUniqueId())
                    .thenAccept(primaryGroup -> {
                        if (primaryGroup == null) {
                            return;
                        }

                        // Find group by name
                        Group group = GroupsRetriever.getAllGroups().stream()
                                .filter(g -> g.getName().equals(primaryGroup))
                                .findFirst()
                                .orElse(null);

                        if (group != null) {
                            int weight = group.getWeight().orElse(0);
                            synchronized (playerWeights) {
                                playerWeights.put(player, weight);
                            }
                        }
                    });
            futures.add(future);
        }

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
            // Sort players by their weight (higher weight -> higher in the tab list)
            List<Map.Entry<Player, Integer>> sortedPlayers = new ArrayList<>(playerWeights.entrySet());
            sortedPlayers.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

            // Update tab list ordering and prefixes
            for (Map.Entry<Player, Integer> entry : sortedPlayers) {
                Player player = entry.getKey();

                Component header = MiniMessage.miniMessage().deserialize("<gray><bold><st>================</st><newline>"
                    + "Le Monde Survie<newline>"
                    + "<green><bold>>> Bienvenue " + player.getName() + " <<<newline></bold></green></bold></gray>");

                Component footer = MiniMessage.miniMessage().deserialize("<newline><green>" 
                    + String.format("%.2f", Bukkit.getServer().getTPS()[0]) 
                    + "</green><gray>< ms<newline><st>===============</st></gray>");

        

                UserManager userManager = luckPerms.getUserManager();
                User user = userManager.getUser(player.getUniqueId());
                String fullNameStr = ChatColor.translateAlternateColorCodes('&', user.getCachedData().getMetaData().getPrefix()+ " "+player.getName());

                Component prefixComponent = Component.text(fullNameStr);

                // Update player's tab list and display names
                player.sendPlayerListHeaderAndFooter(header, footer);
                player.displayName(prefixComponent);
                player.playerListName(prefixComponent);

            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }
}
