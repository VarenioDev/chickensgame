package org.vareniodev.mc.chickensgame;

import java.util.Objects;
import java.util.Random;

import org.bukkit.Bukkit;

import org.bukkit.Location;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;

import org.bukkit.entity.Player;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.ChatColor;


public class SpawnCommand implements CommandExecutor{

	private static BukkitTask spawnTask;
	private static ChickensGame plugin;

	public SpawnCommand(ChickensGame plugin) {
		SpawnCommand.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player p = (Player) sender;
		Random rnd = new Random();

		Location loc = new Location(p.getWorld(), 12, 13, 8);
		BukkitScheduler scheduler = Bukkit.getScheduler();
		spawnSomeChicken(Integer.parseInt(args[0]));
		return true;
	}
	
	public static void spawnSomeChicken(int count) {
		BukkitScheduler scheduler = Bukkit.getScheduler();
		
		Location loc = new Location(Bukkit.getWorld("world"), 12, 13, 8);
		Random rnd = new Random();
		
		spawnTask = scheduler.runTaskTimer(plugin, new Runnable() {
            int i = 0;
            int iCost = 0;

            @Override
            public void run() {
                iCost = rnd.nextInt(5) + 1;
                Entity e = Bukkit.getWorld("world").spawn(loc, Chicken.class);
                e.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + "+" + iCost);
                e.setGlowing(true);
                e.setInvulnerable(true);
                e.setSilent(true);
                e.setMetadata("cost", new FixedMetadataValue(plugin, iCost));

                if (i + 1 == count) {
                    spawnTask.cancel();
                } else {
                    i++;
                }
            }
        }, 20, 60);
	}
	
	public int getInt(Entity entity, Plugin plugin, String key) {
		
		for(MetadataValue value : entity.getMetadata(key)) 
		{
			
			if(Objects.equals(value.getOwningPlugin(), plugin)) {
				return value.asInt();
			}
			
		}
		throw new IllegalArgumentException("�� ������� �������� � ������: " + key);
		
	}

}

