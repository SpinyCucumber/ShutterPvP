package org.shutterspiny.plugin.ShutterPvP;

import org.bukkit.Location;

public class SGChest extends SGBlock {
	
	public static SGPlugin plugin;
	public double rarity;
	
	public SGChest(SLocation location, double rarity) {
		super(location);
		this.rarity = rarity;
	}
	
	public SGChest(Location location, double rarity) {
		this(new SLocation(location), rarity);
	}
	
	public SGChest() {}

	@Override
	public void load() {
		
	}
	
}
