package org.shutterspiny.plugin.ShutterPvP;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SGItem {
	
	private static Random random = new Random();
	
	public double rarity;
	public String type;
	public int minCount, maxCount, minDamage, maxDamage;
	public String name;
	
	public ItemStack toItemStack() {
		short damage = (short) (random.nextInt(maxDamage - minDamage + 1) + minDamage);
		int count = random.nextInt(maxCount - minCount + 1) + minCount;
		ItemStack item = new ItemStack(Material.valueOf(type), count, damage);
		ItemMeta meta = item.getItemMeta();
		if(name != null) meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}
	
	public SGItem() {}

	public SGItem(double rarity, String type, int minCount, int maxCount, int minDamage,
			int maxDamage, String name) {
		this.rarity = rarity;
		this.type = type;
		this.minCount = minCount;
		this.maxCount = maxCount;
		this.minDamage = minDamage;
		this.maxDamage = maxDamage;
		this.name = name;
	}
	
	public SGItem(ItemStack stack, double rarity, int minCount, int maxCount, int minDamage, int maxDamage) {
		this(rarity, stack.getType().name(), minCount, maxCount, minDamage, maxDamage,
				stack.getItemMeta() == null ? null : stack.getItemMeta().getDisplayName());
	}
	
}
