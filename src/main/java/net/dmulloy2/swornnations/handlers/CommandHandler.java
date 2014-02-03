package net.dmulloy2.swornnations.handlers;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornnations.SwornNations;
import net.dmulloy2.swornnations.commands.SwornNationsCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author dmulloy2
 */

public class CommandHandler implements CommandExecutor
{
	private final SwornNations plugin;
	private String commandPrefix;
	private List<SwornNationsCommand> registeredCommands;

	public CommandHandler(final SwornNations plugin)
	{
		this.plugin = plugin;
		registeredCommands = new ArrayList<SwornNationsCommand>();
	}

	public void registerCommand(SwornNationsCommand command)
	{
		if (commandPrefix != null)
			registeredCommands.add(command);
	}

	public List<SwornNationsCommand> getRegisteredCommands()
	{
		return registeredCommands;
	}

	public String getCommandPrefix()
	{
		return commandPrefix;
	}

	public void setCommandPrefix(String commandPrefix)
	{
		this.commandPrefix = commandPrefix;
		plugin.getCommand(commandPrefix).setExecutor(this);
	}

	public boolean usesCommandPrefix()
	{
		return commandPrefix != null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		List<String> argsList = new ArrayList<String>();

		if (args.length > 0)
		{
			String commandName = args[0];
			for (int i = 1; i < args.length; i++)
				argsList.add(args[i]);

			for (SwornNationsCommand command : registeredCommands)
			{
				if (commandName.equalsIgnoreCase(command.getName()) || command.getAliases().contains(commandName.toLowerCase()))
				{
					command.execute(sender, argsList.toArray(new String[0]));
					return true;
				}
			}

			sender.sendMessage(ChatColor.RED + "Unknown SwornNations command \"" + args[0] + "\". Try /ua help!");
		}
		else
		{
			// new CmdHelp(plugin).execute(sender, args);
		}

		return true;
	}
}