package org.vareniodev.mc.chickensgame;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ChstartCommand implements CommandExecutor {

	private ChickensGame plugin;

    public ChstartCommand(ChickensGame plugin) {
        this.plugin = plugin;
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		if(gameManager.isGameRunning()) sender.sendMessage("Игра уже идет!");
		else gameManager.startGame();
		return true;
	}

}
