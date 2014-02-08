/**
 * Copyright (C) 2012 t7seven7t
 */
package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.types.NPermission;

import com.massivecraft.factions.types.Relation;

/**
 * @author t7seven7t
 */
public class CmdRelationNation extends FRelationCommand
{

	public CmdRelationNation()
	{
		aliases.add("nation");
		targetRelation = Relation.NATION;
		senderMustHaveNPermission = NPermission.NATION;
	}

}
