package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.types.NPermission;

import com.massivecraft.factions.types.Relation;

public class CmdRelationEnemy extends FRelationCommand
{
	public CmdRelationEnemy()
	{
		aliases.add("enemy");
		targetRelation = Relation.ENEMY;
		senderMustHaveNPermission = NPermission.ENEMY;
	}
}
