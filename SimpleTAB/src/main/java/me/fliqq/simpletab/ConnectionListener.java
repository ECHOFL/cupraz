package me.fliqq.simpletab;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ConnectionListener implements Listener {
    
    private final SimpleTabManager simpleTabManager;
    public ConnectionListener(SimpleTabManager simpleTabManager){
        this.simpleTabManager = simpleTabManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        simpleTabManager.updatePlayerPrefix(event.getPlayer());
        simpleTabManager.updatePlayerTabList(event.getPlayer());
        simpleTabManager.updatePlayerTeam(event.getPlayer());

    }

}
