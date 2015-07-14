package org.shutterspiny.plugin.ShutterPvP.raw;

import org.shutterspiny.lib.PluginUtils.mapping.Convertable;
import org.shutterspiny.plugin.ShutterPvP.item.SGChest;

public class SGRawChest implements Convertable<SGChest> {
	
	public SLocation location;
	public double rarity;
	
	public SGRawChest(SLocation location, double rarity) {
		this.location = location;
		this.rarity = rarity;
	}
	
	public SGRawChest() {}

	@Override
	public SGChest convert() {
		return new SGChest(location.convert(), rarity);
	}
	
}
