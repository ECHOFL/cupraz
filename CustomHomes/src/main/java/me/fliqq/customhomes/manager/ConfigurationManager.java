package me.fliqq.customhomes.manager;

import me.fliqq.customhomes.CustomHomes;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigurationManager {
    private final CustomHomes plugin;
    private FileConfiguration config;

    public ConfigurationManager(CustomHomes plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    @SuppressWarnings("deprecation")
    public String getMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&', config.getString("messages." + key, "Message introuvable!"));
    }

    public int getMaxHomes(String rank) {
        return config.getInt("max-homes." + rank, 1); 
    }
}

