package me.fliqq.simpletab;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        SimpleTAB.getInstance().updateOnlinePlayers();
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        SimpleTAB.getInstance().updateOnlinePlayers();
    }
}
