package org.shutterspiny.plugin.ShutterPvP.item;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.shutterspiny.lib.PluginUtils.mapping.Convertable;
import org.shutterspiny.plugin.ShutterPvP.raw.SGRawEnchantment;

public class SGEnchantment implements Convertable<SGRawEnchantment> {
	
	private Enchantment type;
	private int level;
	private boolean stored;
	
	public SGEnchantment(Enchantment type, int level, boolean stored) {
		this.type = type;
		this.level = level;
		this.stored = stored;
	}

	public SGEnchantment() {}
	
	public void apply(ItemMeta meta) {
		if(stored) ((EnchantmentStorageMeta) meta).addStoredEnchant(type, level, true);
		else meta.addEnchant(type, level, true);
	}

	@Override
	public SGRawEnchantment convert() {
		return new SGRawEnchantment(type.getName(), level, stored);
	}
	
}
