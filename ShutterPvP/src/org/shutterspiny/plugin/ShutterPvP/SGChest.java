package org.shutterspiny.plugin.ShutterPvP;

public class SGChest extends SGBlock {

	private double rarity;

	public double getRarity() {
		return rarity;
	}

	public SGChest(SLocation location, double rarity) {
		super(location);
		this.rarity = rarity;
	}

	@Override
	public void load() {
		
	}
	
}
