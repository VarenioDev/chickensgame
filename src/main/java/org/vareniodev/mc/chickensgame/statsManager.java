package org.vareniodev.mc.chickensgame;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class statsManager {
    private static Map<UUID, playerStats> statsMap = new HashMap<>();

    public playerStats getStats(UUID playerId) {
        return statsMap.getOrDefault(playerId, new playerStats());
    }

    public void updateStats(UUID playerId, boolean isWinner) {
        playerStats stats = statsMap.getOrDefault(playerId, new playerStats());

        stats.setTotalGames(stats.getTotalGames() + 1);

        if (isWinner) {
            stats.setWins(stats.getWins() + 1);
        } else {
            stats.setLosses(stats.getLosses() + 1);
        }

        statsMap.put(playerId, stats);
    }

    public static void saveStatsToFile(File file) {
        FileConfiguration config = new YamlConfiguration();

        for (UUID playerId : statsMap.keySet()) {
            String playerKey = playerId.toString();
            playerStats stats = statsMap.get(playerId);

            config.set(playerKey + ".totalGames", stats.getTotalGames());
            config.set(playerKey + ".wins", stats.getWins());
            config.set(playerKey + ".losses", stats.getLosses());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadStatsFromFile(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String playerKey : config.getKeys(false)) {
            UUID playerId = UUID.fromString(playerKey);
            playerStats stats = new playerStats();

            stats.setTotalGames(config.getInt(playerKey + ".totalGames"));
            stats.setWins(config.getInt(playerKey + ".wins"));
            stats.setLosses(config.getInt(playerKey + ".losses"));

            statsMap.put(playerId, stats);
        }
    }

    public static FileConfiguration getConfig(File statsFile) {
        return YamlConfiguration.loadConfiguration(statsFile);
    }
}