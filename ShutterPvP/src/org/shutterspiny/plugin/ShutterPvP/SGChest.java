package org.shutterspiny.plugin.ShutterPvP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

public class SGChest {
	
	//Will be moved to config.yml eventually
	private final static int MIN_ITEMS = 5, MAX_ITEMS = 22, MAX_SLOT = 27;
	private final static double RARITY_VARIANCE = 0.6;
	
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
		
		int iterations = random.nextInt(MAX_ITEMS - MIN_ITEMS + 1) + MIN_ITEMS;
		List<Integer> slotPick = new ArrayList<Integer>(slots);
		Collections.shuffle(slotPick);
		
		for(int i = 0; i < iterations; i++) {
			int slot = slotPick.get(i);
			double rarityTarget = rarity + RARITY_VARIANCE * random.nextGaussian();
			SGItem item = getClosestItem(items, rarityTarget);
			inventory.setItem(slot, item.toItemStack());
		}
		
	}
	
	private static SGItem getClosestItem(List<SGItem> items, final double rarity) {
		Comparator<SGItem> comparator = new Comparator<SGItem>() {
			public int compare(SGItem arg0, SGItem arg1) {
				double d1 = Math.abs(arg0.rarity - rarity),
						d2 = Math.abs(arg1.rarity - rarity);
				if(d1 < d2) return 1;
				if(d2 > d1) return -1;
				return 0;
			}
		};
		Collections.sort(items, comparator);
		return items.get(0);
	}
	
	public static void main(String[] args) {
		double mean = 0.5, dev = 0.1;
		System.out.println(mean + random.nextGaussian() * dev);
	}
	
}
