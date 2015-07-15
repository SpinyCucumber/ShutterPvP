package org.shutterspiny.plugin.ShutterPvP.raw;

import org.bukkit.enchantments.Enchantment;
import org.shutterspiny.lib.PluginUtils.mapping.Convertable;
import org.shutterspiny.plugin.ShutterPvP.item.SGEnchantment;

public class SGRawEnchantment implements Convertable<SGEnchantment> {
	
	public String type;
	public int level;
	public boolean stored;
	
	public SGRawEnchantment(String type, int level, boolean stored) {
		this.type = type;
		this.level = level;
		this.stored = stored;
	}
	
	public SGRawEnchantment() {}

	@Override
	public SGEnchantment convert() {
		return new SGEnchantment(Enchantment.getByName(type), level, stored);
	}
	
}
