package org.shutterspiny.plugin.ShutterPvP.raw;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.shutterspiny.lib.PluginUtils.mapping.Convertable;
import org.shutterspiny.lib.PluginUtils.mapping.Converter;

public class SLocation implements Convertable<Location> {
	
	public static Converter<SLocation, Location> converter = new Converter<SLocation, Location>(){
		public SLocation convert(Location f) {
			return new SLocation(f.getWorld().getName(), f.getX(), f.getY(), f.getZ());
		}
	};
	
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

	@Override
	public Location convert() {
		return new Location(Bukkit.getWorld(world), x, y, z);
	}
	
}
