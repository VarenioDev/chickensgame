package org.vareniodev.mc.chickensgame;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ConfigManager {

    private final JavaPlugin plugin;
    private File configFile;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void loadConfig() {
        if (!configFile.exists()) {
            // Создание конфига, если его нет
            plugin.saveResource("config.yml", false);
            plugin.getLogger().warning("config.yml just created. Please, configure your game!");
        }

        // Загрузка конфига из файла
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        try {
            // Сохранение конфига в файл
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Не удалось сохранить конфиг в файл.");
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    // Добавьте другие методы для получения и установки значений в конфиге, если необходимо
    public Location getLocation(String loc){
        if (config.contains(loc + ".world")) {
            World world = Bukkit.getWorld(Objects.requireNonNull(config.getString(loc + ".world")));
            double x = config.getDouble( loc + ".x");
            double y = config.getDouble(loc + ".y");
            double z = config.getDouble( loc + ".z");

            return new Location(world, x, y, z);
        }
        return null;
    }

    public void getRedSpawnLocation(String path){}

    public void getBlueSpawnLocation(String path){}

    public void getChickenSpawnLocation(String path){}

    public int getDefaultDuration(){return plugin.getConfig().getInt("duration");}
    public int getDefaultDelay(){return plugin.getConfig().getInt("delay");}
    public int getDefaultCount(){return plugin.getConfig().getInt("count");}
    public void setDefaultDuration(int val) {
        config.set("duration", val);
        saveConfig();
    }

    public void setDefaultDelay(int val) {
        config.set("delay", val);
        saveConfig();
    }

    public void setDefaultCount(int val) {
        config.set("count", val);
        saveConfig();
    }
}
