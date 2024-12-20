package me.fliqq.simpletab;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.UserManager;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerGroupRetriever {
    private final LuckPerms luckPerms;

    public PlayerGroupRetriever(LuckPerms luckPerms) {
        if (luckPerms == null) {
            throw new IllegalArgumentException("LuckPerms instance cannot be null");
        }
        this.luckPerms = luckPerms;    }

    public CompletableFuture<String> getPlayerPrimaryGroup(UUID playerUUID) {
        if (playerUUID == null) {
            CompletableFuture<String> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new IllegalArgumentException("Player UUID cannot be null"));
            return failedFuture;
        }

        UserManager userManager = luckPerms.getUserManager();

        if (userManager == null) {
            SimpleTAB.getInstance().getLogger().warning("UserManager is null. Ensure LuckPerms is correctly initialized.");;
            CompletableFuture<String> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new IllegalStateException("LuckPerms UserManager is unavailable"));
            return failedFuture;
        }

        return userManager.loadUser(playerUUID).thenApply(user -> {
            if (user == null) {
                SimpleTAB.getInstance().getLogger().warning("Failed to load user with UUID: " + playerUUID);;

                return null;
            }

            String primaryGroup = user.getPrimaryGroup();
            if (primaryGroup == null || primaryGroup.isEmpty()) {
                SimpleTAB.getInstance().getLogger().warning("No primary group found for user with UUID: " + playerUUID);;

                return null;
            }

            return primaryGroup;
        }).exceptionally(throwable -> {            
            throwable.printStackTrace();
            return null;
        });
    }
}
