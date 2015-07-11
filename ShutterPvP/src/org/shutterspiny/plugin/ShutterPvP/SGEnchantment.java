package org.shutterspiny.plugin.ShutterPvP;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class SGEnchantment {
	
	public String type;
	public int level;
	public boolean stored;
	
	public SGEnchantment(String type, int level, boolean stored) {
		this.type = type;
		this.level = level;
		this.stored = stored;
	}

	public SGEnchantment() {}
	
	public void apply(ItemMeta meta) {
		Enchantment enchant = Enchantment.getByName(type);
		if(stored) ((EnchantmentStorageMeta) meta).addStoredEnchant(enchant, level, true);
		else meta.addEnchant(Enchantment.getByName(type), level, true);
	}
	
}
