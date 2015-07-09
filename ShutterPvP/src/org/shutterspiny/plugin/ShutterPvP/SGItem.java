package org.shutterspiny.plugin.ShutterPvP;

import java.io.File;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.codehaus.jackson.map.ObjectMapper;

public class SGItem {
	
	private static Random random = new Random();
	
	private double rarity;
	private Material type;
	private int count, minDamage, maxDamage;
	private String name;
	
	public int getMinDamage() {
		return minDamage;
	}

	public int getMaxDamage() {
		return maxDamage;
	}

	public double getRarity() {
		return rarity;
	}
	
	public Material getType() {
		return type;
	}
	
	public int getCount() {
		return count;
	}
	
	public String getName() {
		return name;
	}
	
	public ItemStack toItemStack() {
		short damage = (short) (random.nextInt(maxDamage - minDamage) + minDamage);
		ItemStack item = new ItemStack(type, count, damage);
		ItemMeta meta = item.getItemMeta();
		if(name != null) meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}

	public SGItem(double rarity, Material type, int count, int minDamage,
			int maxDamage, String name) {
		this.rarity = rarity;
		this.type = type;
		this.count = count;
		this.minDamage = minDamage;
		this.maxDamage = maxDamage;
		this.name = name;
	}
	
	//Debug junk
	public static void main(String[] args) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		SGMap map = new SGMap(
				new SGMineable[]{},
				new SGChest[]{new SGChest(new SLocation("overworld", 5, 10, 2), 5.9)},
				new SLocation[]{});
		mapper.writerWithDefaultPrettyPrinter().writeValue(new File("Derp"), map);
	}
	
}
