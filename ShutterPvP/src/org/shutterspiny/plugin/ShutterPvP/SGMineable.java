package org.shutterspiny.plugin.ShutterPvP;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class SGMineable extends SGBlock {

	public String type;

	public void load() {
		location.toLocation().getBlock().setType(Material.valueOf(type));
	}

	public SGMineable(SLocation location, String type) {
		super(location);
		this.type = type;
	}
	
	public SGMineable() {}
	
	public SGMineable(Block block) {
		this(new SLocation(block.getLocation()), block.getType().name());
	}
	
}
