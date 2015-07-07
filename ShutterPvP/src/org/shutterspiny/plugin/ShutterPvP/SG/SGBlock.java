package org.shutterspiny.plugin.ShutterPvP.SG;

import org.bukkit.Location;

public abstract class SGBlock {
	
	protected Location location;
	
	public abstract void set();
	
	public SGBlock(Location location) {
		this.location = location;
	}
	
}
