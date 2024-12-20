package me.fliqq.simpletab;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.entity.Player;

public class PlayerMetaListener {

    private final SimpleTAB plugin;
    private final LuckPerms luckPerms;
    private final SimpleTabManager simpleTabManager;

    public PlayerMetaListener(SimpleTAB plugin, LuckPerms luckPerms, SimpleTabManager simpleTabManager) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
        this.simpleTabManager = simpleTabManager;
    }

    public void register() {
        EventBus eventBus = this.luckPerms.getEventBus();
        eventBus.subscribe(this.plugin, NodeAddEvent.class, this::onNodeAdd);
        eventBus.subscribe(this.plugin, NodeRemoveEvent.class, this::onNodeRemove);
    }

    private void onNodeAdd(NodeAddEvent event) {
        if (!event.isUser()) {
            return;
        }

        User user = (User) event.getTarget();
        Node node = event.getNode();

        if (!(node instanceof InheritanceNode)) {
            return; // Not a group change
        }


        // Schedule the prefix update on the main server thread
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            Player player = this.plugin.getServer().getPlayer(user.getUniqueId());
            if (player == null) {
                return; // Player is offline
            }

            // Update prefix for the player
            simpleTabManager.updatePlayerPrefix(player);

        });
    }

    private void onNodeRemove(NodeRemoveEvent event) {
        if (!event.isUser()) {
            return;
        }

        User user = (User) event.getTarget();
        Node node = event.getNode();

        if (!(node instanceof InheritanceNode)) {
            return; 
        }

        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            Player player = this.plugin.getServer().getPlayer(user.getUniqueId());
            if (player == null) {
                return; 
            }

            simpleTabManager.updatePlayerPrefix(player);
            simpleTabManager.updatePlayerTeam(player);
        });
    }
}
