package org.shutterspiny.plugin.ShutterPvP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shutterspiny.lib.PluginUtils.command.AbstractCommand;
import org.shutterspiny.lib.PluginUtils.command.AbstractCommand.ArgConverter;
import org.shutterspiny.plugin.ShutterPvP.SGPlugin.SGPlayerData;
import org.shutterspiny.plugin.ShutterPvP.map.SGMap;

public final class Commands {
	
	public static abstract class SGMapCommand extends AbstractCommand<SGPlugin, Player> {
		
		protected abstract String runMap(Player sender, SGMap map, List<Object> args);

		@Override
		protected String run(Player sender, List<Object> args) throws CommandException {
			SGPlayerData data = this.getPlugin().getPlayerData(sender);
			return runMap(sender, this.getPlugin().getMaps().get(data.selectedMap), args);
		}
		
		public SGMapCommand(ArgConverter<?>[] args, String name) {
			super(args, name, Player.class);
		}

	}

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
				this.getPlugin().getPlayerData(sender).selectedMap = name;
				return "The map " + name + " has been selected.";
			}
		});
		
	}
	
}
