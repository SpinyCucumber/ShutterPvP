package org.shutterspiny.plugin.ShutterPvP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SGMap {
	
	private SGMineable[] mineables;
	private SGChest[] chests;
	private SLocation[] spawnPoints;
	
	public SGMineable[] getMineables() {
		return mineables;
	}

	public SGChest[] getChests() {
		return chests;
	}

	public SLocation[] getSpawnPoints() {
		return spawnPoints;
	}

	public SGMap(SGMineable[] mineables, SGChest[] chests, SLocation[] spawnPoints) {
		this.mineables = mineables;
		this.chests = chests;
		this.spawnPoints = spawnPoints;
	}
	
	//Place blocks and randomize chests
	public void load() {
		List<SGBlock> blocks = new ArrayList<SGBlock>();
		blocks.addAll(Arrays.asList(mineables));
		blocks.addAll(Arrays.asList(chests));
		for(SGBlock block : blocks) block.load();
	}
	
}
