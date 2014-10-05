/**
 * (c) 2014 dmulloy2
 */
package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.types.NPermission;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.types.Permission;
import com.massivecraft.factions.util.TextUtil;

/**
 * @author dmulloy2
 */

public class CmdMotd extends FCommand
{
	public CmdMotd()
	{
		this.aliases.add("motd");

		this.requiredArgs.add("motd");

		this.permission = Permission.MOTD.node;
		this.setHelpShort("set your faction's MOTD");

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		senderMustHaveNPermission = NPermission.MOTD;
	}

	@Override
	public void perform()
	{
		Faction faction = myFaction;
		if (faction == null || ! faction.isNormal())
		{
			sendMessage("&cYou do not have a faction!");
			return;
		}

		faction.setMOTD(TextUtil.parseColor(TextUtil.implode(args, " ")));
		faction.msg("&aYour faction&e''s MOTD is now %s", faction.getMOTD());
	}
}