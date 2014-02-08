package com.massivecraft.factions.cmd;

import com.massivecraft.factions.types.Permission;
import com.massivecraft.factions.types.Role;

public class CmdShowRoles extends FCommand
{

	public CmdShowRoles()
	{
		super();
		this.aliases.add("showroles");

		this.permission = Permission.SHOW_ROLES.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform()
	{
		msg("<a>Faction roles:");
		for (Role r : Role.values())
		{
			msg("<i>" + r.toString());
		}
	}

}
