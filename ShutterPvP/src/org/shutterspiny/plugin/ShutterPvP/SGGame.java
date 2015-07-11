package org.shutterspiny.plugin.ShutterPvP;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class SGGame implements Listener {
	
	private SGPlugin pluginInstance;
	public String mapName;
	private List<Player> players;
	private List<SGBlock> blocks;
	private List<Entity> spawnedEntities;
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
	
	public class SGGameException extends Exception {

		private static final long serialVersionUID = 1L;

		public SGGameException(String message) {
			super(ChatColor.RED + "" + ChatColor.BOLD + message);
		}
		
	}
	
	public void start() throws SGGameException {
		if(mapName == null) throw new SGGameException("Must select map first.");
		if(started == true) throw new SGGameException("The game is already in progress.");
		for(Player player : players) player.setGameMode(GameMode.SURVIVAL);
		SGMap map = getMap();
		for(SGChest chest : map.chests) chest.load();
		for(SGEntity entity : map.entities) spawnedEntities.add(entity.spawn());
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
			private int time = pluginInstance.getConfig().getInt("Countdown"),
					seconds = pluginInstance.getConfig().getInt("Seconds"),
					factor = pluginInstance.getConfig().getInt("Factor");
			public void run() {
				if(time == 0) {
					this.cancel();
					started = true;
					broadcast(ChatColor.GOLD + "" + ChatColor.MAGIC + "X" + ChatColor.RESET +
							"" + ChatColor.GOLD + " THE GAMES HAVE BEGUN! " + ChatColor.MAGIC + "X");
					for(BukkitRunnable runnable : runnables) runnable.cancel();
				} else if(time <= seconds || time % factor == 0)
					broadcast(ChatColor.GREEN + "" + ChatColor.BOLD + "" +
					time + ChatColor.RESET + "" + ChatColor.GREEN + " seconds until the games begin...");
				time--;
			}
		}.runTaskTimer(pluginInstance, 0, 20);
	}
	
	private SGMap getMap() {
		return pluginInstance.getMaps().get(mapName);
	}
	
	public void end() throws SGGameException {
		if(!started) throw new SGGameException("The game has not yet started.");
		broadcast(ChatColor.GREEN + "" + ChatColor.BOLD + "The game has ended.");
		started = false;
		for(Player player : players) {
			PlayerInventory inventory = player.getInventory();
			inventory.clear();
			inventory.setArmorContents(new ItemStack[]{null, null, null, null});
		}
		for(SGBlock block : blocks) block.load();
		for(Entity entity : spawnedEntities) if(entity.isValid()) entity.remove();
		boolean playersLeave = pluginInstance.getConfig().getBoolean("PlayersLeave");
		if(playersLeave) players.clear();
		blocks.clear();
		spawnedEntities.clear();
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if(!started) return;
		Player player = event.getEntity();
		if(!players.contains(player)) return;
		broadcast(player.getName() + " has died!");
		if(removePlayer(player)) event.getDrops().clear();
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(!started) return;
		Player player = event.getPlayer();
		if(!players.contains(player)) return;
		SGMap map = pluginInstance.getMaps().get(mapName);
		Block block = event.getBlock();
		if(map.isMineable(block)) blocks.add(new SGBlock(block));
		else if(!map.isPlaceable(block)) event.setCancelled(true);
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		if(!(event.getEntityType() == EntityType.PRIMED_TNT) || !started) return;
		TNTPrimed entity = (TNTPrimed) event.getEntity();
		System.out.println(entity.getSource().getName());
		if(!players.contains(entity.getSource())) return;
		for(Block block : event.blockList()) blocks.add(new SGBlock(block));
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(!started) return;
		Player player = event.getPlayer();
		if(!players.contains(player)) return;
		SGMap map = pluginInstance.getMaps().get(mapName);
		Block block = event.getBlock();
		if(map.isPlaceable(block)) blocks.add(new SGBlock(block.getLocation(), Material.AIR));
		else event.setCancelled(true);
	}
	
	public void join(Player player) throws SGGameException {
		if(players.contains(player)) throw new SGGameException("You are already in the game.");
		players.add(player);
		broadcast(ChatColor.GREEN + "" + ChatColor.BOLD + player.getName() + " has joined the game!");
	}
	
	public void leave(Player player) throws SGGameException {
		if(!players.contains(player)) throw new SGGameException("You are not in the game.");
		broadcast(ChatColor.GREEN + "" + ChatColor.BOLD + player.getName() + " has left the game.");
		removePlayer(player);
	}
	
	private boolean removePlayer(Player player) {
		List<Player> playersClone = new ArrayList<Player>(players);
		playersClone.remove(player);
		boolean oneLeft = playersClone.size() == 1;
		if(oneLeft) {
			Player winner = playersClone.get(0);
			broadcast(ChatColor.GOLD + "" + ChatColor.MAGIC + "X" + ChatColor.RESET +
					"" + ChatColor.GOLD + " " + winner.getName() + " has won the SHUTTER-GAMES! " + ChatColor.MAGIC + "X");
			try { end(); } catch (SGGameException e) {}
		} else players = playersClone;
		return oneLeft;
	}
	
	public void broadcast(String message) {
		message = SGPlugin.TAG + message;
		for(Player player : players) player.sendMessage(message);
	}
	
	public SGGame(SGPlugin pluginInstance, String mapName) {
		this.pluginInstance = pluginInstance;
		this.mapName = mapName;
		players = new ArrayList<Player>();
		blocks = new ArrayList<SGBlock>();
		spawnedEntities = new ArrayList<Entity>();
		Bukkit.getPluginManager().registerEvents(this, pluginInstance);
	}
	
}
