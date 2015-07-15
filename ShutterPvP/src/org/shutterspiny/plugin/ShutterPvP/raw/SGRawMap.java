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
	public RawLocation[] spawnPoints;
	public SGSpawner[] entities;
	
	public SGRawMap(SGRawBlockType[] mineables, SGRawBlockType[] placeables, SGRawChest[] chests,
			RawLocation[] spawnPoints, SGSpawner[] entities) {
		this.mineables = mineables;
		this.placeables = placeables;
		this.chests = chests;
		this.spawnPoints = spawnPoints;
		this.entities = entities;
	}

	public SGRawMap() {}

	@Override
	public SGMap convert() {
		return new SGMap(FileUtils.convertList(Arrays.asList(mineables)),
				FileUtils.convertList(Arrays.asList(placeables)),
				FileUtils.convertList(Arrays.asList(chests)),
				FileUtils.convertList(Arrays.asList(spawnPoints)),
				Arrays.asList(entities)
				);
	}
	
}
