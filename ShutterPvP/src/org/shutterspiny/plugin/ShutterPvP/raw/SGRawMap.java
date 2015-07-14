package org.shutterspiny.plugin.ShutterPvP.raw;

import java.util.Arrays;

import org.shutterspiny.lib.PluginUtils.files.FileUtils;
import org.shutterspiny.lib.PluginUtils.mapping.Convertable;
import org.shutterspiny.plugin.ShutterPvP.entity.SGSpawner;
import org.shutterspiny.plugin.ShutterPvP.map.SGMap;

public class SGRawMap implements Convertable<SGMap> {
	
	public SGRawBlockType[] mineables;
	public SGRawBlockType[] placeables;
	public SGRawChest[] chests;
	public SLocation[] spawnPoints;
	public SGSpawner[] entities;
	
	public SGRawMap(SGRawBlockType[] mineables, SGRawBlockType[] placeables, SGRawChest[] chests,
			SLocation[] spawnPoints, SGSpawner[] entities) {
		this.mineables = mineables;
		this.placeables = placeables;
		this.chests = chests;
		this.spawnPoints = spawnPoints;
		this.entities = entities;
	}

	public SGRawMap() {}

	@Override
	public SGMap convert() {
		return new SGMap(Arrays.asList(FileUtils.convertArray(mineables)),
				Arrays.asList(FileUtils.convertArray(placeables)),
				Arrays.asList(FileUtils.convertArray(chests)),
				Arrays.asList(FileUtils.convertArray(spawnPoints)),
				Arrays.asList(entities)
				);
	}
	
}
