package me.fliqq.simpletab;

import org.bukkit.Bukkit;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.user.UserDataRecalculateEvent;

public class PlayerMetaListener {
    
    private final SimpleTAB plugin;
    private final LuckPerms luckPerms;
    private final PrefixManager prefixManager;

    public PlayerMetaListener(SimpleTAB plugin, LuckPerms luckPerms, PrefixManager prefixManager){
        this.plugin = plugin;
        this.luckPerms = luckPerms;
        this.prefixManager = prefixManager;
    }   

    public void register(){
        luckPerms.getEventBus().subscribe(this.plugin, UserDataRecalculateEvent.class, this::onUserDataRecalculate);
    }

    public void onUserDataRecalculate(UserDataRecalculateEvent event){
        prefixManager.updatePlayerPrefix(Bukkit.getPlayer(event.getUser().getUniqueId()));
    }

}
