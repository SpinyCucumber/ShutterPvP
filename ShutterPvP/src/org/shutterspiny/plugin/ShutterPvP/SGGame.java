package org.shutterspiny.plugin.ShutterPvP;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SGGame implements Listener {
	
	private static final int COUNTDOWN = 3;
	
	private SGPlugin pluginInstance;
	public String mapName;
	private List<Player> players;
	private List<SGBlock> blocks;
	private boolean started;
	
	private static class SGBlock {
		
		private Location location;
		private Material type;
		
		public SGBlock(Location location, Material type) {
			this.location = location;
			this.type = type;
		}
		
		public SGBlock(Block block) {
			this(block.getLocation(), block.getType());
		}

		public void load() {
			location.getBlock().setType(type);
		}
		
	}
	
	public static class SGGameException extends Exception {

		private static final long serialVersionUID = 1L;

		public SGGameException(String message) {
			super(message);
		}
		
	}
	
	public void start() throws SGGameException {
		if(mapName == null) throw new SGGameException("Must select map first.");
		for(Player player : players) player.setGameMode(GameMode.SURVIVAL);
		SGMap map = getMap();
		final Set<BukkitRunnable> runnables = new HashSet<BukkitRunnable>();
		for(int i = 0; i < players.size(); i++) {
			int i1 = i % map.spawnPoints.length;
			final Player player = players.get(i);
			final Location spawnPoint = map.spawnPoints[i1].toLocation();
			BukkitRunnable runnable = new BukkitRunnable() {
				public void run() { player.teleport(spawnPoint); }
			};
			runnables.add(runnable);
			runnable.runTaskTimer(pluginInstance, 0, 1);
		}
		new BukkitRunnable() {
			int countdown = COUNTDOWN;
			public void run() {
				countdown--;
				broadcast(countdown + " until the games begin...");
				if(countdown == 0) {
					this.cancel();
					started = true;
					broadcast("THE GAMES HAVE BEGUN!");
					for(BukkitRunnable runnable : runnables) runnable.cancel();
				}
			}
		}.runTaskTimer(pluginInstance, 0, 20);
	}
	
	private SGMap getMap() {
		return pluginInstance.getMaps().get(mapName);
	}
	
	public void end() {
		broadcast("The game has ended.");
		started = false;
		for(Player player : players) player.getInventory().clear();
		SGMap map = getMap();
		map.load();
		for(SGBlock block : blocks) block.load();
		blocks.clear();
		players.clear();
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if(!started) return;
		Player player = event.getEntity();
		if(!players.contains(player)) return;
		broadcast(player.getName() + " has died!");
		List<Player> playersClone = new ArrayList<Player>(players);
		playersClone.remove(player);
		if(playersClone.size() == 1) {
			Player winner = playersClone.get(0);
			broadcast(winner.getName() + " has won!");
			end();
		} else players = playersClone;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if(!players.contains(player)) return;
		SGMap map = pluginInstance.getMaps().get(mapName);
		Block block = event.getBlock();
		if(map.isMineable(block.getType())) blocks.add(new SGBlock(block));
		else event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if(!players.contains(player)) return;
		SGMap map = pluginInstance.getMaps().get(mapName);
		Block block = event.getBlock();
		if(map.isPlaceable(block.getType())) blocks.add(new SGBlock(block.getLocation(), Material.AIR));
		else event.setCancelled(true);
	}
	
	public void join(Player player) {
		players.add(player);
		broadcast(player.getName() + " has joined the game!");
	}
	
	public void leave(Player player) {
		players.remove(player);
		broadcast(player.getName() + " has left the game.");
	}
	
	public void broadcast(String message) {
		for(Player player : players) player.sendMessage(message);
	}
	
	public SGGame(SGPlugin pluginInstance, String mapName) {
		this.pluginInstance = pluginInstance;
		this.mapName = mapName;
		players = new ArrayList<Player>();
		blocks = new ArrayList<SGBlock>();
		Bukkit.getPluginManager().registerEvents(this, pluginInstance);
	}
	
}
