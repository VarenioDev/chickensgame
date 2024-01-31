package org.vareniodev.mc.chickensgame;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class chickenSettingsCommand implements CommandExecutor, TabCompleter {

	final ConfigManager configManager;

	public chickenSettingsCommand(ChickensGame plugin) {
		this.configManager = new ConfigManager(plugin);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (args[0]==null) return false;

		if ("duration".equals(args[0])) {
			configManager.setDefaultDuration(Integer.parseInt(args[1]));
			sender.sendMessage(ChatColor.BOLD + "[Курочки] " + ChatColor.RESET + "Для игры установлено новое время: " + ChatColor.GREEN + "" + ChatColor.BOLD + args[1]);
		}
		else if ("count".equals(args[0])) {
			configManager.setDefaultCount(Integer.parseInt(args[1]));
			sender.sendMessage(ChatColor.BOLD + "[Курочки] " + ChatColor.RESET + "Для игры установлено новое количество куриц: " + ChatColor.GREEN + "" + ChatColor.BOLD + args[1]);
		}
		else if ("delay".equals(args[0])) {
			configManager.setDefaultDelay(Integer.parseInt(args[1]));
			sender.sendMessage(ChatColor.BOLD + "[Курочки] " + ChatColor.RESET + "Между играми установлена новая задержка: " + ChatColor.GREEN + "" + ChatColor.BOLD + args[1]);
		}
		else if ("info".equals(args[0]))
		{
			String[] infoMessage = new String[4];
			infoMessage[0] = ChatColor.GRAY + "" + ChatColor.BOLD + "[Курочки] " + ChatColor.RESET + "" + ChatColor.GRAY + "Список допустимых настроек:";
			infoMessage[1] = ChatColor.GRAY + "duration - устанавливает продолжительность игры";
			infoMessage[2] = ChatColor.GRAY + "count - устанавливает количество спавнящихся куриц в течение игры";
			infoMessage[3] = ChatColor.GRAY + "delay - устанавливает задержку между играми";
			sender.sendMessage(infoMessage);
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> completions = new ArrayList<>();

		if (args.length == 1) {
			completions.add("duration");
			completions.add("delay");
			completions.add("count");
			completions.add("info");
		}

		return completions;
	}

}
