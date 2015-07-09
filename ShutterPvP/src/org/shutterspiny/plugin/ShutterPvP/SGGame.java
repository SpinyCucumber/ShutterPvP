package org.shutterspiny.plugin.ShutterPvP;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SGGame implements Listener {
	
	private SGPlugin pluginInstance;
	private String mapName;
	private List<Player> players;
	
	public void start() {
		SGMap map = pluginInstance.getMaps().get(mapName);
		map.load();
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
			int countdown = 20;
			public void run() {
				countdown--;
				broadcast(countdown + " until the games begin...");
				if(countdown == 0) {
					this.cancel();
					broadcast("THE GAMES HAVE BEGUN!");
					for(BukkitRunnable runnable : runnables) runnable.cancel();
				}
			}
		}.runTaskTimer(pluginInstance, 0, 20);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if(!players.contains(player)) return;
		broadcast(player.getName() + " has died!");
		players.remove(player);
	}
	
	public void join(Player player) {
		players.add(player);
		broadcast(player.getName() + " has joined the game!");
	}
	
	public void broadcast(String message) {
		for(Player player : players) player.sendMessage(message);
	}
	
	public SGGame(SGPlugin pluginInstance, String mapName) {
		this.pluginInstance = pluginInstance;
		this.mapName = mapName;
		players = new ArrayList<Player>();
		Bukkit.getPluginManager().registerEvents(this, pluginInstance);
	}
	
}
