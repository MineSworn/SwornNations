package net.dmulloy2.swornnations.commands;

import net.dmulloy2.commands.Command;
import net.dmulloy2.swornnations.SwornNations;

/**
 * @author dmulloy2
 */

// TODO: Implement SwornNations/Factions esque stuff
public abstract class SwornNationsCommand extends Command
{
	protected final SwornNations plugin;
	public SwornNationsCommand(SwornNations plugin)
	{
		super(plugin);
		this.plugin = plugin;
	}
}