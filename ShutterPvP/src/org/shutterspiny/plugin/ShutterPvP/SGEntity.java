package org.shutterspiny.plugin.ShutterPvP;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class SGEntity {
	
	public String type;
	public SLocation location;
	
	public SGEntity(String type, SLocation location) {
		this.type = type;
		this.location = location;
	}
	
	public SGEntity() {}
	
	public Entity spawn() {
		Location loc = location.toLocation();
		return loc.getWorld().spawnEntity(loc, EntityType.valueOf(type));
	}
	
}
