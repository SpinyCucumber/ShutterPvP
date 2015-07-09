package org.shutterspiny.plugin.ShutterPvP;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.shutterspiny.lib.PluginUtils.FileNode;
import org.shutterspiny.lib.PluginUtils.JSONObjectFile;
import org.shutterspiny.lib.PluginUtils.MapBuilder;
import org.shutterspiny.lib.PluginUtils.MapNode;
import org.shutterspiny.lib.PluginUtils.ParentNode;

//Temporary
public class ShutterGames extends JavaPlugin {
	
	private Map<String, SGMap> maps;
	private SGItem[] items;
	
	private static void loadAPI(JarFile jar, String path, File dir) throws IOException {
		File f = new File(dir, path);
		if(!f.exists()) JarUtils.extractResource(jar, path, f);
		JarUtils.addClassPath(f);
	}
	
	//Implementing WIP "PluginUtils" API, which includes a tree-like file loading system
	@SuppressWarnings("unchecked")		
	private void load() {
		
		Map<String, Object> files;
		File folder = this.getDataFolder(), libs = new File(folder, "lib");
		FileNode<Map<String, Object>> parentNode = null;
		
		try {
			
			libs.mkdirs();
			JarFile jar = JarUtils.getRunningJar();
			loadAPI(jar, "jackson-core-asl-1.9.13.jar", libs);
			loadAPI(jar, "jackson-mapper-asl-1.9.13.jar", libs);
			
			parentNode = new ParentNode(new MapBuilder<String,FileNode<?>>()
					.with("maps", new MapNode<SGMap>(new JSONObjectFile<SGMap>(SGMap.class)))
					.with("items.json", new JSONObjectFile<SGItem[]>(SGItem[].class))
					.end());
			
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
				files.put("items.json", items);
				
				parentNode.save(folder, files);
			
			}
				
		} catch(Exception e) {
			
			log(Level.SEVERE, "Fatal error! I hate it when this happens.");
			disable();
			e.printStackTrace();
			
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
