package net.dmulloy2.swornnations.commands;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornnations.SwornNations;
import net.dmulloy2.swornnations.util.FormatUtil;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.struct.Permission;

/**
 * @author dmulloy2
 */

// TODO: Implement SwornNations/Factions esque stuff
public abstract class SwornNationsCommand implements CommandExecutor
{
	protected final SwornNations plugin;

	protected CommandSender sender;
	protected Player player;
	protected String args[];

	protected String name;
	protected String description;

	protected Permission permission;

	protected boolean mustBePlayer;

	protected List<String> requiredArgs;
	protected List<String> optionalArgs;
	protected List<String> aliases;

	public SwornNationsCommand(SwornNations plugin)
	{
		this.plugin = plugin;
		this.requiredArgs = new ArrayList<String>(2);
		this.optionalArgs = new ArrayList<String>(2);
		this.aliases = new ArrayList<String>(2);
	}

	@Override
	public final boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		execute(sender, args);
		return true;
	}

	public final void execute(CommandSender sender, String[] args)
	{
		this.sender = sender;
		this.args = args;

		if (sender instanceof Player)
			player = (Player) sender;

		if (mustBePlayer && ! isPlayer())
		{
			err("You must be a player to execute this command!");
			return;
		}

		if (requiredArgs.size() > args.length)
		{
			invalidArgs();
			return;
		}

		if (! hasPermission())
		{
			err("You do not have permission to perform this command!");
			// log(Level.WARNING, sender.getName() + " was denied access to a command!");
			return;
		}

		try
		{
			perform();
		}
		catch (Throwable e)
		{
			err("Error executing command: {0}", e.getMessage());
			// plugin.getLogHandler().debug(Util.getUsefulStack(e, "executing command " + name));
		}
	}

	public abstract void perform();

	protected final boolean isPlayer()
	{
		return player != null;
	}

	private final boolean hasPermission()
	{
		return permission.has(sender);
	}

	public final String getDescription()
	{
		return FormatUtil.format(description);
	}

	public final List<String> getAliases()
	{
		return aliases;
	}

	public final String getName()
	{
		return name;
	}

	public final String getUsageTemplate(final boolean displayHelp)
	{
		StringBuilder ret = new StringBuilder();
		ret.append("&b/ua ");

		ret.append(name);

		ret.append("&3 ");
		for (String s : requiredArgs)
			ret.append(String.format("<%s> ", s));

		for (String s : optionalArgs)
			ret.append(String.format("[%s] ", s));

		if (displayHelp)
			ret.append("&e" + description);

		return FormatUtil.format(ret.toString());
	}

	protected final void sendMessage(String message, Object... objects)
	{
		sender.sendMessage(ChatColor.YELLOW + FormatUtil.format(message, objects));
	}

	protected final void err(String string, Object... objects)
	{
		sendMessage("&c" + string, objects);
	}

	protected final void invalidArgs()
	{
		err("Invalid arguments! Try: " + getUsageTemplate(false));
	}
}