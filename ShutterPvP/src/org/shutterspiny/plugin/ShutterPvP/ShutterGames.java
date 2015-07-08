package org.shutterspiny.plugin.ShutterPvP;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.shutterspiny.lib.PluginUtils.FileNode;
import org.shutterspiny.lib.PluginUtils.JSONObjectFile;
import org.shutterspiny.lib.PluginUtils.MapBuilder;
import org.shutterspiny.lib.PluginUtils.MapNode;
import org.shutterspiny.lib.PluginUtils.ParentNode;

//Temporary
@SuppressWarnings("unused")
public class ShutterGames extends JavaPlugin {
	
	//File node that represents the plugins data folder
	private static FileNode<Map<String, Object>> parentNode;
	
	static {
		
		parentNode = new ParentNode(new MapBuilder<String,FileNode<?>>()
				.with("maps", new MapNode<SGMap>(new JSONObjectFile<SGMap>(SGMap.class)))
				.end());
		
	}
	
	private Map<String, SGMap> maps;
	private SGItem[] items;
	
	//Implementing WIP "PluginUtils" API, which includes a tree-like file loading system
	@SuppressWarnings("unchecked")
	private void load() {
		
		Map<String, Object> files;
		File folder = this.getDataFolder();
		
		try {
			
			files = parentNode.load(folder);
			maps = (Map<String, SGMap>) files.get("maps");
			items = (SGItem[]) files.get("items.json");
			
		} catch(IOException e) {
			
			log(Level.WARNING, "Data files either missing or corrupted. Saving default data.");
			maps = new HashMap<String, SGMap>();
			items = new SGItem[0];
			files = new HashMap<String, Object>();
			files.put("maps", maps);
			files.put("items", items);
			
			try {
				parentNode.save(folder, files);
			} catch (IOException e1) {
				log(Level.SEVERE, "Failed to save default data! Disabling plugin.");
				e1.printStackTrace();
				disable();
			}
			
		}
		
	}
	
	//For convenience
	private void disable() {
		Bukkit.getPluginManager().disablePlugin(this);
	}
	
	private void log(Level level, String msg) {
		this.getLogger().log(level, msg);
	}
	
	@Override
	public void onEnable() {
		load();
	}
	
}
