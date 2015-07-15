package org.shutterspiny.plugin.ShutterPvP.raw;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.shutterspiny.lib.PluginUtils.mapping.Convertable;
import org.shutterspiny.lib.PluginUtils.mapping.Converter;

public class RawLocation implements Convertable<Location> {
	
	public static Converter<RawLocation, Location> converter = new Converter<RawLocation, Location>(){
		public RawLocation convert(Location f) {
			return new RawLocation(f.getWorld().getName(), f.getX(), f.getY(), f.getZ());
		}
	};
	
	public double x, y, z;
	public String world;

	public RawLocation(String world, double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}
	
	public RawLocation(Location l) {
		this(l.getWorld().getName(), l.getX(), l.getY(), l.getZ());
	}
	
	public RawLocation() {}

	@Override
	public Location convert() {
		return new Location(Bukkit.getWorld(world), x, y, z);
	}
	
}
