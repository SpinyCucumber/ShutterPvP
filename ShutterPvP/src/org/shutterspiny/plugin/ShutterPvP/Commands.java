package org.shutterspiny.plugin.ShutterPvP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shutterspiny.lib.PluginUtils.command.AbstractCommand;
import org.shutterspiny.lib.PluginUtils.command.AbstractCommand.ArgConverter;
import org.shutterspiny.plugin.ShutterPvP.SGPlugin.SGPlayerData;
import org.shutterspiny.plugin.ShutterPvP.item.SGChest;
import org.shutterspiny.plugin.ShutterPvP.map.SGMap;

public final class Commands {
	
	public static abstract class SGMapCommand extends AbstractCommand<SGPlugin, Player> {
		
		protected abstract String runMap(Player sender, SGMap map, String name, List<Object> args) throws CommandException;

		@Override
		protected String run(Player sender, List<Object> args) throws CommandException {
			SGPlayerData data = this.getPlugin().getPlayerData(sender);
			if(data.selectedMap == null) throw new CommandException("You have not selected a map.");
			return runMap(sender, this.getPlugin().getMaps().get(data.selectedMap), data.selectedMap, args);
		}
		
		public SGMapCommand(ArgConverter<?>[] args, String name) {
			super(args, name, Player.class);
		}

	}

	protected static final int RANGE = 100;

	public static List<AbstractCommand<SGPlugin, ?>> sgCommands;
	
	static {
		
		sgCommands = new ArrayList<AbstractCommand<SGPlugin, ?>>();
		
		sgCommands.add(new AbstractCommand<SGPlugin, CommandSender>(new ArgConverter<?>[0], "sgsave", CommandSender.class){
			protected String run(CommandSender sender, List<Object> args) throws CommandException {
				try {
					this.getPlugin().save();
				} catch (IOException e) {
					throw new CommandException("Error saving data. Check console.");
				}
				return "Data files have been successfully saved.";
			}
		});
		
		sgCommands.add(new AbstractCommand<SGPlugin, CommandSender>(new ArgConverter<?>[0], "sgload", CommandSender.class){
			protected String run(CommandSender sender, List<Object> args) throws CommandException {
				try {
					this.getPlugin().load();
				} catch (Exception e) {
					throw new CommandException("Error loading data. Check console.");
				}
				return "Data files have been successfully reloaded.";
			}
		});
		
		sgCommands.add(new AbstractCommand<SGPlugin, Player>(new ArgConverter<?>[]{AbstractCommand.NONE}, "sgeditmap", Player.class){
			protected String run(Player sender, List<Object> args) throws CommandException {
				String name = (String) args.get(0);
				if(!this.getPlugin().getMaps().containsKey(name))
					throw new CommandException("That map does not exist.");
				if(this.getPlugin().getPlayerData(sender).selectedMap == name)
					throw new CommandException("You've already selected that map.");
				this.getPlugin().getPlayerData(sender).selectedMap = name;
				return "The map " + name + " has been selected.";
			}
		});
		
		sgCommands.add(new AbstractCommand<SGPlugin, Player>(new ArgConverter<?>[0], "sgstopeditting", Player.class){
			protected String run(Player sender, List<Object> args) throws CommandException {
				if(this.getPlugin().getPlayerData(sender).selectedMap == null)
					throw new CommandException("You haven't selected a map.");
				this.getPlugin().getPlayerData(sender).selectedMap = null;
				return "You have deselected your map.";
			}
		});
		
		sgCommands.add(new AbstractCommand<SGPlugin, CommandSender>(new ArgConverter<?>[]{AbstractCommand.NONE}, "sgcreatemap", CommandSender.class){
			protected String run(CommandSender sender, List<Object> args) throws CommandException {
				String name = (String) args.get(0);
				if(this.getPlugin().getMaps().containsKey(name))
					throw new CommandException("That map already exists.");
				this.getPlugin().getMaps().put(name, new SGMap());
				return "The new map " + name + " has been created.";
			}
		});
		
		sgCommands.add(new AbstractCommand<SGPlugin, CommandSender>(new ArgConverter<?>[0], "sglistmaps", CommandSender.class){
			protected String run(CommandSender sender, List<Object> args) throws CommandException {
				int size = this.getPlugin().getMaps().size();
				if(size == 0)
					throw new CommandException("There are no maps created. Use /sgcreatemap <map-name> to create a map.");
				for(String name : this.getPlugin().getMaps().keySet())
					sender.sendMessage(name);
				return "Listed " + size + " maps.";
			}
		});
		
		sgCommands.add(new SGMapCommand(new ArgConverter<?>[0], "sgaddspawnpoint"){
			protected String runMap(Player sender, SGMap map, String name, List<Object> args) throws CommandException {
				Location loc = sender.getLocation();
				map.spawnPoints.add(loc);
				return "Spawnpoint at " + loc + " has been successfully added to map " + name;
			}
		});
		
		sgCommands.add(new SGMapCommand(new ArgConverter<?>[]{AbstractCommand.DOUBLE}, "sgaddchest"){
			protected String runMap(Player sender, SGMap map, String name, List<Object> args) throws CommandException {
				Double rarity = (Double) args.get(0);
				Block block = sender.getTargetBlock((Set<Material>) null, RANGE);
				if(block.getType() != Material.CHEST) throw new CommandException("Block must be a chest!");
				map.chests.add(new SGChest(block.getLocation(), rarity));
				return "Chest at " + block.getLocation() + " with rarity " + rarity + " has been successfully added to map " + name;
			}
		});
		
		sgCommands.add(new SGMapCommand(new ArgConverter<?>[0], "sgaddmineable"){
			protected String runMap(Player sender, SGMap map, String name, List<Object> args) throws CommandException {
				Block block = sender.getTargetBlock((Set<Material>) null, RANGE);
				
			}
		});
		
	}
	
}
