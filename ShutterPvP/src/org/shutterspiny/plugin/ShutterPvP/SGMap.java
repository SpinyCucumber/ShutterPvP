package org.shutterspiny.plugin.ShutterPvP;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

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

	//Debug junk
	public static void main(String[] args) throws Exception {
		new ObjectMapper().writeValue(new File("Derp"), new SGMap(new SGMineable[]{
				new SGMineable(new SLocation("overworld", 5, 20, 10), "DIAMOND_BLOCK")},
				new SGChest[]{}, 
				new SLocation[]{}));
	}
	
}
