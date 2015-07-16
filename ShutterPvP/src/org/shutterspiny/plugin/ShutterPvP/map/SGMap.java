package org.shutterspiny.plugin.ShutterPvP.map;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.shutterspiny.lib.PluginUtils.files.FileUtils;
import org.shutterspiny.lib.PluginUtils.mapping.Convertable;
import org.shutterspiny.plugin.ShutterPvP.SGPlugin;
import org.shutterspiny.plugin.ShutterPvP.entity.SGSpawner;
import org.shutterspiny.plugin.ShutterPvP.item.SGChest;
import org.shutterspiny.plugin.ShutterPvP.raw.SGRawBlockType;
import org.shutterspiny.plugin.ShutterPvP.raw.SGRawChest;
import org.shutterspiny.plugin.ShutterPvP.raw.SGRawMap;
import org.shutterspiny.plugin.ShutterPvP.raw.RawLocation;

public class SGMap implements Convertable<SGRawMap> {
	
	public List<SGBlockType> mineables, placeables;
	public List<SGChest> chests;
	public List<Location> spawnPoints;
	public List<SGSpawner> spawners;
	
	public SGMap() {
		mineables = new ArrayList<SGBlockType>();
		placeables = new ArrayList<SGBlockType>();
		chests = new ArrayList<SGChest>();
		spawnPoints = new ArrayList<Location>();
		spawners = new ArrayList<SGSpawner>();
	}

	public SGMap(List<SGBlockType> mineables, List<SGBlockType> placeables,
			List<SGChest> chests, List<Location> spawnPoints,
			List<SGSpawner> spawners) {
		this.mineables = mineables;
		this.placeables = placeables;
		this.chests = chests;
		this.spawnPoints = spawnPoints;
		this.spawners = spawners;
	}
	
	public void setPlugin(SGPlugin plugin) {
		for(SGChest chest : chests) chest.pluginInstance = plugin;
	}
	
	public void load() {
		for(SGChest chest : chests) chest.load();
	}

	@Override
	public SGRawMap convert() {
		return new SGRawMap(FileUtils.toArray(FileUtils.convertList(mineables), SGRawBlockType.class),
				FileUtils.toArray(FileUtils.convertList(placeables), SGRawBlockType.class),
				FileUtils.toArray(FileUtils.convertList(chests), SGRawChest.class),
				FileUtils.toArray(FileUtils.convertList(spawnPoints, RawLocation.converter), RawLocation.class),
				FileUtils.toArray(spawners, SGSpawner.class));
	}
	
	public boolean isMineable(Block block) {
		return mineables.contains(new SGBlockType(block));
	}
	
	public boolean isPlaceable(Block block) {
		return placeables.contains(new SGBlockType(block));
	}
	
}
