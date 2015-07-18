package org.shutterspiny.plugin.ShutterPvP.raw;

import org.shutterspiny.lib.PluginUtils.mapping.Convertable;
import org.shutterspiny.plugin.ShutterPvP.entity.SGSpawner;

public class SGRawSpawner implements Convertable<SGSpawner> {
	
	public String typeName;
	public int delay, maxEntities, repeats;
	public RawLocation location;
	
	public SGRawSpawner(String typeName, int delay, int maxEntities,
			int repeats, RawLocation location) {
		this.typeName = typeName;
		this.delay = delay;
		this.maxEntities = maxEntities;
		this.repeats = repeats;
		this.location = location;
	}
	
	public SGRawSpawner() {}

	@Override
	public SGSpawner convert() {
		return new SGSpawner(typeName,
				delay, maxEntities, repeats, location.convert()); 
	}
	
}
