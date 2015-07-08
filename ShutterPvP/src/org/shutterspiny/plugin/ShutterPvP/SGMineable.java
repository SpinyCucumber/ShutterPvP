package org.shutterspiny.plugin.ShutterPvP;

import org.bukkit.Material;

public class SGMineable extends SGBlock {

	private String type;
	
	public String getType() {
		return type;
	}

	public void load() {
		location.toLocation().getBlock().setType(Material.valueOf(type));
	}

	public SGMineable(SLocation location, String type) {
		super(location);
		this.type = type;
	}
	
}
