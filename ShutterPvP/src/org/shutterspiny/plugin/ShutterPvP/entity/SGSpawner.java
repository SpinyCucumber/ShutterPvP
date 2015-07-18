package org.shutterspiny.plugin.ShutterPvP.entity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.shutterspiny.lib.PluginUtils.mapping.Convertable;
import org.shutterspiny.plugin.ShutterPvP.SGPlugin;
import org.shutterspiny.plugin.ShutterPvP.raw.RawLocation;
import org.shutterspiny.plugin.ShutterPvP.raw.SGRawSpawner;

public class SGSpawner implements Convertable<SGRawSpawner> {
	
	public class SpawnerTimer {
		
		private int repeat, time;
		public List<Entity> spawned = new ArrayList<Entity>();
		
		public void update() {
			System.out.println(spawned.size());
			if(spawned.size() == maxEntities || repeat == repeats) return;
			time++;
			System.out.println("Hi");
			if(time == delay) {
				time = 0;
				repeat++;
				System.out.println("Hi!");
				spawned.add(pluginInstance.getEntities().get(type).spawn(location));
			}
		}
		
		public void removeEntities() {
			for(Entity entity : spawned) entity.remove();
		}
		
	}
	
	private String type;
	public SGPlugin pluginInstance;
	private int delay, maxEntities, repeats;
	private Location location;
	
	public SGSpawner(String type, int delay, int maxEntities,
			int repeats, Location location) {
		this.type = type;
		this.delay = delay;
		this.maxEntities = maxEntities;
		this.repeats = repeats;
		this.location = location;
	}

	@Override
	public String toString() {
		return "SGSpawner [type=" + type + ", delay=" + delay
				+ ", maxEntities=" + maxEntities + ", repeats=" + repeats
				+ ", location=" + location + "]";
	}

	@Override
	public SGRawSpawner convert() {
		return new SGRawSpawner(type, delay, maxEntities, repeats, RawLocation.converter.convert(location));
	}
	
}
