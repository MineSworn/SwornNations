package com.massivecraft.factions.cmd;

import me.t7seven7t.swornnations.npermissions.NPermission;

import com.massivecraft.factions.struct.Relation;

public class CmdRelationEnemy extends FRelationCommand
{
	public CmdRelationEnemy()
	{
		aliases.add("enemy");
		targetRelation = Relation.ENEMY;
		senderMustHaveNPermission = NPermission.ENEMY;
	}
}
