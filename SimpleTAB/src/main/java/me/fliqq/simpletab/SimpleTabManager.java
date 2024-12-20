package me.fliqq.simpletab;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SimpleTabManager {
    private final PlayerGroupRetriever playerGroupRetriever;
    private final LuckPerms luckPerms;
    private final Scoreboard scoreboard;

    public SimpleTabManager(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
        this.scoreboard=Bukkit.getScoreboardManager().getMainScoreboard();
        this.playerGroupRetriever = new PlayerGroupRetriever(luckPerms);
    }

    /**
     * Updates the tab list headers and footers for all players.
     */
    public void updateTabList() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            futures.add(updatePlayerTabList(player));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    /**
     * Updates a single player's tab list header and footer.
     *
     * @param player The player to update.
     * @return A CompletableFuture representing the operation.
     */
    public CompletableFuture<Void> updatePlayerTabList(Player player) {
        return CompletableFuture.runAsync(() -> {
            Component header = MiniMessage.miniMessage().deserialize("<gray><bold><st>================</st><newline>"
                    + "<newline>Le Monde Survie<newline>"
                    + "<newline><green><bold>>> Bienvenue " + player.getName() + " <<<newline></bold></green></bold></gray>");

            Component footer = MiniMessage.miniMessage().deserialize("<newline><gray><newline><green>"
                    + String.format("%.2f", Math.min(Bukkit.getServer().getTPS()[0], 20.00))
                    + "</green><gray>ms<newline> <newline><st><bold>================</bold></st></gray>");

            player.sendPlayerListHeaderAndFooter(header, footer);
        });
    }

    /**
     * Updates a single player's prefix (display name and tab list name).
     *
     * @param player The player whose prefix to update.
     * @return A CompletableFuture representing the operation.
     */
    public CompletableFuture<Void> updatePlayerPrefix(Player player) {
        return playerGroupRetriever.getPlayerPrimaryGroup(player.getUniqueId())
                .thenAccept(primaryGroup -> {
                    if (primaryGroup == null) {
                        SimpleTAB.getInstance().getLogger().warning("Failed to retrieve primary group for player: " + player.getName());
                        return;
                    }

                    User user = luckPerms.getUserManager().loadUser(player.getUniqueId()).join();
                    String prefix = user.getCachedData().getMetaData().getPrefix();
                    String fullNameStr = ChatColor.translateAlternateColorCodes('&', prefix + " " + player.getName());

                    Component prefixComponent = Component.text(fullNameStr);

                    player.displayName(prefixComponent);
                    player.playerListName(prefixComponent);
                }).exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
    }

    /**
     * Updates prefixes for all online players.
     */
    public void updateAllPlayerPrefixes() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            futures.add(updatePlayerPrefix(player));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }
    public void initTeams() {
        char teamPrefix = 'a';
        for (Group group : luckPerms.getGroupManager().getLoadedGroups()) {
            String teamName = teamPrefix + "_" + group.getName();
            Team team = scoreboard.getTeam(teamName);
            if (team == null) {
                team = scoreboard.registerNewTeam(teamName);
            }
            teamPrefix++;
        }
    }
    public void updatePlayerTeam(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            String groupName = user.getPrimaryGroup();
            String teamName = getTeamNameForGroup(groupName);
            Team team = scoreboard.getTeam(teamName);
            if (team != null) {
                team.addEntry(player.getName());
            }
        }
    }

    private String getTeamNameForGroup(String groupName) {
        char teamPrefix = 'a';
        for (Group group : luckPerms.getGroupManager().getLoadedGroups()) {
            if (group.getName().equals(groupName)) {
                return teamPrefix + "_" + groupName;
            }
            teamPrefix++;
        }
        return "z_default";
    }
    
}
