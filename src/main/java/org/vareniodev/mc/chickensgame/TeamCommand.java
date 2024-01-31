package org.vareniodev.mc.chickensgame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamCommand implements CommandExecutor {

    private final Scoreboard scoreboard;

    public TeamCommand() {
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    TeamManager teamManager = new TeamManager();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Команда доступна только игрокам!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Используйте: /team <blue/red>");
            return true;
        }

        String teamName = args[0].toLowerCase();

        if (!teamName.equals("blue") && !teamName.equals("red")) {
            player.sendMessage(ChatColor.RED + "Допустимые команды: /team <blue/red>");
            return true;
        }
        teamManager.joinTeam(teamName, player);
        return true;
    }

}