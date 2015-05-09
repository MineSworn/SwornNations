package com.massivecraft.factions.util;

import java.util.HashMap;
import java.util.Map;

import net.dmulloy2.swornnations.SwornNations;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.massivecraft.factions.types.Lang;

public class PermUtil
{
	private final Map<String, String> descriptions;
	private final SwornNations plugin;

	public PermUtil(SwornNations plugin)
	{
		this.descriptions = new HashMap<>();
		for (Permission perm : plugin.getDescription().getPermissions())
		{
			descriptions.put(perm.getName(), perm.getDescription());
		}

		this.plugin = plugin;
	}

	public String getForbiddenMessage(String perm)
	{
		return plugin.txt.parse(Lang.permForbidden, getPermissionDescription(perm));
	}

	public String getPermissionDescription(String perm)
	{
		String desc = descriptions.get(perm);
		return desc != null ? desc : Lang.permDoThat;
	}

	public boolean has(CommandSender me, String perm)
	{
		if (me == null)
			return false;

		if (! (me instanceof Player))
		{
			return me.hasPermission(perm);
		}

		return me.hasPermission(perm);
	}

	public boolean has(CommandSender me, String perm, boolean informSenderIfNot)
	{
		if (me == null)
			return false;

		if (has(me, perm))
		{
			return true;
		}
		else if (informSenderIfNot)
		{
			me.sendMessage(getForbiddenMessage(perm));
		}

		return false;
	}
}