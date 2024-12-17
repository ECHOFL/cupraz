package me.fliqq.customhomes.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.fliqq.customhomes.CustomHomes;
import me.fliqq.customhomes.object.Home;

public class HomeManager {
    private final CustomHomes plugin;
    private final ConfigurationManager configManager;
    private final File homesFile;
    private FileConfiguration config;
    private final Map<UUID, List<Home>>  playersHomes = new 
    HashMap<>();

    public HomeManager(CustomHomes plugin, ConfigurationManager configManager){
        this.plugin=plugin;
        this.configManager=configManager;
        this.homesFile=new File(plugin.getDataFolder(), "homes.yml");
        loadHomeFile();
    }
    private void loadHomeFile(){
        if(!homesFile.exists()){
            try {
                homesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Impossible de créer le fichier homes.yml");
            }
        }
        this.config = YamlConfiguration.loadConfiguration(homesFile);
    }

    public void loadHomes(UUID playerId) {
            this.config = YamlConfiguration.loadConfiguration(homesFile);
            
            if (config.contains(playerId.toString())) {
                List<Home> homes = new ArrayList<>();
                for (String homeName : config.getConfigurationSection(playerId.toString()).getKeys(false)) {
                    String locString = config.getString(playerId.toString() + "." + homeName);
                    Location location = stringToLocation(locString);
                    homes.add(new Home(homeName, location)); 
                }
                playersHomes.put(playerId, homes);
            } else {
                playersHomes.put(playerId, new ArrayList<>());
            }
    }
    
    public void saveHomes(UUID playerId) {
        this.config = YamlConfiguration.loadConfiguration(homesFile);
    
        config.set(playerId.toString(), null); 
        if (playersHomes.containsKey(playerId)) {
            for (Home home : playersHomes.get(playerId)) {
                String locString = locationToString(home.getLocation());
                config.set(playerId.toString() + "." + home.getName(), locString);
            }
        }
    
        try {
            this.config.save(homesFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Impossible d'enregistrer dans le fichier homes.yml");
        }
        playersHomes.remove(playerId);
    }

    public void saveAllHomes() {
        this.config = YamlConfiguration.loadConfiguration(homesFile);

        for (Map.Entry<UUID, List<Home>> entry : playersHomes.entrySet()) {
            String uuid = entry.getKey().toString();
            config.set(uuid, null);
            for (Home home : entry.getValue()) {
                String locString = locationToString(home.getLocation());
                config.set(uuid + "." + home.getName(), locString);
            }
        }

        try {
            config.save(homesFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Impossible d'enregistrer dans le fichier homes.yml");
        }
    }

    public void setHome(UUID playerId, String name, Location location) {
        playersHomes.putIfAbsent(playerId, new ArrayList<>());
    
        int maxHomes = getMaxHomesBasedOnRank(Bukkit.getPlayer(playerId));
    
        if (playersHomes.get(playerId).size() >= maxHomes) {
            Bukkit.getPlayer(playerId).sendMessage(
                configManager.getMessage("max_home_number").replace("%max-home%", String.valueOf(maxHomes))
            );
            return;
        }
    
        for (Home home : playersHomes.get(playerId)) {
            if (home.getName().equalsIgnoreCase(name)) {
                Bukkit.getPlayer(playerId).sendMessage(configManager.getMessage("home_exists").replace("%home%", name));
                return; 
            }
        }
    
        playersHomes.get(playerId).add(new Home(name, location));
        Bukkit.getPlayer(playerId).sendMessage(configManager.getMessage("home_set").replace("%home%", name));
    }

    public Location getHome(UUID playerId, String name) {
        if (playersHomes.containsKey(playerId)) {
            for (Home home : playersHomes.get(playerId)) {
                if (home.getName().equalsIgnoreCase(name)) {
                    return home.getLocation();
                }
            }
        }
        
        Bukkit.getPlayer(playerId).sendMessage(configManager.getMessage("home_not_found").replace("%home%", name));
        return null;
    }
    public Map<UUID, List<Home>> getHomesMap(){
        return playersHomes;
    }
    public void removeHome(UUID playerId, String homeName) {
        List<Home> homes = playersHomes.get(playerId);
    
        if (homes != null) {
            homes.removeIf(home -> home.getName().equalsIgnoreCase(homeName));
        }
    }
    
    private int getMaxHomesBasedOnRank(Player player) {
        if (player.isOp()) {
            return 100;
        }

        Collection<String> possibleGroups = getDefinedRanks();
        String playerGroup = getPlayerGroup(player, possibleGroups);

        if (playerGroup != null) {
            return configManager.getMaxHomes(playerGroup);
        }

        return configManager.getMaxHomes("default");
    }

    public List<String> getDefinedRanks() {
        if (plugin.getConfig().contains("max-homes")) {
            return new ArrayList<>(plugin.getConfig().getConfigurationSection("max-homes").getKeys(false));
        } else {
            plugin.getLogger().warning("max-homes section introuvable dans home.yml");
            return new ArrayList<>(); 
        }
    }
    private String getPlayerGroup(Player player, Collection<String> possibleGroups) {
        for (String group : possibleGroups) {
            if (player.hasPermission("group." + group)) {
                return group;
            }
        }
        return null;
    }
    public boolean isPlayerInGroup(Player player, String group) {
        return player.hasPermission("group." + group);
    }
    
    private Location stringToLocation(String str){
        String[] parts = str.split(",");
        return new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5]));
    }

    private String locationToString(Location loc){
        return loc.getWorld().getName()+","+loc.getX()+","+loc.getY()+","+loc.getZ()+","+loc.getYaw()+","+loc.getPitch();
    }
    public ConfigurationManager getConfigManager(){
        return this.configManager;
    }
}
