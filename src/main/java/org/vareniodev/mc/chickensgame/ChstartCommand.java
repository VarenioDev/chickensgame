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
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		plugin.checkGameStart();
		return true;
	}

}
