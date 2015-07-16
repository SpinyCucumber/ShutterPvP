package org.shutterspiny.plugin.ShutterPvP.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.shutterspiny.lib.PluginUtils.mapping.Convertable;
import org.shutterspiny.plugin.ShutterPvP.SGPlugin;
import org.shutterspiny.plugin.ShutterPvP.raw.SGRawChest;
import org.shutterspiny.plugin.ShutterPvP.raw.RawLocation;

public class SGChest implements Convertable<SGRawChest> {

	private final static int MAX_SLOT = 27;

	private static Random random;
	private static List<Integer> slots;
	
	//Initialize static fields
	static {
		random = new Random();
		slots = new ArrayList<Integer>();
		for(int i = 0; i < MAX_SLOT; i++) slots.add(i);
	}
	
	private double rarity;
	private Location location;
	public SGPlugin pluginInstance;
	
	public SGChest(Location location, double rarity) {
		this.location = location;
		this.rarity = rarity;
	}

	public void load() {
		
		Block block = location.getBlock();
		Inventory inventory = ((Chest) block.getState()).getBlockInventory();
		inventory.clear();
		
		List<SGItem> items = pluginInstance.getItems();
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
	
	@Override
	public SGRawChest convert() {
		return new SGRawChest(RawLocation.converter.convert(location), rarity);
	}
	
	@Override
	public String toString() {
		return "SGChest [rarity=" + rarity + ", location=" + location + "]";
	}

	private static SGItem getClosestItem(List<SGItem> items, final double rarity) {
		List<SGItem> closest = new ArrayList<SGItem>();
		double dist = 0;
		for(SGItem item : items) {
			double d = Math.abs(item.getRarity() - rarity);
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
