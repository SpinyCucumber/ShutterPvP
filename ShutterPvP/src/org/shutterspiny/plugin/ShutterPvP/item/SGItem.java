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
import org.shutterspiny.lib.PluginUtils.files.FileUtils;
import org.shutterspiny.lib.PluginUtils.mapping.Convertable;
import org.shutterspiny.plugin.ShutterPvP.raw.SGRawEnchantment;
import org.shutterspiny.plugin.ShutterPvP.raw.SGRawItem;

public class SGItem implements Convertable<SGRawItem> {
	
	private static Random random = new Random();
	
	private double rarity;
	private Material type;
	private int minCount, maxCount, minDamage, maxDamage;
	private String name;
	private List<SGEnchantment> enchants;
	
	public ItemStack toItemStack() {
		short damage = (short) (random.nextInt(maxDamage - minDamage + 1) + minDamage);
		int count = random.nextInt(maxCount - minCount + 1) + minCount;
		ItemStack item = new ItemStack(type, count, damage);
		ItemMeta meta = item.getItemMeta();
		if(name != null) meta.setDisplayName(name);
		for(SGEnchantment enchant : enchants) enchant.apply(meta);
		item.setItemMeta(meta);
		return item;
	}
	
	public SGItem() {}

	public SGItem(double rarity, Material type, int minCount, int maxCount, int minDamage,
			int maxDamage, String name, List<SGEnchantment> enchantments) {
		this.rarity = rarity;
		this.type = type;
		this.minCount = minCount;
		this.maxCount = maxCount;
		this.minDamage = minDamage;
		this.maxDamage = maxDamage;
		this.name = name;
		this.enchants = enchantments;
	}
	
	public static SGItem fromItemStack(ItemStack stack, double rarity, int minCount, int maxCount,
			int minDamage, int maxDamage) {
		
		ItemMeta meta = stack.getItemMeta();
		List<SGEnchantment> enchants= new ArrayList<SGEnchantment>();
		
		for(Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet())
			enchants.add(new SGEnchantment(entry.getKey(), entry.getValue(), false));
		if(meta instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta enchMeta = (EnchantmentStorageMeta) meta;
			for(Entry<Enchantment, Integer> entry : enchMeta.getStoredEnchants().entrySet())
				enchants.add(new SGEnchantment(entry.getKey(), entry.getValue(), true));
		}

		String name = meta.getDisplayName();
		
		return new SGItem(rarity, stack.getType(), minCount, maxCount, minDamage, maxDamage, name, enchants);
		
	}
	
	public double getRarity() {
		return rarity;
	}

	@Override
	public String toString() {
		return type.name();
	}
	
	@Override
	public SGRawItem convert() {
		return new SGRawItem(rarity, type.name(), minCount, maxCount,
				minDamage, maxDamage, name,
				FileUtils.toArray(FileUtils.convertList(enchants), SGRawEnchantment.class));
	}
	
	public static SGItem fromItemStackDamaged(ItemStack stack, double rarity, int minCount, int maxCount) {
		int damage = (int) stack.getDurability();
		return fromItemStack(stack, rarity, minCount, maxCount, damage, damage);
	}
	
}
