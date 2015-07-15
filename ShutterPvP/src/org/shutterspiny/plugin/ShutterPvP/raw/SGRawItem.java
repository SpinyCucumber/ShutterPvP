package org.shutterspiny.plugin.ShutterPvP.raw;

import java.util.Arrays;

import org.bukkit.Material;
import org.shutterspiny.lib.PluginUtils.files.FileUtils;
import org.shutterspiny.lib.PluginUtils.mapping.Convertable;
import org.shutterspiny.plugin.ShutterPvP.item.SGItem;

public class SGRawItem implements Convertable<SGItem> {
	
	public double rarity;
	public String type;
	public int minCount, maxCount, minDamage, maxDamage;
	public String name;
	public SGRawEnchantment[] enchants;
	
	public SGRawItem(double rarity, String type, int minCount, int maxCount,
			int minDamage, int maxDamage, String name,
			SGRawEnchantment[] enchants) {
		this.rarity = rarity;
		this.type = type;
		this.minCount = minCount;
		this.maxCount = maxCount;
		this.minDamage = minDamage;
		this.maxDamage = maxDamage;
		this.name = name;
		this.enchants = enchants;
	}
	
	public SGRawItem() {}

	@Override
	public SGItem convert() {
		return new SGItem(rarity, Material.valueOf(type),
				minCount, maxCount, minDamage, maxDamage, name,
				FileUtils.convertList(Arrays.asList(enchants)));
	}
	
}
