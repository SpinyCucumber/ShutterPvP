package org.shutterspiny.plugin.ShutterPvP;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

//Temporary
@SuppressWarnings("unused")
public class ShutterGames extends JavaPlugin {
	
	public static final ObjectMapper MAPPER = new ObjectMapper();
	
	private Map<String, SGMap> maps;
	private SGItem[] items;
	
	//Hoping to implement api that helps load file structures.
	private void load() throws IOException {
		
		File dataFolder = this.getDataFolder(),
				mapFolder = new File(dataFolder, "maps"), itemFile = new File(dataFolder, "items");
		mapFolder.mkdirs();
		
		for(File file : mapFolder.listFiles())
			maps.put(file.getName(), MAPPER.readValue(file, SGMap.class));
		
		//Get array from json file
		if(!itemFile.exists()) Files.copy(new File("res/items.json").toPath(), itemFile.toPath());
		JsonNode root = MAPPER.readTree(itemFile);
		items = MAPPER.treeToValue(root.path("items"), SGItem[].class);
		
	}
	
	//For convenience
	private void disable() {
		Bukkit.getPluginManager().disablePlugin(this);
	}
	
	@Override
	public void onEnable() {
		try {
			load();
		} catch (IOException e) {
			this.getLogger().log(Level.WARNING, "Failed to load required data files. Disabling plugin.");
			disable();
		}
	}
	
}
