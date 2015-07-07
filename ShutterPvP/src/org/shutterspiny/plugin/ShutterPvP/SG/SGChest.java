package org.shutterspiny.plugin.ShutterPvP.SG;

import org.bukkit.Location;

public class SGChest extends SGBlock {

	@SuppressWarnings("unused")
	private double rarity;

	public SGChest(Location location, double rarity) {
		super(location);
		this.rarity = rarity;
	}

	@Override
	public void set() {
		
	}
	
}
