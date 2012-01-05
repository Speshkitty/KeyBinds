package uk.co.speshkittyonline.keyBinds;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;
import org.getspout.spoutapi.gui.Screen;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.player.SpoutPlayer;

public class KeyBinds extends JavaPlugin
{
	
	@Override
	public void onDisable()
	{
		getServer().getLogger().log(Level.INFO, "Keybinding disabled!");
	}

	@Override
	public void onEnable()
	{
		getServer().getPluginManager().registerEvent(Event.Type.CUSTOM_EVENT, 
			new InputListener()
			{
				public void onKeyReleasedEvent(KeyReleasedEvent event)
				{
					Screen playerScreen = event.getPlayer().getCurrentScreen();
					if(playerScreen.getScreenType() != ScreenType.GAME_SCREEN) { return; }
					
					SpoutPlayer player = event.getPlayer();
					String command = getConfig().getString(player.getPlayerListName() + "." + event.getKey(), "");
					
					if(!command.equalsIgnoreCase("")) { player.chat(command); }
				}
			},
		Event.Priority.Normal, this);
		getServer().getLogger().log(Level.INFO, "Keybinding enabled!");
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
		if(command.getName().equalsIgnoreCase("bind"))
		{
			if(!(sender instanceof Player)) { sender.sendMessage("You are not Player, what is this I don't even"); return true; }
			SpoutPlayer player = (SpoutPlayer) sender;
			if(!player.isSpoutCraftEnabled()) { sender.sendMessage("You need to be using the Spout client for this command!"); return true; }
			if(args.length == 0)
			{
				Help(player, "Not enough arguments!");
				return true;
			}
			if(args.length == 1)
			{
				if(args[0].equalsIgnoreCase("set")) { Help(player, "Not enough arguments!", "set"); }
				else if(args[0].equalsIgnoreCase("remove")) { Help(player, "Not enough arguments!", "remove"); }
				else if(args[0].equalsIgnoreCase("show"))
				{
					player.sendMessage("This doesn't exist yet.");
					//TODO
				}
				else { Help(player, "Unrecognised command!"); }
				return true;
			}
			if(args.length == 2)
			{
				if(args[0].equalsIgnoreCase("set")) { Help(player, "Not enough arguments!", "set"); }
				else if(args[0].equalsIgnoreCase("remove"))
				{
					Keyboard key = null;
					try { key = Keyboard.valueOf("KEY_" + args[1].toUpperCase()); }
					catch (Exception e)
					{
						if(key == null)
						{
							player.sendMessage(ChatColor.DARK_RED + args[1] + ChatColor.RED + " is not a recognised letter!");
							return true;
						}
					}
					getConfig().set(player.getPlayerListName() + "." + key.toString(), null);
					saveConfig();
					player.sendMessage(ChatColor.RED + "The command bound to " + ChatColor.DARK_RED + getLetter(key) + ChatColor.RED + " was removed.");
					
				}
				else if(args[0].equalsIgnoreCase("show"))
				{
					Keyboard key = null;
					try { key = Keyboard.valueOf("KEY_" + args[1].toUpperCase()); }
					catch (Exception e)
					{
						if(key == null)
						{
							player.sendMessage(ChatColor.DARK_RED + args[1] + ChatColor.RED + " is not a recognised letter!");
							return true;
						}
					}
					String oldCommand = getConfig().getString(player.getPlayerListName() + "." + key.toString());
					if(oldCommand == null) { player.sendMessage(ChatColor.RED + "No command is bound to " + ChatColor.DARK_RED + getLetter(key)); }
					else { player.sendMessage(ChatColor.RED + "The command bound to " + ChatColor.DARK_RED + getLetter(key) + ChatColor.RED + " is " + ChatColor.DARK_RED + oldCommand); }
				}
				else { Help(player, "Unrecognised command!"); }
				return true;
			}
			if(args.length >= 3)
			{
				if(args[0].equalsIgnoreCase("remove")) { Help(player, "Too many arguments!", "remove"); }
				else if(args[0].equalsIgnoreCase("set"))
				{
					String newCommand = "";
					for(int i=2; i<args.length;i++) { newCommand = newCommand.concat(args[i]).concat(" "); } //Build the string
					newCommand = newCommand.trim();
					
					if(!newCommand.substring(0, 1).equalsIgnoreCase("/")) //If the entered text isn't a command
					{
						newCommand = "/".concat(newCommand);
						player.sendMessage(ChatColor.RED + "A / character was added to the start of the command.");
					}
					
					Keyboard key = null;
					try { key = Keyboard.valueOf("KEY_" + args[1].toUpperCase()); }
					catch (Exception e)
					{
						if(key == null)
						{
							player.sendMessage(ChatColor.DARK_RED + args[1] + ChatColor.RED + " is not a recognised letter!");
							return true;
						}
					}
					getConfig().set(player.getPlayerListName() + "." + key.toString(), newCommand);
					saveConfig();
					player.sendMessage(ChatColor.RED + "The command " + ChatColor.DARK_RED + newCommand + ChatColor.RED + " was assigned to " + args[1]);
				}
				else if(args[0].equalsIgnoreCase("show")) { Help(player, "Too many arguments!", "show"); }
				else { Help(player, "Unrecognised command!"); }
				return true;
			}
		}
		return true;
	}
	protected String getLetter(Keyboard key) { return getLetter(key.toString()); }
	protected String getLetter(String key) { return key.substring(4).toLowerCase(); }

	protected void Help(SpoutPlayer player, String reason) { Help(player, reason, "all"); }
	protected void Help(SpoutPlayer player, String reason, String part)
	{
		player.sendMessage(ChatColor.RED + reason);
		if(part.equalsIgnoreCase("all") || part.equalsIgnoreCase("set"))
		{
			player.sendMessage(ChatColor.RED + "Adds or changes a keybind.");
			player.sendMessage(ChatColor.DARK_RED + "/bind set <letter> <command>");
			player.sendMessage(ChatColor.RED + "Example usage:" + ChatColor.DARK_RED + " /bind set g /gamemode");
		}
		if(part.equalsIgnoreCase("all"))
		{
			player.sendMessage("");
		}
		if(part.equalsIgnoreCase("all") || part.equalsIgnoreCase("remove"))
		{
			player.sendMessage(ChatColor.RED + "Removes a keybind.");
			player.sendMessage(ChatColor.DARK_RED + "/bind remove <letter>");
			player.sendMessage(ChatColor.RED + "Example usage:" + ChatColor.DARK_RED + " /bind remove g");
		}
		if(part.equalsIgnoreCase("all"))
		{
			player.sendMessage("");
		}
		if(part.equalsIgnoreCase("all") || part.equalsIgnoreCase("show"))
		{
			player.sendMessage(ChatColor.RED + "Shows you an existing keybind, or all keybinds.");
			player.sendMessage(ChatColor.DARK_RED + "/bind show [letter]");
			player.sendMessage(ChatColor.RED + "Example usage:" + ChatColor.DARK_RED + " /bind show");
			player.sendMessage(ChatColor.RED + "Example usage:" + ChatColor.DARK_RED + " /bind show g");
		}
	}
}
