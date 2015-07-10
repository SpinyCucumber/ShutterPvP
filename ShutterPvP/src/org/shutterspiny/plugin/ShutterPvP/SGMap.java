package org.shutterspiny.plugin.ShutterPvP;

import java.util.Arrays;

import org.bukkit.Material;

public class SGMap {
	
	public String[] mineables;
	public String[] placeables;
	public SGChest[] chests;
	public SLocation[] spawnPoints;
	
	public SGMap(String[] mineables, String[] placeables, SGChest[] chests,
			SLocation[] spawnPoints) {
		this.mineables = mineables;
		this.placeables = placeables;
		this.chests = chests;
		this.spawnPoints = spawnPoints;
	}

	public SGMap() {
		this.mineables = new String[0];
		this.placeables = new String[0];
		this.chests = new SGChest[0];
		this.spawnPoints = new SLocation[0];
	}
	
	public boolean isMineable(Material type) {
		return Arrays.asList(mineables).contains(type.name());
	}
	
	public boolean isPlaceable(Material type) {
		return Arrays.asList(placeables).contains(type.name());
	}
	
	//Place blocks and randomize chests
	public void load() {
		for(SGChest chest : chests) chest.load();
	}
	
}
