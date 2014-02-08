package com.massivecraft.factions.cmd;

import com.massivecraft.factions.types.Permission;

public class CmdLock extends FCommand
{
	public CmdLock()
	{
		super();
		this.aliases.add("lock");

		// this.requiredArgs.add("");
		this.optionalArgs.put("on/off", "flip");

		this.permission = Permission.LOCK.node;
		this.disableOnLock = false;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform()
	{
		p.setLocked(argAsBool(0, ! p.getLocked()));

		if (p.getLocked())
		{
			msg("<i>Factions is now locked");
		}
		else
		{
			msg("<i>Factions in now unlocked");
		}
	}

}
