package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.SwornNations;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Permission;

public class CmdClean extends FCommand
{

	public CmdClean()
	{
		super();
		this.aliases.add("clean");

		this.permission = Permission.CLEAN.node;
		this.disableOnLock = true;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform()
	{
		FPlayers.i.cleanWildernessPlayers();
		fme.msg("<i>Cleaning all players that are in wilderness.");
		SwornNations.get().log(fme.getName() + " has begun cleaning of all players who are in wilderness.");
	}

}
