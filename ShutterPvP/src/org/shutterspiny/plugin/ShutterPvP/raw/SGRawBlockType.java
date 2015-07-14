package org.shutterspiny.plugin.ShutterPvP.raw;

import org.bukkit.Material;
import org.shutterspiny.lib.PluginUtils.mapping.Convertable;
import org.shutterspiny.plugin.ShutterPvP.map.SGBlockType;

public class SGRawBlockType implements Convertable<SGBlockType> {
	
	public String type;
	public int damage;
	
	public SGRawBlockType(String type, int damage) {
		this.type = type;
		this.damage = damage;
	}
	
	public SGRawBlockType() {}

	@Override
	public SGBlockType convert() {
		return new SGBlockType(Material.valueOf(type), (byte) damage);
	}
	
}
