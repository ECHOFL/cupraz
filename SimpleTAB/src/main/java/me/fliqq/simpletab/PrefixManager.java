package me.fliqq.simpletab;

import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class PrefixManager {
    private final PlayerGroupRetriever playerGroupRetriever;
    private final LuckPerms lp;

    public PrefixManager(LuckPerms luckPerms) {
        this.lp=luckPerms;
        this.playerGroupRetriever = new PlayerGroupRetriever(luckPerms);
    }

    public CompletableFuture<Void> updatePlayerPrefix(Player player) {
        return playerGroupRetriever.getPlayerPrimaryGroup(player.getUniqueId())
                .thenAccept(primaryGroup -> {
                    if (primaryGroup == null) {
                        SimpleTAB.getInstance().getLogger().warning("Failed to retrieve primary group for player: " + player.getName());
                        return;
                    }
                    User user = lp.getUserManager().loadUser(player.getUniqueId()).join();
                    String fullNameStr = ChatColor.translateAlternateColorCodes('&', user.getCachedData().getMetaData().getPrefix()+ " "+player.getName());
                    Component prefixComponent = Component.text(fullNameStr);

                    player.displayName(prefixComponent);
                    player.playerListName(prefixComponent);
                }).exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
    }
}

