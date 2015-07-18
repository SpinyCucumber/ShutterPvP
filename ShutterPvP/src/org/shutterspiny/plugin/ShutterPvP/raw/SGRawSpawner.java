package org.shutterspiny.plugin.ShutterPvP.raw;

import org.shutterspiny.lib.PluginUtils.mapping.Convertable;
import org.shutterspiny.plugin.ShutterPvP.SGPlugin;
import org.shutterspiny.plugin.ShutterPvP.entity.SGSpawner;

public class SGRawSpawner implements Convertable<SGSpawner> {
	
	public static SGPlugin pluginInstance;
	
	private String typeName;
	private int delay, maxEntities, repeats;
	private RawLocation location;
	
	public SGRawSpawner(String typeName, int delay, int maxEntities,
			int repeats, RawLocation location) {
		this.typeName = typeName;
		this.delay = delay;
		this.maxEntities = maxEntities;
		this.repeats = repeats;
		this.location = location;
	}

	@Override
	public SGSpawner convert() {
		return new SGSpawner(pluginInstance.getEntities().get(typeName),
				delay, maxEntities, repeats, location.convert()); 
	}
	
}
