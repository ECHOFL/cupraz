package me.fliqq.customhomes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.fliqq.customhomes.command.DelHomeCommand;
import me.fliqq.customhomes.command.HomeCommand;
import me.fliqq.customhomes.command.SetHomeCommand;
import me.fliqq.customhomes.listener.PlayerDataListener;
import me.fliqq.customhomes.manager.ConfigurationManager;
import me.fliqq.customhomes.manager.HomeManager;
import me.fliqq.customhomes.object.ConfirmDeletionHolder;

public class CustomHomes extends JavaPlugin implements Listener
{
    private ConfigurationManager configManager;
    private HomeManager homeManager;
    private DelHomeCommand delHomeCommand;
    @Override
    public void onEnable(){
        saveDefaultConfig();

        configManager=new ConfigurationManager(this);
        homeManager =new HomeManager(this, configManager);

        getServer().getPluginManager().registerEvents(new PlayerDataListener(homeManager), this);
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("sethome").setExecutor(new SetHomeCommand(homeManager));
        getCommand("home").setExecutor(new HomeCommand(homeManager));
        getCommand("home").setTabCompleter(new HomeCommand(homeManager));  


        this.delHomeCommand = new DelHomeCommand(homeManager);
        getCommand("delhome").setTabCompleter(delHomeCommand);
        getCommand("delhome").setExecutor(delHomeCommand);

        int saveInterval = getConfig().getInt("save-interval", 10);
        getServer().getScheduler().runTaskTimer(this, () -> {
            homeManager.saveAllHomes();
            getLogger().info("Tout les homes actifs ont été sauvegardés");
        }, saveInterval * 60 * 20L, saveInterval * 60 * 20L);



        messages();
    }

 @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!(event.getInventory().getHolder() instanceof ConfirmDeletionHolder)) return;

        Player player = (Player) event.getWhoClicked();
        ConfirmDeletionHolder holder = (ConfirmDeletionHolder) event.getInventory().getHolder();
        String homeName = holder.getHomeName();

        event.setCancelled(true);

        if (event.getCurrentItem() != null) {
            if (event.getCurrentItem().getType() == Material.GREEN_STAINED_GLASS_PANE) {
                delHomeCommand.handleConfirmDelete(player, homeName);
                player.closeInventory();
            } else if (event.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE) {
                delHomeCommand.handleCancelDelete(player);
                player.closeInventory();
            }
        }
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
