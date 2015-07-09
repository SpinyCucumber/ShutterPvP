package org.shutterspiny.plugin.ShutterPvP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SGMap {
	
	public SGMineable[] mineables;
	public SGChest[] chests;
	public SLocation[] spawnPoints;

	public SGMap(SGMineable[] mineables, SGChest[] chests, SLocation[] spawnPoints) {
		this.mineables = mineables;
		this.chests = chests;
		this.spawnPoints = spawnPoints;
	}
	
	public SGMap() {
		this.mineables = new SGMineable[0];
		this.chests = new SGChest[0];
		this.spawnPoints = new SLocation[0];
	}
	
	//Place blocks and randomize chests
	public void load() {
		List<SGBlock> blocks = new ArrayList<SGBlock>();
		blocks.addAll(Arrays.asList(mineables));
		blocks.addAll(Arrays.asList(chests));
		for(SGBlock block : blocks) block.load();
	}
	
}
