package org.shutterspiny.plugin.ShutterPvP;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

public class SGEnchantment {
	
	public String type;
	public int level;
	
	public SGEnchantment(String type, int level) {
		this.type = type;
		this.level = level;
	}

	public SGEnchantment() {}
	
	public void apply(ItemMeta meta) {
		meta.addEnchant(Enchantment.getByName(type), level, true);
	}
	
}
