package org.shutterspiny.plugin.ShutterPvP;

import java.util.Arrays;

import org.bukkit.block.Block;

public class SGMap {
	
	public SGBlockType[] mineables;
	public SGBlockType[] placeables;
	public SGChest[] chests;
	public SLocation[] spawnPoints;
	public SGEntity[] entities;
	
	public SGMap(SGBlockType[] mineables, SGBlockType[] placeables, SGChest[] chests,
			SLocation[] spawnPoints, SGEntity[] entities) {
		this.mineables = mineables;
		this.placeables = placeables;
		this.chests = chests;
		this.spawnPoints = spawnPoints;
		this.entities = entities;
	}

	public SGMap() {
		this.mineables = new SGBlockType[0];
		this.placeables = new SGBlockType[0];
		this.chests = new SGChest[0];
		this.spawnPoints = new SLocation[0];
		this.entities = new SGEntity[0];
	}
	
	public boolean isMineable(Block block) {
		return Arrays.asList(mineables).contains(new SGBlockType(block));
	}
	
	public boolean isPlaceable(Block block) {
		return Arrays.asList(placeables).contains(new SGBlockType(block));
	}
	
}
