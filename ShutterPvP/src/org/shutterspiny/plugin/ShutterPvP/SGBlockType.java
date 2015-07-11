package org.shutterspiny.plugin.ShutterPvP;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class SGBlockType {
	
	public String type;
	public int damage;
	
	public SGBlockType(String type, int damage) {
		this.type = type;
		this.damage = damage;
	}
	
	public SGBlockType() {}
	
	@SuppressWarnings("deprecation")
	public SGBlockType(Block block) {
		this.type = block.getType().name();
		this.damage = (int) block.getData();
	}
	
	@SuppressWarnings("deprecation")
	public void set(Block block) {
		block.setType(Material.valueOf(type));
		block.setData((byte) damage);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + damage;
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
		if (damage != other.damage && !(damage == -1) && !(other.damage == -1))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
}
