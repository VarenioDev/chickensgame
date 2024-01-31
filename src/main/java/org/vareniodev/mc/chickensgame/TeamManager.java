package org.vareniodev.mc.chickensgame;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamManager {
	
	private static Scoreboard scoreboard;

    public TeamManager() {
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }
	
	public static void joinTeam(String teamName, Player player) {
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.setAllowFriendlyFire(false);
            if (teamName == "blue") team.setPrefix(ChatColor.BLUE + "" + ChatColor.BOLD + "⚔ " + ChatColor.RESET);
            else team.setPrefix(ChatColor.RED + "" + ChatColor.BOLD + "⚔ " + ChatColor.RESET);
        }

        if (gameManager.isGameRunning()){
            player.sendMessage(Component.text("Игра уже идёт!")
                    .color(TextColor.color(0xED0100)));
        }

        else if (team.hasEntry(player.getName())){
            String currentTeam = scoreboard.getPlayerTeam(player).getName();

            if(currentTeam.equals(teamName)){
                player.sendMessage(Component.text("Вы уже находитесь в команде " + teamName)
                        .color(TextColor.color(0xED0100)));
            }

        }
        else if (!canSwitchTeams(teamName)){
            player.sendMessage(Component.text("В команде " + teamName + " уже слишком много игроков")
                    .color(TextColor.color(0xED0100)));
        }
            else {
                team.removeEntry(player.getName());
                team.addEntry(player.getName());
                player.sendMessage(Component.text("Вы успешно перешли в команду " + teamName)
                        .color(TextColor.color(0x9B20)));
            }

        updatePlayerColors(player);
    } 

    private static boolean canSwitchTeams(String newTeam) {
        int blueCount = scoreboard.getTeam("blue").getSize();
        int redCount = scoreboard.getTeam("red").getSize();

        if (newTeam.equals("blue")) {
            return Math.abs(blueCount - redCount) <= 1 || (blueCount + redCount) >= 3;
        } else {
            return Math.abs(redCount - blueCount) <= 1 || (blueCount + redCount) >= 3;
        }
    }

    private static void updatePlayerColors(Player player) {
        Team team = scoreboard.getPlayerTeam(player);
        if (team != null) {
            String teamName = team.getName();
            if (teamName.equals("blue")) {
                player.setDisplayName(ChatColor.BLUE + player.getName() + ChatColor.RESET);
                player.setPlayerListName(ChatColor.BLUE + "" + ChatColor.BOLD + "⚔ " + ChatColor.RESET + player.getName());
            } else if (teamName.equals("red")) {
            	player.setDisplayName(ChatColor.RED + player.getName() + ChatColor.RESET);
                player.setPlayerListName(ChatColor.RED + "" + ChatColor.BOLD + "⚔ " + ChatColor.RESET + player.getName());
            }
        }
    }
}
