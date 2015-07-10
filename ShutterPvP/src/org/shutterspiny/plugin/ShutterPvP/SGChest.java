package org.shutterspiny.plugin.ShutterPvP;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

public class SGChest extends SGBlock {
	
	//Will be moved to config.yml eventually
	private final static int MIN_ITEMS = 5, MAX_ITEMS = 22;
	private final static double RARITY_RANGE = 1.0;
	
	public static SGPlugin pluginInstance;
	private static Random random = new Random();
	public double rarity;
	
	public SGChest(SLocation location, double rarity) {
		super(location);
		this.rarity = rarity;
	}
	
	public SGChest(Location location, double rarity) {
		this(new SLocation(location), rarity);
	}
	
	public SGChest() {}

	@Override
	public void load() {
		Block block = location.toLocation().getBlock();
		Inventory inventory = ((Chest) block.getState()).getBlockInventory();
		inventory.clear();
		List<SGItem> items = getAppropriateItems();
		int iterations = random.nextInt(MAX_ITEMS - MIN_ITEMS + 1) + MIN_ITEMS;
		for(int i = 0; i < iterations; i++) {
			int slot = random.nextInt(27);
			SGItem item = items.get(random.nextInt(items.size()));
			inventory.setItem(slot, item.toItemStack());
		}
	}
	
	private List<SGItem> getAppropriateItems() {
		List<SGItem> items = new ArrayList<SGItem>();
		for(SGItem item : pluginInstance.getItems()) {
			if(Math.abs(item.rarity - rarity) <= RARITY_RANGE) items.add(item);
		}
		return items;
	}
	
}
