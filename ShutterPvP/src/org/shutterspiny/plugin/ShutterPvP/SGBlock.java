package org.shutterspiny.plugin.ShutterPvP;

public abstract class SGBlock {
	
	public SLocation location;
	
	public SLocation getLocation() {
		return location;
	}

	public abstract void load();
	
	public SGBlock(SLocation location) {
		this.location = location;
	}
	
}
