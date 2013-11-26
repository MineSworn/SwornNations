/**
 * (c) 2013 dmulloy2
 */
package com.massivecraft.factions.util;

import org.bukkit.ChatColor;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TextUtil;

/**
 * RelationPartipation for Console. Console is always neutral.
 * 
 * @author dmulloy2
 */

public class Console implements RelationParticipator
{
	// -------------------------------
	// Instance
	// -------------------------------

	private static Console i;
	public Console() { i = this; }
	public static Console get() { return i; }

	// FIELD: name
	private String name = "Console";
	public String getName()
	{
		return this.name;
	}

	// -------------------------------
	// Relation and relation colors
	// -------------------------------

	@Override
	public String describeTo(RelationParticipator that, boolean ucfirst)
	{
		String ret = "";

		if (that instanceof FPlayer)
		{
			ret = ((FPlayer) that).getName();
		}
		else if (that instanceof Faction)
		{
			ret = ((Faction) that).getTag();
		}
		else if (that instanceof Console)
		{
			ret = ((Console) that).getName();
		}

		if (ucfirst)
		{
			ret = TextUtil.upperCaseFirst(ret);
		}

		return ret;
	}

	@Override
	public String describeTo(RelationParticipator that)
	{
		String ret = "";

		if (that instanceof FPlayer)
		{
			ret = ((FPlayer) that).getName();
		}
		else if (that instanceof Faction)
		{
			ret = ((Faction) that).getTag();
		}
		else if (that instanceof Console)
		{
			ret = ((Console) that).getName();
		}

		return ret;
	}

	@Override
	public Relation getRelationTo(RelationParticipator rp)
	{
		return Relation.NEUTRAL;
	}

	@Override
	public Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful)
	{
		return Relation.NEUTRAL;
	}

	@Override
	public ChatColor getColorTo(RelationParticipator rp)
	{
		return ChatColor.WHITE;
	}
}