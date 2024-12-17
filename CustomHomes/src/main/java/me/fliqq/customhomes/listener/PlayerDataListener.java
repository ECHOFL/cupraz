package me.fliqq.customhomes.listener;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.AllArgsConstructor;
import me.fliqq.customhomes.manager.HomeManager;
@AllArgsConstructor
public class PlayerDataListener implements Listener{
    private final HomeManager homeManager;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        UUID uuid = event.getPlayer().getUniqueId();
        homeManager.loadHomes(uuid);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        UUID uuid = event.getPlayer().getUniqueId();
        homeManager.saveHomes(uuid);
    }
}
