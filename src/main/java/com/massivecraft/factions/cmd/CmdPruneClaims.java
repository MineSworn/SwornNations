package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.SwornNations;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.types.Permission;

public class CmdPruneClaims extends FCommand
{

	public CmdPruneClaims()
	{
		super();
		this.aliases.add("pruneclaim");

		this.permission = Permission.PRUNE.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform()
	{
		int x = Board.cleanupClaims();
		fme.msg("<i>You have pruned %s claims.", x);
		SwornNations.get().log(fme.getName() + " pruned the map of faction claims not near their bases.");

	}

}
