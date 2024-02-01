package org.vareniodev.mc.chickensgame;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//import org.apache.logging.log4j.core.config.Scheduled;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Wool;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;


public class gameManager {
	
	private static BukkitTask spawnTask;
	private static BukkitTask startTask;
	private static ChickensGame plugin;
    private static ConfigManager configManager;
    public gameManager(ChickensGame plugin) {
        this.plugin = plugin;
        this.configManager = new ConfigManager(plugin);
    }

    private static boolean gameRunning = false;
    private static int gameTime;
    private static int gameDelay;
    private static final String BLUE_TEAM = "blue";
    private static final String RED_TEAM = "red";

    private static final int MAX_PLAYERS_DIFFERENCE = 1;
    private static final int MIN_PLAYERS_FOR_GAME_START = 1;

    public static boolean isGameRunning() {
        return gameRunning;
    }
    public static void startGame() {
        if (!gameRunning) {
            gameRunning = true;
        }
        if (!canStartGame()) {
            //Bukkit.broadcastMessage(ChatColor.RED + "Not enough players to start the game.");
            return;
        }
        SpawnCommand.spawnSomeChicken(configManager.getDefaultCount());

        distributePlayers();

        BukkitScheduler scheduler = Bukkit.getScheduler();
        Handler h = new Handler(plugin);
        gameTime = configManager.getDefaultDuration();
        spawnTask = scheduler.runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
            	
            	int redScore = h.getRedScore();
                int blueScore = h.getBlueScore();

                if (gameTime <= 0) {
                    Component gameResult = Component.text()
                            .append(Component.text("[Курочки] ").color(TextColor.color(0, 144, 255)).decoration(TextDecoration.BOLD, true))
                            .append(Component.text("Игра от " + getTime() + " завершилась"))
                            .hoverEvent(HoverEvent.showText(getScoreInfo(redScore, blueScore)))
                            .build();

                    Bukkit.broadcast(gameResult);

                	String winner;
                	if(redScore>blueScore) {
                		endGame("red");
                	}
                	else if (blueScore>redScore) {
                		endGame("blue");
                	}
                	else endGame("draw");
                    
                    spawnTask.cancel();
                } else {
                    gameTime--;
                }

                Objective scoreObjective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("Score");

                scoreObjective.unregister();
                scoreObjective = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective("Score", "dummy");
                scoreObjective.getScore(ChatColor.GRAY + "====================").setScore(4);
                scoreObjective.getScore("Счёт " + ChatColor.RED  + ChatColor.BOLD + "красных: " + ChatColor.BOLD + redScore).setScore(3);
                scoreObjective.getScore("Счёт " + ChatColor.BLUE + ChatColor.BOLD + "синих: " + ChatColor.BOLD + blueScore).setScore(2);
                scoreObjective.getScore(ChatColor.GRAY + "=====================").setScore(1);
                scoreObjective.getScore("Время до конца игры: " + ChatColor.GREEN + "" + ChatColor.BOLD + gameTime).setScore(0);

                scoreObjective.setDisplayName(ChatColor.BOLD + "Курочки");
                
                scoreObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                }
            }
        }, 20, 20);
    }
    public static void endGame(String winner) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        configManager = new ConfigManager(plugin);
        if (gameRunning) {
            gameRunning = false;
        }
        statsManager sm = new statsManager();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        
        Team winnerTeam = scoreboard.getTeam(winner);
        
        if (winner == "blue") {
        	for (Player player : Bukkit.getOnlinePlayers()) {
        		player.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "Игра окончена!", "Победила " + ChatColor.BLUE + "" + ChatColor.BOLD + "синяя " + ChatColor.RESET + "команда!", 2, 50, 15);
        	}
        }
        
        else if (winner == "red") {
        	for (Player player : Bukkit.getOnlinePlayers()) {
        		player.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "Игра окончена!", "Победила " + ChatColor.RED + "" + ChatColor.BOLD + "красная " + ChatColor.RESET + "команда!", 2, 50, 15);
        	}
        }
        else {for (Player player : Bukkit.getOnlinePlayers()) {
    		player.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "Игра окончена!", ChatColor.BOLD + "Ничья! Силы оказались равны" , 2, 50, 15);
    	}}

        if (winner!="draw") {
        for (OfflinePlayer player : winnerTeam.getPlayers()) {
        	if (player.isOnline()) {
                Player onlinePlayer = (Player) player;

                Component winMsg = Component.text()
                                .content("Поздравляем с победой!")
                                .color(TextColor.color(12, 155, 0))
                                .build();

                onlinePlayer.sendActionBar(winMsg);
            }
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
    		sm.updateStats(player.getUniqueId(), winnerTeam.hasPlayer(player));
    		if (winnerTeam.hasPlayer(player)) {
    			player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5, 0.75f);
    		}
    	}
        }
        sm.saveStatsToFile(new File(plugin.getDataFolder(), "stats.yml"));
    	removeAllChickens();
        clearTeams();
        
        Handler h = new Handler(plugin);
        h.setBlueScore(0);
        h.setRedScore(0);
        gameDelay = configManager.getDefaultDelay();
        startTask = scheduler.runTaskTimer(plugin, new Runnable() {
        	@Override
            public void run() {
                if (gameDelay <= 0) {
                    startGame();
                    startTask.cancel();
                } else {
                    gameDelay--;
                }

                Objective scoreObjective = scoreboard.getObjective("Score");
                
                scoreObjective.unregister();
                scoreObjective = scoreboard.registerNewObjective("Score", "dummy");
                
                Team redTeam = scoreboard.getTeam("red");
                Team blueTeam = scoreboard.getTeam("blue");
                
                int redPlayers = redTeam.getSize();
                int bluePlayers = blueTeam.getSize();
                
                scoreObjective.getScore(ChatColor.GRAY + "======================").setScore(6);
                
                scoreObjective.getScore(ChatColor.RED +"" + ChatColor.BOLD + "Красная команда").setScore(5);
                scoreObjective.getScore(ChatColor.BLUE +"" + ChatColor.BOLD + "Синяя команда").setScore(3);
                
                for (OfflinePlayer player : redTeam.getPlayers()) {
                    scoreObjective.getScore(player.getName()).setScore(4);
                }
                
                for (OfflinePlayer player : blueTeam.getPlayers()) {
                	scoreObjective.getScore(player.getName()).setScore(2);
                }
                
                scoreObjective.getScore(ChatColor.GRAY + "=====================").setScore(1);
                scoreObjective.getScore("Время до начала игры: " + ChatColor.GREEN + "" + ChatColor.BOLD + gameDelay).setScore(0);

                scoreObjective.setDisplayName(ChatColor.BOLD + "Курочки");
                
                scoreObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.setScoreboard(scoreboard);
                }
            }
        	
        }, 0, 20);
    }
    private static void distributePlayers() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        for (Player player : Bukkit.getOnlinePlayers()) {

            Team playerTeam = scoreboard.getPlayerTeam(player);

            if (player.getGameMode() == GameMode.CREATIVE) {
            	player.sendActionBar("Вы находитесь в креативе, так что игра начнётся без вас");
            }
            else if (playerTeam != null) {
                teleportSpawn(playerTeam.getName(), player);
            }
            else {
                teleportSpectator(player);
            }
        }
    }
    public static void teleportSpectator(Player player){
        Location spectatorLoc = configManager.getLocation("defaultSpawn");

        player.sendActionBar(ChatColor.RED + "Вы не находитесь ни в одной команде, так что игра начнётся без вас");
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(spectatorLoc);
    }
    public static void teleportSpawn(String teamName, Player player){

        ItemStack item = new ItemStack(Material.FISHING_ROD);
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(true);
        List<String> lore = new ArrayList<>();

        meta.setLore(lore);
        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.BOLD + "Удочка");
        item.setItemMeta(meta);

        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta stickMeta = stick.getItemMeta();
        stickMeta.setDisplayName(ChatColor.BOLD + "Палка");
        stick.setItemMeta(stickMeta);
        stick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

        Location blueSpawnLocation = configManager.getLocation("blueSpawn");
        Location redSpawnLocation = configManager.getLocation("redSpawn");

        Team playerTeam = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(player);
        teamName = playerTeam.getName();
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestMeta = (LeatherArmorMeta) chest.getItemMeta();

        // ������������� ������� � ��������������� ���������� ��� �������
        if (teamName.equals(BLUE_TEAM)) {
            chestMeta.setColor(Color.BLUE);
            player.teleport(blueSpawnLocation);
        } else if (teamName.equals(RED_TEAM)) {
            chestMeta.setColor(Color.RED);
            player.teleport(redSpawnLocation);
        }
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.getInventory().addItem(item);
        player.getInventory().addItem(stick);
        chest.setItemMeta(chestMeta);
        player.getInventory().setChestplate(chest);

        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 0.75f);

        player.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "Игра началась!", ChatColor.BOLD + "Удачи!", 2, 50, 15);
        Component message = Component.text()
                .content("Пушка")
                .color(TextColor.color(18, 75, 63))
                .build();
        player.sendMessage(message);
    }
    private static void clearTeams() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    	Team blueTeam = scoreboard.getTeam("blue");
        if (blueTeam != null) {
            for (OfflinePlayer player : blueTeam.getPlayers()) {
                blueTeam.removePlayer(player);
            }
        }

        Team redTeam = scoreboard.getTeam("red");
        if (redTeam != null) {
            for (OfflinePlayer player : redTeam.getPlayers()) {
                redTeam.removePlayer(player);
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            lobbyTeleportation(player);
        }
    }
    public static boolean canStartGame() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    	int blueCount = scoreboard.getTeam("blue").getSize();
        int redCount = scoreboard.getTeam("red").getSize();

        return Math.abs(blueCount - redCount) <= MAX_PLAYERS_DIFFERENCE && Bukkit.getOnlinePlayers().size() >= MIN_PLAYERS_FOR_GAME_START;
    	//return true;
    }
    public static void lobbyTeleportation(Player player){

        ItemStack redTeamItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta redMeta = redTeamItem.getItemMeta();
        redMeta.setLocalizedName("red");
        redMeta.setDisplayName("Красная команда");
        redTeamItem.setItemMeta(redMeta);

        ItemStack blueTeamItem = new ItemStack(Material.LAPIS_BLOCK);
        ItemMeta blueMeta = blueTeamItem.getItemMeta();
        blueMeta.setLocalizedName("blue");
        blueMeta.setDisplayName("Синяя команда");
        blueTeamItem.setItemMeta(blueMeta);

        if (player.getGameMode() == GameMode.CREATIVE) {
        }
        else {
            Location spawnLoc = configManager.getLocation("lobby");

            player.setGameMode(GameMode.ADVENTURE);
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());
            player.teleport(spawnLoc);
            player.setFoodLevel(20);
            player.setHealth(20);
            player.getInventory().clear();

            player.getInventory().addItem(redTeamItem);
            player.getInventory().addItem(blueTeamItem);
        }
    }
    public static void removeAllChickens() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getType() == EntityType.CHICKEN) {
                    entity.remove();
                }
            }
        }
    }
    private static Component getScoreInfo(int redScore, int blueScore) {
        return Component.text()
                .append(Component.text("Красные: ").color(TextColor.fromHexString("#ff0000")))
                .append(Component.text(String.valueOf(redScore)).color(TextColor.fromHexString("#ff0000")).decoration(TextDecoration.BOLD, true))
                .append(Component.newline())
                .append(Component.text("Синие: ").color(TextColor.fromHexString("#0000ff")))
                .append(Component.text(String.valueOf(blueScore)).color(TextColor.fromHexString("#0000ff")).decoration(TextDecoration.BOLD, true))
                .build();
    }
    public static String getTime() {
		LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return currentTime.format(formatter);
	}
}