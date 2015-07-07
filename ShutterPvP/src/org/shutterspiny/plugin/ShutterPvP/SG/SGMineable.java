package org.shutterspiny.plugin.ShutterPvP.SG;

import org.bukkit.Location;
import org.bukkit.Material;

public class SGMineable extends SGBlock {

	private Material type;
	
	public void set() {
		location.getBlock().setType(type);
	}

	public SGMineable(Location location, Material type) {
		super(location);
		this.type = type;
	}
	
	public static void main(String[] args) {
		
	}
	
}
