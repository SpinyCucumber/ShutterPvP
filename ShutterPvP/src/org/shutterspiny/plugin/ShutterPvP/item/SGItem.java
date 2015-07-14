package org.shutterspiny.plugin.ShutterPvP.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class SGItem {
	
	private static Random random = new Random();
	
	public double rarity;
	public String type;
	public int minCount, maxCount, minDamage, maxDamage;
	public String name;
	public SGEnchantment[] enchantments;
	
	public ItemStack toItemStack() {
		short damage = (short) (random.nextInt(maxDamage - minDamage + 1) + minDamage);
		int count = random.nextInt(maxCount - minCount + 1) + minCount;
		ItemStack item = new ItemStack(Material.valueOf(type), count, damage);
		ItemMeta meta = item.getItemMeta();
		if(name != null) meta.setDisplayName(name);
		for(SGEnchantment enchant : enchantments) enchant.apply(meta);
		item.setItemMeta(meta);
		return item;
	}
	
	public SGItem() {}

	public SGItem(double rarity, String type, int minCount, int maxCount, int minDamage,
			int maxDamage, String name, SGEnchantment[] enchantments) {
		this.rarity = rarity;
		this.type = type;
		this.minCount = minCount;
		this.maxCount = maxCount;
		this.minDamage = minDamage;
		this.maxDamage = maxDamage;
		this.name = name;
		this.enchantments = enchantments;
	}
	
	public static SGItem fromItemStack(ItemStack stack, double rarity, int minCount, int maxCount,
			int minDamage, int maxDamage) {
		
		ItemMeta meta = stack.getItemMeta();
		List<SGEnchantment> enchantList = new ArrayList<SGEnchantment>();
		
		for(Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet())
			enchantList.add(new SGEnchantment(entry.getKey().getName(), entry.getValue(), false));
		if(meta instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta enchMeta = (EnchantmentStorageMeta) meta;
			for(Entry<Enchantment, Integer> entry : enchMeta.getStoredEnchants().entrySet())
				enchantList.add(new SGEnchantment(entry.getKey().getName(), entry.getValue(), true));
		}
		
		SGEnchantment[] enchants = enchantList.toArray(new SGEnchantment[enchantList.size()]);
		String name = meta.getDisplayName();
		String type = stack.getType().name();
		
		return new SGItem(rarity, type, minCount, maxCount, minDamage, maxDamage, name, enchants);
		
	}
	
	public static SGItem fromItemStackDamaged(ItemStack stack, double rarity, int minCount, int maxCount) {
		int damage = (int) stack.getDurability();
		return fromItemStack(stack, rarity, minCount, maxCount, damage, damage);
	}
	
}
