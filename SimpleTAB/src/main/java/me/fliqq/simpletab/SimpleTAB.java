package me.fliqq.simpletab;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.luckperms.api.LuckPerms;

public class SimpleTAB extends JavaPlugin
{
    private LuckPerms luckPerms;
    private PrefixManager prefixManager;
    private TabListManager tabListManager;
    private PlayerMetaListener playerMetaListener;

    @Override
    public void onEnable() {
        luckPerms = getLuckPermsAPI();
        if (luckPerms == null) {
            getLogger().severe("LuckPerms API not found! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        prefixManager = new PrefixManager(luckPerms);
        tabListManager = new TabListManager(luckPerms);
        playerMetaListener = new PlayerMetaListener(this, luckPerms, prefixManager);
        playerMetaListener.register();


        getServer().getPluginManager().registerEvents(new ConnectionListener(), this);

        Bukkit.getScheduler().runTaskTimer(this, this::updateOnlinePlayers, 20L, 600L);

        messages();
    }

    private LuckPerms getLuckPermsAPI() {
        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            return getServer().getServicesManager().load(LuckPerms.class);
        }
        return null;
    }

    public void updateOnlinePlayers() {
        Consumer<Player> cons = player -> prefixManager.updatePlayerPrefix(player);
        Bukkit.getOnlinePlayers().forEach(cons);
        tabListManager.updateTabList();
    }



        
    private void messages() {
        getLogger().info("");
        getLogger().info("SimpleTAB 1.0 enabled");
        getLogger().info("Plugin by Fliqqq");
        getLogger().info("");
    }

    public static SimpleTAB getInstance(){
        return getPlugin(SimpleTAB.class);
    }
}
