package org.shutterspiny.plugin.ShutterPvP.entity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class SGSpawner {
	
	public class SpawnerTimer {
		
		private int repeat, time;
		private List<Entity> spawned = new ArrayList<Entity>();
		
		public void update() {
			if(spawned.size() >= maxEntities || repeat >= repeats) return;
			time++;
			if(time == delay) {
				time = 0;
				repeats++;
				spawned.add(type.spawn(location));
			}
		}
		
		public void removeEntities() {
			for(Entity entity : spawned) if(entity.isValid()) entity.remove();
		}
		
	}
	
	private SGEntityType type;
	private int delay, maxEntities, repeats;
	private Location location;
	
	public SGSpawner(SGEntityType type, int delay, int maxEntities,
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
	
}
