package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.SwornNations;

import com.massivecraft.factions.types.Permission;

public class CmdVersion extends FCommand
{
	public CmdVersion()
	{
		this.aliases.add("version");

		// this.requiredArgs.add("");
		// this.optionalArgs.put("", "");

		this.permission = Permission.VERSION.node;
		this.disableOnLock = false;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform()
	{
		msg("<i>You are running " + SwornNations.get().getDescription().getFullName());
	}
}
