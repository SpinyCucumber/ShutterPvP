package org.shutterspiny.plugin.ShutterPvP;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.shutterspiny.lib.PluginUtils.command.AbstractCommand;
import org.shutterspiny.lib.PluginUtils.files.ConvertableNode;
import org.shutterspiny.lib.PluginUtils.files.FileNode;
import org.shutterspiny.lib.PluginUtils.files.Files;
import org.shutterspiny.lib.PluginUtils.files.JSONObjectFile;
import org.shutterspiny.lib.PluginUtils.files.MapNode;
import org.shutterspiny.lib.PluginUtils.files.ParentNode;
import org.shutterspiny.lib.PluginUtils.mapping.AdvancedMap;
import org.shutterspiny.lib.PluginUtils.mapping.Factory;
import org.shutterspiny.lib.PluginUtils.mapping.MapBuilder;
import org.shutterspiny.plugin.ShutterPvP.entity.SGEntityType;
import org.shutterspiny.plugin.ShutterPvP.item.SGItem;
import org.shutterspiny.plugin.ShutterPvP.map.SGMap;
import org.shutterspiny.plugin.ShutterPvP.raw.SGRawEntityType;
import org.shutterspiny.plugin.ShutterPvP.raw.SGRawItem;
import org.shutterspiny.plugin.ShutterPvP.raw.SGRawMap;

//Temporary
public class SGPlugin extends JavaPlugin {

	public static final String TAG = ChatColor.GOLD + "[" + ChatColor.YELLOW
			+ "" + ChatColor.BOLD + "SG" + ChatColor.GOLD + "] " + ChatColor.RESET;
	
	public static class SGPlayerData {
		
		public String selectedMap;
		
	}
	
	private Map<String, SGMap> maps;
	private Map<String, SGEntityType> entities;
	private Map<Player, SGPlayerData> playerData;
	private List<SGItem> items;
	private ParentNode node;
	private SGGame game;

	public Map<String, SGMap> getMaps() {
		return maps;
	}

	public Map<String, SGEntityType> getEntities() {
		return entities;
	}

	public SGPlayerData getPlayerData(Player player) {
		return playerData.get(player);
	}

	public List<SGItem> getItems() {
		return items;
	}

	public SGGame getGame() {
		return game;
	}

	//Implementing WIP "PluginUtils" API, which includes a tree-like file loading system
	@SuppressWarnings("unchecked")		
	public void load() throws Exception {
		
		//Use plugin utils to load files
		node = new ParentNode(new MapBuilder<String,FileNode<?>>()
				.with("maps", new MapNode<SGMap>(new ConvertableNode<SGMap, SGRawMap>(new JSONObjectFile<SGRawMap>(SGRawMap.class)), new Factory<Map<String,SGMap>>(){ public Map<String, SGMap> create() { return new HashMap<String, SGMap>();}}, ".json"))
				.with("entities", new MapNode<SGEntityType>(new ConvertableNode<SGEntityType, SGRawEntityType>(new JSONObjectFile<SGRawEntityType>(SGRawEntityType.class)), new Factory<Map<String,SGEntityType>>(){ public Map<String, SGEntityType> create() { return new HashMap<String, SGEntityType>();}}, ".json"))
				.with("items.json", new JSONObjectFile<SGRawItem[]>(SGRawItem[].class))
				.end());
		
		try {
			
			log(Level.INFO, "Loading data...");
			this.saveDefaultConfig();
			this.reloadConfig();
			Map<String, Object> files = node.load(this.getDataFolder());
			maps = (Map<String, SGMap>) files.get("maps");
			entities = (Map<String, SGEntityType>) files.get("entities");
			items = Files.convertList(Arrays.asList((SGRawItem[]) files.get("items.json")));
			log(Level.INFO, "Successfully loaded data.");
			
		} catch(IOException e) {
			
			log(Level.WARNING, "Data files either missing or corrupted. Using default data.");
			maps = new HashMap<String, SGMap>();
			entities = new HashMap<String, SGEntityType>();
			items = new ArrayList<SGItem>();
			save();
		
		}
		
	}
	
	private void loadAPIs() throws IOException {
		
		final File libs = new File(this.getDataFolder(), "lib");
		final JarFile jar = JarUtils.getRunningJar();
		libs.mkdirs();
		
		//local classes help with code repetition
		abstract class APILoader {
			abstract void loadAPI(String path) throws IOException;
		}
		APILoader loader = new APILoader() {
			void loadAPI(String path) throws IOException {
				File f = new File(libs, path);
				if(!f.exists()) {
					JarUtils.extractResource(jar, path, f);
					log(Level.INFO, "Successfully extracted " + path + " from jar.");
				}
				JarUtils.addClassPath(f);
				log(Level.INFO, "Successfully loaded " + path + " from lib folder.");
			}
		};
		
		log(Level.INFO, "Loading apis...");
		loader.loadAPI("jackson-core-asl-1.9.13.jar");
		loader.loadAPI("jackson-mapper-asl-1.9.13.jar");
		
	}
	
	private void registerCommands() {
		for(AbstractCommand<SGPlugin, ?> command : Commands.sgCommands) {
			log(Level.INFO, "Registering command " + command.getName() + "...");
			command.register(this);
		}
	}
	
	public void save() throws IOException {
		
		Map<String, Object> files = new HashMap<String, Object>();
		files.put("maps", maps);
		files.put("entities", entities);
		files.put("items.json", Files.toArray(Files.convertList(items), SGRawItem.class));
		
		log(Level.INFO, "Saving data...");
		node.save(getDataFolder(), files);
		
		log(Level.INFO, "Successfully saved data.");
		
	}
	
	@Override
	public void onEnable() {
		try {
			loadAPIs();
			load();
			registerCommands();
		} catch (Exception e) {
			log(Level.SEVERE, "Error loading! Default data could not be used!");
			e.printStackTrace();
		}
		playerData = new AdvancedMap<Player, SGPlayerData>(new Factory<SGPlayerData>(){
			public SGPlayerData create() {
				return new SGPlayerData();
			}
		});
		for(SGMap map : maps.values()) map.setPlugin(this);
		game = new SGGame(this);
	}
	
	@Override
	public void onDisable() {
		try {
			save();
		} catch (IOException e) {
			log(Level.SEVERE, "Error loading. Data may have been lost!");
			e.printStackTrace();
		}
	}
	
	public void log(Level level, String msg) {
		this.getLogger().log(level, msg);
	}
	
}
