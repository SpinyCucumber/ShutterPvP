package org.shutterspiny.plugin.ShutterPvP;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class SGBlock {
	
	private Location location;
	private Material type;
	
	public Block set() {
		Block block = location.getBlock();
		block.setType(type);
		return block;
	}

	public SGBlock(Location location, Material type) {
		this.location = location;
		this.type = type;
	}
	
	public static void main(String[] args) {
		
	}
	
}
