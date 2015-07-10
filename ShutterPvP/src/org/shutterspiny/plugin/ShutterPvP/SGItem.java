package org.shutterspiny.plugin.ShutterPvP;

import java.io.File;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.codehaus.jackson.map.ObjectMapper;

public class SGItem {
	
	private static Random random = new Random();
	
	public double rarity;
	public String type;
	public int count, minDamage, maxDamage;
	public String name;
	
	public ItemStack toItemStack() {
		short damage = (short) (random.nextInt(maxDamage - minDamage + 1) + minDamage);
		ItemStack item = new ItemStack(Material.valueOf(type), count, damage);
		ItemMeta meta = item.getItemMeta();
		if(name != null) meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}
	
	public SGItem() {}

	public SGItem(double rarity, String type, int count, int minDamage,
			int maxDamage, String name) {
		this.rarity = rarity;
		this.type = type;
		this.count = count;
		this.minDamage = minDamage;
		this.maxDamage = maxDamage;
		this.name = name;
	}
	
	public SGItem(ItemStack stack, double rarity, int minDamage, int maxDamage) {
		this(rarity, stack.getType().name(), stack.getAmount(), minDamage, maxDamage,
				stack.getItemMeta() == null ? null : stack.getItemMeta().getDisplayName());
	}
	
	//Debug junk
	public static void main(String[] args) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		SGMap map = new SGMap(
				new SGMineable[]{},
				new SGChest[]{new SGChest(new SLocation("overworld", 5, 10, 2), 5.9),
						new SGChest(new SLocation("overworld", 2, 60, 10), 11),
						new SGChest(new SLocation("overworld", 5, -9, 6), 1.2)},
				new SLocation[]{});
		mapper.writerWithDefaultPrettyPrinter().writeValue(new File("Derp"), map);
	}
	
}
