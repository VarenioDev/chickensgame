package org.vareniodev.mc.chickensgame;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class statsCommand implements CommandExecutor {

    private static ChickensGame plugin;

    public statsCommand(ChickensGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            getStats(player);
            return true;
        } else {
            sender.sendMessage("������� ����� ������������ ������ �����!");
            return false;
        }
    }

    private void getStats(Player player) {
    	File statsFile = plugin.getStats();
        FileConfiguration config = statsManager.getConfig(statsFile);
        //player.sendMessage(config.getName());
        String playerUUID = player.getUniqueId().toString();
        //player.sendMessage("��� UUID: " + playerUUID);
        
        if (config.contains(playerUUID)) {
            //player.sendMessage("���������� ������������ ��� " + playerUUID + ": " + config.getConfigurationSection(playerUUID).getValues(true));
        	String[] stats = new String[4];
        	stats[0] = ChatColor.BOLD + "[Курочки] " + ChatColor.RESET + "Выша статистика:";
        	stats[1] = ChatColor.WHITE + "" + ChatColor.BOLD + "Всего игр: " + ChatColor.RESET + config.getInt(playerUUID + ".totalGames", 0);
        	stats[2] = ChatColor.GREEN + "" + ChatColor.BOLD + "Побед: " +ChatColor.RESET+ config.getInt(playerUUID + ".wins", 0);
        	stats[3] = ChatColor.RED + "" + ChatColor.BOLD + "Поражений: " +ChatColor.RESET+ config.getInt(playerUUID + ".losses", 0);
            //stats[4] = ChatColor.RED + "��� ���������� ���������� �����������!";
            player.sendMessage(stats);
            return;
        } else {
            player.sendMessage("���� " + playerUUID + " �� ������ � ������������!");
            return;
        }
    }
}