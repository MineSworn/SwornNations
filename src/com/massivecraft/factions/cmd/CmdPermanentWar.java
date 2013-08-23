/**
 * Copyright (C) 2012 t7seven7t
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;

/**
 * @author t7seven7t
 */
public class CmdPermanentWar extends FCommand
{

	public CmdPermanentWar()
	{
		super();
		this.aliases.add("permanentwar");
		this.requiredArgs.add("faction tag");

		this.permission = Permission.SET_PERMANENTWAR.node;
		this.disableOnLock = true;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform()
	{
		Faction faction = this.argAsFaction(0);
		if (faction == null)
			return;

		String change;
		if (faction.isPermanentWar())
		{
			change = "removed permanent war status from";
			faction.setPermanentWar(false);
		}
		else
		{
			change = "granted permanent war status to";
			faction.setPermanentWar(true);
			faction.setPeaceful(false);
		}

		P.p.log((fme == null ? "A server admin" : fme.getName()) + " " + change + " the faction \"" + faction.getTag() + "\".");

		for (FPlayer fplayer : FPlayers.i.getOnline())
		{
			if (fplayer.getFaction() == faction)
			{
				fplayer.msg((fme == null ? "A server admin" : fme.describeTo(fplayer, true)) + "<i> " + change + " your faction.");
			}
			else
			{
				fplayer.msg((fme == null ? "A server admin" : fme.describeTo(fplayer, true)) + "<i> " + change + " the faction \"" + faction.getTag(fplayer)
						+ "<i>\".");
			}
		}
	}

}
