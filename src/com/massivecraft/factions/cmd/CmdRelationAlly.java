package com.massivecraft.factions.cmd;

import me.t7seven7t.swornnations.npermissions.NPermission;

import com.massivecraft.factions.struct.Relation;

public class CmdRelationAlly extends FRelationCommand
{
	public CmdRelationAlly()
	{
		aliases.add("ally");
		targetRelation = Relation.ALLY;
		senderMustHaveNPermission = NPermission.ALLY;
	}
}
