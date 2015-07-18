package org.shutterspiny.plugin.ShutterPvP;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.shutterspiny.lib.PluginUtils.command.AbstractCommand.CommandException;
import org.shutterspiny.lib.PluginUtils.misc.ObjectiveWriter;
import org.shutterspiny.plugin.ShutterPvP.entity.SGSpawner;
import org.shutterspiny.plugin.ShutterPvP.entity.SGSpawner.SpawnerTimer;
import org.shutterspiny.plugin.ShutterPvP.map.SGBlockType;
import org.shutterspiny.plugin.ShutterPvP.map.SGMap;

public class SGGame implements Listener {
	
	private static abstract class GameEvent {
		
		private int time;
		private String name, message;
		
		public GameEvent(int time, String name, String message) {
			this.time = time;
			this.name = name;
			this.message = message;
		}

		public abstract void run(SGGame game);
		
	}
	
	private class GameTimer extends BukkitRunnable {
		
		private int time, lastTime, eventIndex, seconds, factor;
		private boolean running;
		
		public GameTimer() {
			seconds = pluginInstance.getConfig().getInt("Seconds");
			factor = pluginInstance.getConfig().getInt("Factor");
		}
		
		private GameEvent event() {
			return events.get(eventIndex);
		}
		
		public void run() {
			if(running) {
				if(eventIndex < events.size()) {
					int timeUntilEvent = event().time - time + lastTime;
					if(timeUntilEvent == 0) {
						lastTime = time;
						event().run(SGGame.this);
						broadcast(event().message);
						eventIndex++;
					} else if(timeUntilEvent <= seconds || timeUntilEvent % factor == 0) {
						broadcast(ChatColor.GREEN + "" + timeUntilEvent + " seconds until " + event().name);
					}
				}
				time++;
			}
			updateScoreboard();
			for(SpawnerTimer spawner : spawnerTimers) spawner.update();
		}
		
		public void start() {
			running = true;
		}
		
		public void stop() {
			running = false;
			eventIndex = 0;
			time = 0;
			lastTime = 0;
		}

		private void updateScoreboard() {
			for(String entry : scoreboard.getEntries()) scoreboard.resetScores(entry);
			ObjectiveWriter writer = new ObjectiveWriter(scoreboard.getObjective("main"));
			Date date = new Date(time * 1000);
			writer.write(ChatColor.GREEN + "Time Elapsed: " + DATE_FORMAT.format(date));
			writer.writeSpace();
			writer.write(ChatColor.GREEN + "Next Event: " + events.get(eventIndex).name);
			writer.writeSpace();
			for(Player player : players) writer.write(ChatColor.YELLOW + player.getName());
			writer.write(ChatColor.GREEN + "Players In-Game");
			writer.writeSpace();
			writer.write(ChatColor.GREEN + "Players Dead: " + (players.size() - alivePlayers.size()));
		}
		
	}
	
	private class PlayerFreezer extends BukkitRunnable {
		
		private Player player;
		private Location loc;
		
		public PlayerFreezer(Player player, Location loc) {
			this.player = player;
			this.loc = loc;
			this.runTaskTimer(pluginInstance, 0, 1);
		}

		@Override
		public void run() {
			player.teleport(loc);
		}
		
	}
	
	private static void clear(Player player) {
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setLevel(0);
		player.setExp(0);
		PlayerInventory inventory = player.getInventory();
		inventory.clear();
		inventory.setArmorContents(new ItemStack[]{null, null, null, null});
	}
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("mm:ss");
	
	private SGPlugin pluginInstance;
	private SGMap map;
	private Scoreboard scoreboard;
	private List<Player> players, alivePlayers;
	private List<GameEvent> events;
	private List<PlayerFreezer> freezers;
	private List<SpawnerTimer> spawnerTimers;
	
	private Map<Block, SGBlockType> changedBlocks;
	
	private GameTimer timer;
	
	public SGGame(SGPlugin pluginInstance) {
		
		this.pluginInstance = pluginInstance;
		players = new ArrayList<Player>();
		alivePlayers = new ArrayList<Player>();
		events = new ArrayList<GameEvent>();
		freezers = new ArrayList<PlayerFreezer>();
		changedBlocks = new HashMap<Block, SGBlockType>();
		timer = new GameTimer();
		timer.runTaskTimer(pluginInstance, 0, 20);
		
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective main = scoreboard.registerNewObjective("main", "dummy");
		main.setDisplaySlot(DisplaySlot.SIDEBAR);
		main.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SHUTTER-GAMES");
		
		events.add(new GameEvent(pluginInstance.getConfig().getInt("Countdown"), "unfreeze", "Players have been unfrozen! GO!") {
			public void run(SGGame game) {
				for(PlayerFreezer freezer : game.freezers) freezer.cancel();
				game.freezers.clear();
				game.alivePlayers = new ArrayList<Player>(game.players);
			}
		});
		events.add(new GameEvent(pluginInstance.getConfig().getInt("Invincible"), "vulnerability", "Players are now vulnerable. Attack!") {
			public void run(SGGame game) {
				//Do something cool
			}
		});
		Bukkit.getPluginManager().registerEvents(this, pluginInstance);
		
	}
	
	public void broadcast(String message) {
		message = SGPlugin.TAG + message;
		for(Player player : players) player.sendMessage(message);
	}
	
	public void end() throws CommandException {
		
		if(!timer.running) throw new CommandException("The game has not yet started.");
		broadcast(ChatColor.GREEN + "" + ChatColor.BOLD + "The game has ended.");
		
		timer.stop();
		
		for(Player player : players) clear(player);
		
		for(Entry<Block, SGBlockType> entry : changedBlocks.entrySet())
			entry.getValue().set(entry.getKey());
		for(SpawnerTimer spawner : spawnerTimers) spawner.removeEntities();
		
		boolean playersLeave = pluginInstance.getConfig().getBoolean("PlayersLeave");
		if(playersLeave) {
			broadcast(ChatColor.GREEN + "" + ChatColor.BOLD + "You have left the game.");
			for(Player player : players) player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			players.clear();
		}
		
		alivePlayers.clear();
		changedBlocks.clear();
		spawnerTimers.clear();
		
	}
	
	public void join(Player player) throws CommandException {
		if(players.contains(player)) throw new CommandException("You are already in the game.");
		players.add(player);
		player.setScoreboard(scoreboard);
		broadcast(ChatColor.GREEN + "" + ChatColor.BOLD + player.getName() + " has joined the game!");
	}
	
	public void leave(Player player) throws CommandException {
		if(!players.contains(player)) throw new CommandException("You are not in the game.");
		broadcast(ChatColor.GREEN + "" + ChatColor.BOLD + player.getName() + " has left the game.");
		if(timer.running) removePlayer(player);
		players.remove(player);
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(!timer.running || !alivePlayers.contains(event.getPlayer())) return;
		Block block = event.getBlock();
		if(map.isMineable(block)) changedBlocks.put(block, new SGBlockType(block));
		else if(!map.isPlaceable(block)) event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(!timer.running || !alivePlayers.contains(event.getPlayer())) return;
		Block block = event.getBlock();
		if(map.isPlaceable(block)) {
			if(!changedBlocks.containsKey(block)) changedBlocks.put(block, new SGBlockType(Material.AIR, (byte) 0));
		} else event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if(!(timer.eventIndex == 1) || !(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		if(alivePlayers.contains(player)) event.setCancelled(true);
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		if(!timer.running || !(event.getEntity() instanceof TNTPrimed)) return;
		TNTPrimed entity = (TNTPrimed) event.getEntity();
		if(!alivePlayers.contains(entity.getSource())) return;
		for(Block block : event.blockList()) changedBlocks.put(block, new SGBlockType(block));
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if(!timer.running || !alivePlayers.contains(player)) return;
		broadcast(player.getName() + " has died!");
		if(removePlayer(player)) event.getDrops().clear();
	}
	
	private boolean removePlayer(Player player) {
		alivePlayers.remove(player);
		boolean oneLeft = alivePlayers.size() == 1;
		if(oneLeft) {
			Player winner = alivePlayers.get(0);
			broadcast(ChatColor.GOLD + "" + ChatColor.MAGIC + "X" + ChatColor.RESET +
					"" + ChatColor.GOLD + " " + winner.getName() + " has won the SHUTTER-GAMES! " + ChatColor.MAGIC + "X");
			try { end(); } catch (CommandException e) {}
		}
		return oneLeft;
	}
	
	public void setMap(String mapName) throws CommandException {
		if(!pluginInstance.getMaps().containsKey(mapName))
			throw new CommandException("That map does not exist.");
		map = pluginInstance.getMaps().get(mapName);
	}
	
	public void start() throws CommandException {
		
		if(players.size() < 1) throw new CommandException("Not enough players!");
		if(map == null) throw new CommandException("Must select map first.");
		if(timer.running) throw new CommandException("The game is already in progress.");
		if(map.spawnPoints.size() == 0) throw new CommandException("No spawnpoints!");
		
		map.load();

		for(int i = 0; i < players.size(); i++) {
			Location spawn = map.spawnPoints.get(i % map.spawnPoints.size());
			Player player = players.get(i);
			clear(player);
			player.setGameMode(GameMode.SURVIVAL);
			freezers.add(new PlayerFreezer(player, spawn));
		}
		for(SGSpawner spawner : map.spawners) spawnerTimers.add(spawner.new SpawnerTimer());
		
		timer.start();
		
	}
	
}
