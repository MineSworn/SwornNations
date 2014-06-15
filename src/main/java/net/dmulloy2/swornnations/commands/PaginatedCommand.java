package net.dmulloy2.swornnations.commands;

import net.dmulloy2.swornnations.SwornNations;

/**
 * @author dmulloy2
 */

public abstract class PaginatedCommand extends net.dmulloy2.commands.PaginatedCommand
{
	protected final SwornNations plugin;
	public PaginatedCommand(SwornNations plugin)
	{
		super(plugin);
		this.plugin = plugin;
	}
}