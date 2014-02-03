package net.dmulloy2.swornnations.handlers;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.struct.Permission;

/**
 * @author dmulloy2
 */

public class PermissionHandler
{
	public boolean hasPermission(CommandSender sender, Permission permission)
	{
		return (permission == null) ? true : hasPermission(sender, getPermissionString(permission));
	}

	public boolean hasPermission(CommandSender sender, String permission)
	{
		if (sender instanceof Player)
		{
			Player p = (Player) sender;
			return (p.hasPermission(permission) || p.isOp());
		}

		return true;
	}

	private String getPermissionString(Permission permission)
	{
		return "factions" + "." + permission.node.toLowerCase();
	}
}