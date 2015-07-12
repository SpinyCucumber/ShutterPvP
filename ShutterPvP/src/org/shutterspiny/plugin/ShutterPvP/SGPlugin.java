package org.shutterspiny.plugin.ShutterPvP;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.shutterspiny.lib.PluginUtils.AdvancedMap;
import org.shutterspiny.lib.PluginUtils.Factory;
import org.shutterspiny.lib.PluginUtils.FileNode;
import org.shutterspiny.lib.PluginUtils.JSONObjectFile;
import org.shutterspiny.lib.PluginUtils.MapBuilder;
import org.shutterspiny.lib.PluginUtils.MapNode;
import org.shutterspiny.lib.PluginUtils.ParentNode;
import org.shutterspiny.plugin.ShutterPvP.SGGame.SGGameException;

//Temporary
public class SGPlugin extends JavaPlugin {
	
	private static final int TARGET_RANGE = 100;
	public static final String TAG = ChatColor.GOLD + "[" + ChatColor.YELLOW
			+ "" + ChatColor.BOLD + "SG" + ChatColor.GOLD + "] " + ChatColor.RESET;
	
	private Map<String, SGMap> maps;
	private Map<UUID, SGPlayerData> playerData;
	private SGItem[] items;
	private ParentNode node;
	private SGGame game;
	
	public Map<String, SGMap> getMaps() {
		return maps;
	}

	public Map<UUID, SGPlayerData> getPlayerData() {
		return playerData;
	}

	public SGItem[] getItems() {
		return items;
	}

	public SGGame getGame() {
		return game;
	}

	//Implementing WIP "PluginUtils" API, which includes a tree-like file loading system
	@SuppressWarnings("unchecked")		
	private void load() throws Exception {
		
		//Use plugin utils to load files
		node = new ParentNode(new MapBuilder<String,FileNode<?>>()
				.with("maps", new MapNode<SGMap>(new JSONObjectFile<SGMap>(SGMap.class), new Factory<Map<String,SGMap>>(){ public Map<String, SGMap> create() { return new HashMap<String, SGMap>();}}, ".json"))
				.with("items.json", new JSONObjectFile<SGItem[]>(SGItem[].class))
				.end());
		
		try {
			
			log(Level.INFO, "Loading data...");
			this.saveDefaultConfig();
			this.reloadConfig();
			Map<String, Object> files = node.load(this.getDataFolder());
			maps = (Map<String, SGMap>) files.get("maps");
			items = (SGItem[]) files.get("items.json");
			log(Level.INFO, "Successfully loaded data.");
			
		} catch(IOException e) {
			
			log(Level.WARNING, "Data files either missing or corrupted. Using default data.");
			maps = new HashMap<String, SGMap>();
			items = new SGItem[0];
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
	
	private void save() throws IOException {
		
		Map<String, Object> files = new HashMap<String, Object>();
		files.put("maps", maps);
		files.put("items.json", items);
		
		log(Level.INFO, "Saving data...");
		node.save(getDataFolder(), files);
		
		log(Level.INFO, "Successfully saved data.");
		
	}
	
	/*Was thinking about making more complicated command executor, but ended up not doing it.
	 * Maybe for a larger plugin.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		SGPlayerData data = playerData.get(player.getUniqueId());
		switch(command.getName()) {
			case "sgselectmap" : {
				if(args.length == 0) {
					data.selectedMap = null;
					player.sendMessage("You have deselected your map.");
				} else {
					if(!maps.containsKey(args[0])) {
						player.sendMessage("New map " + args[0] + " created.");
						maps.put(args[0], new SGMap());
						log(Level.INFO, "New map " + args[0] + " has been created.");
					}
					data.selectedMap = args[0];
					player.sendMessage("Map " + args[0] + " has been selected.");
				}
				return true;
			}
			case "sgaddchest" : {
				if(data.selectedMap == null) {
					player.sendMessage("You have not selected a map. Use /selectmap <map-name> to select or create a map.");
				} else {
					SGMap map = maps.get(data.selectedMap);
					Block block = player.getTargetBlock((Set<Material>) null, TARGET_RANGE);
					if(block.getType() != Material.CHEST) {
						player.sendMessage("Block must be a chest!");
						return true;
					}
					Location loc = block.getLocation();
					double rarity = Double.parseDouble(args[0]);
					map.chests = addToArray(map.chests, new SGChest(loc, rarity));
					player.sendMessage("Chest at " + loc + " with rarity " + rarity + " has been successfully added to map " + data.selectedMap);
				}
				return true;
			}
			case "sgaddspawnpoint" : {
				if(data.selectedMap == null) {
					player.sendMessage("You have not selected a map. Use /selectmap <map-name> to select or create a map.");
				} else {
					SGMap map = maps.get(data.selectedMap);
					Location loc = player.getLocation();
					map.spawnPoints = addToArray(map.spawnPoints, new SLocation(player.getLocation()));
					player.sendMessage("Spawnpoint at " + loc + " has been successfully added to map " + data.selectedMap);
				}
				return true;
			}
			case "sgadditem" : {
				ItemStack item = player.getItemInHand();
				SGItem sgItem = SGItem.fromItemStack(item, Double.parseDouble(args[0]),
						Integer.parseInt(args[1]), Integer.parseInt(args[2]),
						Integer.parseInt(args[3]), Integer.parseInt(args[4]));
				items = addToArray(items, sgItem);
				player.sendMessage(sgItem.type + " has been successfully added.");
				return true;
			}
			case "sgadditemdamaged" : {
				ItemStack item = player.getItemInHand();
				SGItem sgItem = SGItem.fromItemStackDamaged(item, Double.parseDouble(args[0]),
						Integer.parseInt(args[1]), Integer.parseInt(args[2]));
				items = addToArray(items, sgItem);
				player.sendMessage(sgItem.type + " has been successfully added.");
				return true;
			}
			case "sgaddentity" : {
				if(data.selectedMap == null) {
					player.sendMessage("You have not selected a map. Use /selectmap <map-name> to select or create a map.");
				} else {
					SGMap map = maps.get(data.selectedMap);
					SGEntity entity = new SGEntity(args[0], new SLocation(player.getLocation()));
					map.entities = addToArray(map.entities, entity);
					player.sendMessage(entity.type + " has been successfully added to map " + data.selectedMap);
				}
				return true;
			}
			case "sgaddmineable" : {
				if(data.selectedMap == null) { player.sendMessage("You have not selected a map. Use /selectmap <map-name> to select or create a map."); } else {
					SGMap map = maps.get(data.selectedMap);
					SGBlockType block = args.length == 0 ? new SGBlockType(player.getTargetBlock((Set<Material>) null, TARGET_RANGE))
					: new SGBlockType(args[0], -1);
					map.mineables = addToArray(map.mineables, block);
					player.sendMessage(block.type + " has become mineable on map " + data.selectedMap);
				}
				return true;
			}
			case "sgaddplaceable" : {
				if(data.selectedMap == null) { player.sendMessage("You have not selected a map. Use /selectmap <map-name> to select or create a map."); } else {
					SGMap map = maps.get(data.selectedMap);
					SGBlockType block = args.length == 0 ? new SGBlockType(player.getTargetBlock((Set<Material>) null, TARGET_RANGE))
					: new SGBlockType(args[0], -1);
					map.placeables = addToArray(map.placeables, block);
					player.sendMessage(block.type + " has become placeable on map " + data.selectedMap);
				}
				return true;
			}
			case "sgsave" : {
				try {
					save();
					player.sendMessage("Data files successfully saved.");
				} catch (IOException e) {
					player.sendMessage("There was an error saving the data. Check the console.");
					e.printStackTrace();
				}
				return true;
			}
			case "sgload" : {
				try {
					load();
					player.sendMessage("Data files successfully reloaded.");
				} catch (Exception e) {
					player.sendMessage("There was an error loading the data. Check the console.");
					e.printStackTrace();
				}
				return true;
			}
			case "sglistmaps" : {
				if(maps.size() == 0) player.sendMessage("There are no loaded maps.");
				else for(String name : maps.keySet()) player.sendMessage(name);
				return true;
			}
			case "sgstart" : {
				try {
					game.start();
				} catch (SGGameException e) {
					player.sendMessage(e.getMessage());
				}
				return true;
			}
			case "sgsetmap" : {
				game.setMap(args[0]);
				player.sendMessage("Game map has been set to " + args[0] + ".");
				return true;
			}
			case "sgjoin" : {
				try {
					game.join(player);
				} catch (SGGameException e) {
					player.sendMessage(e.getMessage());
				}
				return true;
			}
			case "sgstop" : {
				try {
					game.end();
				} catch (SGGameException e) {
					player.sendMessage(e.getMessage());
				}
				return true;
			}
			case "sgleave" : {
				try {
					game.leave(player);
				} catch (SGGameException e) {
					player.sendMessage(e.getMessage());
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onEnable() {
		try {
			loadAPIs();
			load();
		} catch (Exception e) {
			log(Level.SEVERE, "Error loading! Default data could not be used!");
			e.printStackTrace();
		}
		playerData = new AdvancedMap<UUID, SGPlayerData>(new Factory<SGPlayerData>(){
			public SGPlayerData create() {
				return new SGPlayerData();
			}
		});
		game = new SGGame(this, null);
		SGChest.pluginInstance = this;
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
	
	private void log(Level level, String msg) {
		this.getLogger().log(level, msg);
	}

	private static <T> T[] addToArray(T[] array, T element) {
		T[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = element;
		return newArray;
	}
	
}
