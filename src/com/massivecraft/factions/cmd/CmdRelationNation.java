/**
 * Copyright (C) 2012 t7seven7t
 */
package com.massivecraft.factions.cmd;

import me.t7seven7t.swornnations.npermissions.NPermission;

import com.massivecraft.factions.struct.Relation;

/**
 * @author t7seven7t
 */
public class CmdRelationNation extends FRelationCommand {

	public CmdRelationNation() {
		aliases.add("nation");
		targetRelation = Relation.NATION;
		senderMustHaveNPermission = NPermission.NATION;
	}
	
}
