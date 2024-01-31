package org.vareniodev.mc.chickensgame;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.vareniodev.mc.chickensgame.SpawnCommand;

public class ChickensGame extends JavaPlugin
{
    private gameManager gm;
    private statsManager sm;

    private ConfigManager configManager;

    File statsFile;

    public void onEnable() {
        getLogger().info("enabled!");

        gm = new gameManager(this);

        Bukkit.getPluginManager().registerEvents(new Handler(this),this);
        getCommand("chspawn").setExecutor(new SpawnCommand(this));
        getCommand("team").setExecutor(new TeamCommand());
        getCommand("chstart").setExecutor(new ChstartCommand(this));
        getCommand("gamesettings").setExecutor(new chickenSettingsCommand(this));
        getCommand("mystats").setExecutor(new statsCommand(this));

        sm = new statsManager();

        // Загрузка статистики из файла при включении плагина
        statsFile = new File(getDataFolder(), "stats.yml");
        statsManager.loadStatsFromFile(statsFile);

        configManager = new ConfigManager(this);
        configManager.loadConfig();
    }

    void checkGameStart() {
        getLogger().info("check");
        if (!gameManager.isGameRunning() && gm.canStartGame()) {
            gm.startGame();
        } else {
            getLogger().info("Сan`t start the game.");
        }
    }

    public File getStats() {
        return statsFile;
    }

    public void onDisable() {
        statsFile = new File(getDataFolder(), "stats.yml");
        statsManager.saveStatsToFile(statsFile);

        configManager.saveConfig();

        getLogger().info("disabled!");
    }
}
