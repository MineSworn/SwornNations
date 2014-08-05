package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.types.NPermission;

import com.massivecraft.factions.struct.Relation;

public class CmdRelationNeutral extends FRelationCommand
{
	public CmdRelationNeutral()
	{
		aliases.add("neutral");
		targetRelation = Relation.NEUTRAL;
		senderMustHaveNPermission = NPermission.NEUTRAL;
	}
}
