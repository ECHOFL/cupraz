package me.fliqq.customhomes;

import org.bukkit.plugin.java.JavaPlugin;

import me.fliqq.customhomes.command.DelHomeCommand;
import me.fliqq.customhomes.command.HomeCommand;
import me.fliqq.customhomes.command.SetHomeCommand;
import me.fliqq.customhomes.listener.PlayerDataListener;
import me.fliqq.customhomes.manager.ConfigurationManager;
import me.fliqq.customhomes.manager.HomeManager;

public class CustomHomes extends JavaPlugin
{
    private ConfigurationManager configManager;
    private HomeManager homeManager;
    @Override
    public void onEnable(){
        saveDefaultConfig();

        configManager=new ConfigurationManager(this);
        homeManager =new HomeManager(this, configManager);

        getServer().getPluginManager().registerEvents(new PlayerDataListener(homeManager), this);
        getCommand("sethome").setExecutor(new SetHomeCommand(homeManager));
        getCommand("home").setExecutor(new HomeCommand(homeManager));
        getCommand("home").setTabCompleter(new HomeCommand(homeManager));  
        getCommand("delhome").setExecutor(new DelHomeCommand(homeManager));
        getCommand("delhome").setTabCompleter(new DelHomeCommand(homeManager));

        int saveInterval = getConfig().getInt("save-interval", 10);
        getServer().getScheduler().runTaskTimer(this, () -> {
            homeManager.saveAllHomes();
            getLogger().info("Tout les homes actifs ont été sauvegardés");
        }, saveInterval * 60 * 20L, saveInterval * 60 * 20L);


        messages();
    }
    @Override   
    public void onDisable(){
        homeManager.saveAllHomes();
    }
    private void messages() {
        getLogger().info("***********");
        getLogger().info("CustomHomes 1.0 enabled");
        getLogger().info("Plugin by Fliqqq");
        getLogger().info("***********");
    }
    public CustomHomes getInstance(){
        return getPlugin(CustomHomes.class);
    }
}
