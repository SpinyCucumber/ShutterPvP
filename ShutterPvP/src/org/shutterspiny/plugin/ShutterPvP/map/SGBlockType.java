package org.shutterspiny.plugin.ShutterPvP.map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.shutterspiny.lib.PluginUtils.mapping.Convertable;
import org.shutterspiny.plugin.ShutterPvP.raw.SGRawBlockType;

public class SGBlockType implements Convertable<SGRawBlockType> {
	
	private Material type;
	private byte data;
	
	public SGBlockType(Material type, byte data) {
		this.type = type;
		this.data = data;
	}
	
	@SuppressWarnings("deprecation")
	public SGBlockType(Block block) {
		this(block.getType(), block.getData());
	}
	
	@SuppressWarnings("deprecation")
	public void set(Block block) {
		block.setType(type);
		block.setData(data);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + data;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SGBlockType other = (SGBlockType) obj;
		if (data != other.data)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public SGRawBlockType convert() {
		return new SGRawBlockType(type.name(), (int) data);
	}
	
}
