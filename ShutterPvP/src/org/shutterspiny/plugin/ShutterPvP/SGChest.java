package org.shutterspiny.plugin.ShutterPvP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;

public class SGChest {

	private final static int MAX_SLOT = 27;
	
	//Sadly, with JSON, there is no way to pass down an instance of SGPlugin to each chest.
	public static SGPlugin pluginInstance;
	private static Random random;
	private static List<Integer> slots;
	
	//Initialize static fields
	static {
		random = new Random();
		slots = new ArrayList<Integer>();
		for(int i = 0; i < MAX_SLOT; i++) slots.add(i);
	}
	
	public double rarity;
	public SLocation location;
	
	public SGChest(SLocation location, double rarity) {
		this.location = location;
		this.rarity = rarity;
	}
	
	public SGChest(Location location, double rarity) {
		this(new SLocation(location), rarity);
	}
	
	public SGChest() {}

	public void load() {
		
		Block block = location.toLocation().getBlock();
		Inventory inventory = ((Chest) block.getState()).getBlockInventory();
		inventory.clear();
		
		List<SGItem> items = Arrays.asList(pluginInstance.getItems());
		if(items.size() == 0) return;
		
		FileConfiguration config = pluginInstance.getConfig();
		int minItems = config.getInt("MinItems"), maxItems = config.getInt("MaxItems");
		double rarityVar = config.getDouble("RarityVariance");
		int iterations = random.nextInt(maxItems - minItems + 1) + minItems;
		List<Integer> slotPick = new ArrayList<Integer>(slots);
		Collections.shuffle(slotPick);
		
		for(int i = 0; i < iterations; i++) {
			int slot = slotPick.get(i);
			double rarityTarget = rarity + rarityVar * random.nextGaussian();
			SGItem item = getClosestItem(items, rarityTarget);
			inventory.setItem(slot, item.toItemStack());
		}
		
	}
	
	private static SGItem getClosestItem(List<SGItem> items, final double rarity) {
		List<SGItem> closest = new ArrayList<SGItem>();
		double dist = 0;
		for(SGItem item : items) {
			double d = Math.abs(item.rarity - rarity);
			if(closest.isEmpty() || d < dist) {
				closest.clear();
				closest.add(item);
				dist = d;
			} else if(d == dist) closest.add(item);
		}
		SGItem item = closest.get(closest.size() == 1 ? 0 : random.nextInt(closest.size()));
		return item;
	}
	
	public static void main(String[] args) {
		double mean = 0.5, dev = 0.1;
		System.out.println(mean + random.nextGaussian() * dev);
	}
	
}
