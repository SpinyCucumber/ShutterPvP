package org.shutterspiny.plugin.ShutterPvP.SG;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SLocation {
	
	private double x, y, z;
	private String world;
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public String getWorld() {
		return world;
	}

	public SLocation(String world, double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}
	
	public Location toLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z);
	}
	
}
