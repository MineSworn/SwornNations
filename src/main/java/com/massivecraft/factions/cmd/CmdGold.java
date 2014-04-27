/**
 * (c) 2014 dmulloy2
 */
package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.SwornNations;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdGold extends FCommand
{
	public CmdGold()
	{
		super();
		this.aliases.add("gold");
		this.requiredArgs.add("faction tag");

		this.permission = Permission.SET_GOLD.node;
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
		if (faction.isGold())
		{
			change = "removed gold status from";
			faction.setGold(false);
		}
		else
		{
			change = "granted gold status to";
			faction.setGold(true);
			faction.setPeaceful(false);
		}

		SwornNations.get()
				.log((fme == null ? "A server admin" : fme.getName()) + " " + change + " the faction \"" + faction.getTag() + "\".");

		for (FPlayer fplayer : FPlayers.i.getOnline())
		{
			if (fplayer.getFaction() == faction)
			{
				fplayer.msg((fme == null ? "A server admin" : fme.describeTo(fplayer, true)) + "<i> " + change + " your faction.");
			}
			else
			{
				fplayer.msg((fme == null ? "A server admin" : fme.describeTo(fplayer, true)) + "<i> " + change + " the faction \""
						+ faction.getTag(fplayer) + "<i>\".");
			}
		}
	}
}