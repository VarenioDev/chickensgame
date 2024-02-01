package org.vareniodev.mc.chickensgame;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.Async;
//import net.minecraft.server.v1_12_R1.Item;

public class Handler implements Listener{

	private static BukkitTask msgTask;
	static int blueScore = 0;
	static int redScore = 0;

	private final Scoreboard scoreboard;
	
	ChickensGame plugin;

    public Handler(ChickensGame plugin) {
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        this.plugin = plugin;
    }
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		String team = Objects.requireNonNull(player.getScoreboard().getPlayerTeam(player)).getName();
		if(player.getGameMode().equals(GameMode.CREATIVE)) return;
		if (gameManager.isGameRunning()){
			gameManager.teleportSpectator(player);

			sendActionBarRepeatedly(player, 3, 20);

			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				gameManager.teleportSpawn(team, player);
			}, 60);
		}
		else gameManager.lobbyTeleportation(player);
	}

	private void sendActionBarRepeatedly(Player player, int i, int interval) {
		final int[] times = {i};
		msgTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			player.sendActionBar(Component.text("До возрождения: " + times[0]));
            times[0]--;

			if (times[0] <= 0) {
				msgTask.cancel();
			}
		}, 0, interval);
	}
	@EventHandler
    public void onChickenSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Chicken) {
            Chicken chicken = (Chicken) event.getEntity();
            chicken.setAdult();
            chicken.setAgeLock(true);
            chicken.setAge(0);
        }
    }

	@EventHandler
	public void chatEvent(AsyncPlayerChatEvent apse){
		Player p = apse.getPlayer();
		String msg = apse.getMessage();

		Component chatMsg = Component.text()
				.content(p.getPlayerListName() + " -> ").append(Component.text(msg).color(TextColor.color(166, 168, 150)))
				.build();
		p.sendPlayerListFooter(Component.text("Привет"));
		apse.setCancelled(true);

		Bukkit.broadcast(chatMsg);
	}

    @EventHandler
	public void join(PlayerJoinEvent pje) {
    	Player p = pje.getPlayer();
		gameManager.lobbyTeleportation(p);
    	p.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "Привет!", ChatColor.DARK_GREEN + "" + ChatColor.ITALIC +  "Время выбрать команду! (/team)", 2, 50, 15);
		String eternalIP = "Вечный IP: " + Bukkit.getServer().getIp();
		Component textComponent = Component.text()
				.append(Component.space())
				.append(Component.text(eternalIP))
				.build();
		p.sendPlayerListFooter(textComponent);
	}
    
    @EventHandler
    private void onDropItem(PlayerDropItemEvent pdie) {
        org.bukkit.entity.Item item = pdie.getItemDrop();
        item.setVelocity(item.getVelocity().zero()); // ��������� �������� � �������
        trackItem(pdie.getPlayer(), item);
    }

    private void trackItem(final Player player, final org.bukkit.entity.Item item) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (item.isValid()) {
                    if (item.isOnGround()) {
                        player.getInventory().addItem(item.getItemStack());
                        item.remove(); // �������� �������� ����� ��������
                        cancel();
                    }
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }
    
    @EventHandler
    private void onInteract(PlayerInteractEvent pie) {
    	if (!pie.getAction().toString().contains("RIGHT") || pie.getItem() == null) {
    		return;
    	}
    	String name = pie.getItem().getItemMeta().getLocalizedName();
    	if (name.equals("blue") || name.equals("red")) {
    		TeamManager.joinTeam(name, pie.getPlayer());
    	}
    	else return;
	}
	
	@EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("ChickensGame");
        if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            Player player = event.getPlayer();
            Entity caughtEntity = event.getCaught();

			caughtEntity.setVelocity(caughtEntity.getVelocity().multiply(10.0));

            if (caughtEntity != null) {
                String playerName = player.getName();
                caughtEntity.setMetadata("lastFisherman", new FixedMetadataValue(plugin, playerName));
            }
        }
    }
	
	@EventHandler
	public void interact(EntityInteractEvent eie) 
	{
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("ChickensGame");
		SpawnCommand sp = new SpawnCommand((ChickensGame) plugin);
		int cost = sp.getInt(eie.getEntity(), plugin, "cost");
		String lf = eie.getEntity().getMetadata("lastFisherman").get(0).asString();
		
		eie.getEntity().remove();
		eie.getEntity().getNearbyEntities(10, 10, 10);
		
		Location registerLoc1 = new Location(eie.getEntity().getWorld(), 9,9,11);
		Location registerLoc2 = new Location(eie.getEntity().getWorld(), 15,9,5);
		
		Location eLoc = eie.getEntity().getLocation();
		
		Location loc = eie.getBlock().getLocation();
		
		if(eie.getEntityType() != EntityType.CHICKEN) return;
		List<Player> pList = eie.getEntity().getWorld().getPlayers();
		
		chickenCount("blue", registerLoc1, eie, cost, (lf + " принёс курочку в  " + ChatColor.BLUE + ChatColor.BOLD + "синюю " + ChatColor.RESET + "лунку!" + ChatColor.BLUE + "" + ChatColor.BOLD + " (+" + cost + ")"), ChatColor.BLUE + "Счёт синих:");
		
		chickenCount("red" , registerLoc2, eie, cost, (lf + " принёс курочку в " + ChatColor.RED + ChatColor.BOLD + "красную " + ChatColor.RESET + "лунку!" + ChatColor.RED + "" + ChatColor.BOLD + " (+" + cost + ")"), ChatColor.RED + "Счёт красных:");
		
	}
	
	public void chickenCount(String m, Location loc, EntityInteractEvent e, int cost, String message, String color)
	{
		if((int)e.getEntity().getLocation().getX() == loc.getX())
			if((int)e.getEntity().getLocation().getY() == loc.getY())
				if((int)e.getEntity().getLocation().getZ() == loc.getZ()) {

			if(Objects.equals(m, "blue")) {
				blueScore+=cost;
			}
			else redScore+=cost;

			for (String p : Bukkit.getScoreboardManager().getMainScoreboard().getTeam(m).getEntries()){
				Player player = Bukkit.getPlayer(p);
				if (player!=null && player.isOnline()){
					player.showTitle(
							Title.title(scoreText(cost),
									Component.empty(),
									Title.Times.times(Duration.ofSeconds(1),Duration.ofSeconds(1),Duration.ofSeconds(1)))
					);
					player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
				}
			}
			Bukkit.broadcast(Component.text().content(message).build());
		}

	}
	public Component scoreText(int val){
		String score = switch (val) {
			case 1 -> " очко!";
			case 5 -> " очков!";
			default -> " очка!";
		};

		return Component.text()
				.content("+" + val + score)
				.color(TextColor.color(0, 155, 32))
				.build();
	}
	public int getRedScore() {return redScore;}
	public int getBlueScore() {return blueScore;}
	public void setRedScore(int newScore) {redScore = newScore;}
	public void setBlueScore(int newScore) {blueScore = newScore;}
}