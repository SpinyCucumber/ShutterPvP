package org.shutterspiny.plugin.ShutterPvP;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.shutterspiny.lib.PluginUtils.command.AbstractCommand;
import org.shutterspiny.lib.PluginUtils.command.AbstractCommand.Argument;
import org.shutterspiny.lib.PluginUtils.command.Args;
import org.shutterspiny.lib.PluginUtils.command.ItemCommand;
import org.shutterspiny.plugin.ShutterPvP.SGPlugin.SGPlayerData;
import org.shutterspiny.plugin.ShutterPvP.item.SGChest;
import org.shutterspiny.plugin.ShutterPvP.item.SGItem;
import org.shutterspiny.plugin.ShutterPvP.map.SGBlockType;
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
		
		public SGMapCommand(Argument<?>[] args, String name) {
			super(args, name, Player.class);
		}

	}

	protected static final int RANGE = 100;

	public static List<AbstractCommand<SGPlugin, ?>> sgCommands;
	
	static {
		
		sgCommands = new ArrayList<AbstractCommand<SGPlugin, ?>>();
		
		sgCommands.add(new AbstractCommand<SGPlugin, CommandSender>(new Argument<?>[]{}, "sgsave", CommandSender.class){
			protected String run(CommandSender sender, List<Object> args) throws CommandException {
				try {
					this.getPlugin().save();
				} catch (Exception e) {
					throw new CommandException("Error saving data. Check console.");
				}
				return "Data files have been successfully saved.";
			}
		});
		
		sgCommands.add(new AbstractCommand<SGPlugin, CommandSender>(new Argument<?>[]{}, "sgload", CommandSender.class){
			protected String run(CommandSender sender, List<Object> args) throws CommandException {
				try {
					this.getPlugin().load();
				} catch (Exception e) {
					throw new CommandException("Error loading data. Check console.");
				}
				return "Data files have been successfully reloaded.";
			}
		});
		
		sgCommands.add(new AbstractCommand<SGPlugin, Player>(new Argument<?>[]{Args.NONE}, "sgeditmap", Player.class){
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
		
		sgCommands.add(new AbstractCommand<SGPlugin, Player>(new Argument<?>[]{}, "sgstopeditting", Player.class){
			protected String run(Player sender, List<Object> args) throws CommandException {
				if(this.getPlugin().getPlayerData(sender).selectedMap == null)
					throw new CommandException("You haven't selected a map.");
				this.getPlugin().getPlayerData(sender).selectedMap = null;
				return "You have deselected your map.";
			}
		});
		
		sgCommands.add(new AbstractCommand<SGPlugin, CommandSender>(new Argument<?>[]{Args.NONE}, "sgcreatemap", CommandSender.class){
			protected String run(CommandSender sender, List<Object> args) throws CommandException {
				String name = (String) args.get(0);
				if(this.getPlugin().getMaps().containsKey(name))
					throw new CommandException("That map already exists.");
				this.getPlugin().getMaps().put(name, new SGMap());
				return "The new map " + name + " has been created.";
			}
		});
		
		sgCommands.add(new AbstractCommand<SGPlugin, CommandSender>(new Argument<?>[]{}, "sglistmaps", CommandSender.class){
			protected String run(CommandSender sender, List<Object> args) throws CommandException {
				int size = this.getPlugin().getMaps().size();
				if(size == 0)
					throw new CommandException("There are no maps created. Use /sgcreatemap <map-name> to create a map.");
				for(String name : this.getPlugin().getMaps().keySet())
					sender.sendMessage(name);
				return "Listed " + size + " maps.";
			}
		});
		
		sgCommands.add(new SGMapCommand(new Argument<?>[]{}, "sgaddspawnpoint"){
			protected String runMap(Player sender, SGMap map, String name, List<Object> args) throws CommandException {
				Location loc = sender.getLocation();
				map.spawnPoints.add(loc);
				return "Spawnpoint at " + loc + " has been successfully added to map " + name;
			}
		});
		
		sgCommands.add(new SGMapCommand(new Argument<?>[]{Args.DOUBLE}, "sgaddchest"){
			protected String runMap(Player sender, SGMap map, String name, List<Object> args) throws CommandException {
				Block block = sender.getTargetBlock((Set<Material>) null, RANGE);
				if(block.getType() != Material.CHEST) throw new CommandException("Block must be a chest!");
				SGChest chest = new SGChest(block.getLocation(), (Double) args.get(0));
				map.chests.add(chest);
				return "Chest " + chest + " has been successfully added to map " + name;
			}
		});
		
		sgCommands.add(new SGMapCommand(new Argument<?>[]{}, "sgaddmineable"){
			protected String runMap(Player sender, SGMap map, String name, List<Object> args) throws CommandException {
				SGBlockType type = new SGBlockType(sender.getTargetBlock((Set<Material>) null, RANGE));
				map.mineables.add(type);
				return type + " has become mineable on " + name;
			}
		});
		
		sgCommands.add(new SGMapCommand(new Argument<?>[]{}, "sgaddplaceable"){
			protected String runMap(Player sender, SGMap map, String name, List<Object> args) throws CommandException {
				SGBlockType type = new SGBlockType(sender.getTargetBlock((Set<Material>) null, RANGE));
				map.placeables.add(type);
				return type + " has become placeable on " + name;
			}
		});
		
		sgCommands.add(new ItemCommand<SGPlugin>(new Argument<?>[]{Args.DOUBLE, Args.INT, Args.INT, Args.INT, Args.INT}, "sgadditem"){
			protected String runItem(Player sender, ItemStack stack, List<Object> args) throws CommandException {
				SGItem item = SGItem.fromItemStack(stack, (Double) args.get(0),
						(Integer) args.get(1), (Integer) args.get(2), (Integer) args.get(3), (Integer) args.get(4));
				this.getPlugin().getItems().add(item);
				return "Successfully added " + item;
			}
		});
		
		sgCommands.add(new ItemCommand<SGPlugin>(new Argument<?>[]{Args.DOUBLE, Args.INT, Args.INT}, "sgadditemdamaged"){
			protected String runItem(Player sender, ItemStack stack, List<Object> args) throws CommandException {
				SGItem item = SGItem.fromItemStackDamaged(stack, (Double) args.get(0),
						(Integer) args.get(1), (Integer) args.get(2));
				this.getPlugin().getItems().add(item);
				return "Successfully added " + item;
			}
		});
		
		sgCommands.add(new ItemCommand<SGPlugin>(new Argument<?>[]{Args.ENCHANT, Args.INT}, "sgenchant"){
			protected String runItem(Player sender, ItemStack stack, List<Object> args) throws CommandException {
				Enchantment enchant = (Enchantment) args.get(0);
				Integer level = (Integer) args.get(1);
				stack.getItemMeta().addEnchant(enchant, level, true);
				return enchant.getName() + " x" + level + " has been added to " + stack + ".";
			}
		});
		
		sgCommands.add(new AbstractCommand<SGPlugin, CommandSender>(new Argument<?>[]{Args.NONE}, "sgsetmap", CommandSender.class){
			protected String run(CommandSender sender, List<Object> args) throws CommandException {
				String name = (String) args.get(0);
				this.getPlugin().getGame().setMap(name);
				return "Game map successfully set to " + name;
			}
		});
		
		sgCommands.add(new AbstractCommand<SGPlugin, CommandSender>(new Argument<?>[]{}, "sgstart", CommandSender.class){
			protected String run(CommandSender sender, List<Object> args) throws CommandException {
				this.getPlugin().getGame().start();
				return "GAM HAS BEN 574r73d!!!!111 HUEHUEHUE";
			}
		});
		
		sgCommands.add(new AbstractCommand<SGPlugin, CommandSender>(new Argument<?>[]{}, "sgstop", CommandSender.class){
			protected String run(CommandSender sender, List<Object> args) throws CommandException {
				this.getPlugin().getGame().end();
				return "You have stopped the game.";
			}
		});
		
		sgCommands.add(new AbstractCommand<SGPlugin, Player>(new Argument<?>[]{}, "sgjoin", Player.class){
			protected String run(Player sender, List<Object> args) throws CommandException {
				this.getPlugin().getGame().join(sender);
				return "You have joined the game.";
			}
		});
		
		sgCommands.add(new AbstractCommand<SGPlugin, Player>(new Argument<?>[]{}, "sgleave", Player.class){
			protected String run(Player sender, List<Object> args) throws CommandException {
				this.getPlugin().getGame().leave(sender);
				return "You have left the game.";
			}
		});
		
	}
	
}
