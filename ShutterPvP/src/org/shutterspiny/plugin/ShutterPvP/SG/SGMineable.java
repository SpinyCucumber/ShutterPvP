package org.shutterspiny.plugin.ShutterPvP.SG;

import java.io.File;

import org.bukkit.Material;
import org.codehaus.jackson.map.ObjectMapper;

public class SGMineable extends SGBlock {

	private String type;
	
	public String getType() {
		return type;
	}

	public void set() {
		location.toLocation().getBlock().setType(Material.valueOf(type));
	}

	public SGMineable(SLocation location, String type) {
		super(location);
		this.type = type;
	}
	
	//Debug junk
	public static void main(String[] args) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(new File("Derp"), new SGMineable(new SLocation("overworld",10,20,2), "DIRT"));
	}
	
}
