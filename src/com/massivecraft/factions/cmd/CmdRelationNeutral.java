package com.massivecraft.factions.cmd;

import me.t7seven7t.swornnations.npermissions.NPermission;

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
