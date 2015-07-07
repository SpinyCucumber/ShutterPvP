package org.shutterspiny.plugin.ShutterPvP.SG;

public abstract class SGBlock {
	
	public SLocation location;
	
	public SLocation getLocation() {
		return location;
	}

	public abstract void set();
	
	public SGBlock(SLocation location) {
		this.location = location;
	}
	
}
