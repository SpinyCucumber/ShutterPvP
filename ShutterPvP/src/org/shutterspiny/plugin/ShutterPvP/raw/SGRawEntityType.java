package org.shutterspiny.plugin.ShutterPvP.raw;

import org.bukkit.entity.EntityType;
import org.shutterspiny.lib.PluginUtils.mapping.Convertable;
import org.shutterspiny.plugin.ShutterPvP.entity.SGEntityType;

public class SGRawEntityType implements Convertable<SGEntityType> {
	
	private String typeName;

	public SGRawEntityType(String typeName) {
		this.typeName = typeName;
	}

	@Override
	public SGEntityType convert() {
		return new SGEntityType(EntityType.valueOf(typeName));
	}
	
}
