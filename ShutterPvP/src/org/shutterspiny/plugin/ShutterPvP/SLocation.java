package org.shutterspiny.plugin.ShutterPvP;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SLocation {
	
	public double x, y, z;
	public String world;

	public SLocation(String world, double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}
	
	public SLocation(Location l) {
		this(l.getWorld().getName(), l.getX(), l.getY(), l.getZ());
	}
	
	public SLocation() {}
	
	public Location toLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z);
	}
	
}
