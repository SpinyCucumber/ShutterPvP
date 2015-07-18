package org.shutterspiny.plugin.ShutterPvP.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.shutterspiny.lib.PluginUtils.mapping.Convertable;
import org.shutterspiny.plugin.ShutterPvP.raw.SGRawEntityType;

public class SGEntityType implements Convertable<SGRawEntityType> {
	
	private EntityType type;

	public SGEntityType(EntityType type) {
		this.type = type;
	}
	
	public Entity spawn(Location loc) {
		return loc.getWorld().spawnEntity(loc, type);
	}

	@Override
	public SGRawEntityType convert() {
		return new SGRawEntityType(type.name());
	}

	@Override
	public String toString() {
		return "SGEntityType [type=" + type + "]";
	}
	
}
