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
        if(!config.contains("duration")){setDefaultDuration(10);}
        if (!config.contains("delay")){setDefaultDelay(10);}
        if (!config.contains("count")){setDefaultCount(2);}
        if (!config.contains("lobby")){setLocation("lobby", new Location(Bukkit.getWorld("world"),1,1,1));}
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
    public Location getLobbyLocation(){
        if (config.contains("lobby.world")) {
            World world = Bukkit.getWorld(Objects.requireNonNull(config.getString("lobby.world")));
            double x = config.getDouble( "lobby.x");
            double y = config.getDouble("lobby.y");
            double z = config.getDouble( "lobby.z");

            return new Location(world, x, y, z);
        }
        return null;
    }

    public void getRedSpawnLocation(String path){}

    public void getBlueSpawnLocation(String path){}

    public void getChickenSpawnLocation(String path){}

    public int getDefaultDuration(){return config.getInt("duration");}
    public int getDefaultDelay(){return config.getInt("delay");}
    public int getDefaultCount(){return config.getInt("count");}

    public void setLocation(String path, Location location) {
        config.set(path + ".world", location.getWorld().getName());
        config.set(path + ".x", location.getX());
        config.set(path + ".y", location.getY());
        config.set(path + ".z", location.getZ());
        saveConfig();
    }

    public void setDefaultDuration(int val){config.set("duration", val); saveConfig();}
    public void setDefaultDelay(int val){config.set("delay", val);saveConfig();}
    public void setDefaultCount(int val){config.set("count", val);saveConfig();}
}
