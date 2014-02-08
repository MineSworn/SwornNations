package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.types.NPermission;

import com.massivecraft.factions.types.Relation;

public class CmdRelationAlly extends FRelationCommand
{
	public CmdRelationAlly()
	{
		aliases.add("ally");
		targetRelation = Relation.ALLY;
		senderMustHaveNPermission = NPermission.ALLY;
	}
}
