package me.fliqq.simpletab;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import net.luckperms.api.LuckPerms;

public class SimpleTAB extends JavaPlugin
{
    private LuckPerms luckPerms;
    private SimpleTabManager simpleTabManager;
    private PlayerMetaListener playerMetaListener;

    @Override
    public void onEnable() {
        luckPerms = getLuckPermsAPI();
        if (luckPerms == null) {
            getLogger().severe("LuckPerms API not found! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        simpleTabManager = new SimpleTabManager(luckPerms);
        simpleTabManager.initTeams();

        playerMetaListener = new PlayerMetaListener(this, luckPerms, simpleTabManager);
        playerMetaListener.register();


        getServer().getPluginManager().registerEvents(new ConnectionListener(simpleTabManager), this);
        getCommand("tabreload").setExecutor(new TabReloadCommand(simpleTabManager));
        Bukkit.getScheduler().runTaskTimer(this, simpleTabManager::updateTabList, 20L, 400L);

        messages();
    }

    private LuckPerms getLuckPermsAPI() {
        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            return getServer().getServicesManager().load(LuckPerms.class);
        }
        return null;
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
