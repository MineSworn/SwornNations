package com.massivecraft.factions.util;

import org.bukkit.ChatColor;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.Relation;

public class RelationUtil
{
	public static String describeThatToMe(RelationParticipator that, RelationParticipator me, boolean ucfirst)
	{
		String ret = "";

		Faction thatFaction = getFaction(that);
		if (thatFaction == null)
			return "ERROR"; // ERROR

		Faction myFaction = getFaction(me);
		// if (myFaction == null) return that.describeTo(null); // no relation,
		// but can show basic name or tag

		if (that instanceof Faction)
		{
			if (me instanceof FPlayer && myFaction == thatFaction)
			{
				ret = "your faction";
			}
			else
			{
				ret = thatFaction.getTag();
			}
		}
		else if (that instanceof FPlayer)
		{
			FPlayer fplayerthat = (FPlayer) that;
			if (that == me)
			{
				ret = "you";
			}
			else if (thatFaction == myFaction)
			{
				ret = fplayerthat.getNameAndTitle();
			}
			else
			{
				ret = fplayerthat.getNameAndTag();
			}
		}

		if (ucfirst)
		{
			ret = TextUtil.upperCaseFirst(ret);
		}

		return "" + getColorOfThatToMe(that, me) + ret;
	}

	public static String describeThatToMe(RelationParticipator that, RelationParticipator me)
	{
		return describeThatToMe(that, me, false);
	}

	public static Relation getRelationTo(RelationParticipator me, RelationParticipator that)
	{
		return getRelationTo(that, me, false);
	}

	public static Relation getRelationTo(RelationParticipator me, RelationParticipator that, boolean ignorePeaceful)
	{
		Faction fthat = getFaction(that);
		if (fthat == null)
			return Relation.NEUTRAL; // ERROR

		Faction fme = getFaction(me);
		if (fme == null)
			return Relation.NEUTRAL; // ERROR

		if (! fthat.isNormal() || ! fme.isNormal())
		{
			return Relation.NEUTRAL;
		}

		if (fthat.equals(fme))
		{
			return Relation.MEMBER;
		}

		if (! ignorePeaceful && (fme.isPeaceful() || fthat.isPeaceful()))
		{
			return Relation.NEUTRAL;
		}

		if (fme.isPermanentWar() && fthat.isPermanentWar())
		{
			return Relation.ENEMY;
		}
		else if (fme.isPermanentWar() || fthat.isPermanentWar())
		{
			return Relation.NEUTRAL;
		}

		if (fme.isGold() && fthat.isGold())
		{
			return Relation.ENEMY;
		}
		else if (fme.isGold() || fthat.isGold() && fthat.getRelationWish(fme) != Relation.ENEMY)
		{
			return Relation.NEUTRAL;
		}

		if (fthat.getRelationWish(fme) == Relation.NATION || fme.getRelationWish(fme) == Relation.NATION)
		{
			return Relation.NATION;
		}

		if (fme.getRelationWish(fthat).value >= fthat.getRelationWish(fme).value)
		{
			return fthat.getRelationWish(fme);
		}

		return fme.getRelationWish(fthat);
	}

	public static Faction getFaction(RelationParticipator rp)
	{
		if (rp instanceof Faction)
		{
			return (Faction) rp;
		}

		if (rp instanceof FPlayer)
		{
			return ((FPlayer) rp).getFaction();
		}

		// ERROR
		return null;
	}

	public static ChatColor getColorOfThatToMe(RelationParticipator that, RelationParticipator me)
	{
		Faction thatFaction = getFaction(that);
		if (thatFaction != null)
		{
			if (thatFaction.isPeaceful() && thatFaction != getFaction(me))
			{
				return ChatColor.GOLD;
			}

			if (thatFaction.isGold() && thatFaction != getFaction(me))
			{
				return ChatColor.GOLD;
			}

			if (thatFaction.isSafeZone() && thatFaction != getFaction(me))
			{
				return ChatColor.GOLD;
			}

			if (thatFaction.isWarZone() && thatFaction != getFaction(me))
			{
				return ChatColor.DARK_RED;
			}
		}

		return getRelationTo(that, me).getColor();
	}
}
