package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.types.NPermission;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.types.Permission;

public class CmdSetoutpost extends FCommand
{
	public CmdSetoutpost()
	{
		this.aliases.add("setoutpost");

		// this.requiredArgs.add("");
		this.optionalArgs.put("faction tag", "mine");

		this.permission = Permission.OUTPOST_SET.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		senderMustHaveNPermission = NPermission.SETOUTPOST;

		this.setHelpShort("set the outpost for your faction");
	}

	@Override
	public void perform()
	{
		if (!Conf.homesEnabled)
		{
			fme.msg("<b>Sorry, Faction outposts are disabled on this server.");
			return;
		}

		Faction faction = this.argAsFaction(0, myFaction);
		if (faction == null)
			return;

		// Can the player set the home for this faction?
		if (faction != myFaction)
		{
			if (! Permission.OUTPOST_SET_ANY.has(sender, true))
				return;
		}

		// Can the player set the faction home HERE?
		if (Conf.homesMustBeInClaimedTerritory && Board.getFactionAt(new FLocation(me)) != faction)
		{
			fme.msg("<b>Sorry, your faction outpost can only be set inside your own claimed territory.");
			return;
		}

		if (! fme.isAdminBypassing() && Conf.homesMustBeGreaterThan > 0 && me.getLocation().getBlockY() < Conf.homesMustBeGreaterThan)
		{
			fme.msg("<b>Sorry, your faction outpost can only be set above ground.");
			return;
		}

		if (! faction.hasHome())
		{
			fme.msg("<b>You must have a faction home set first!");
			return;
		}

		if (faction.getHome().getWorld().getUID() == me.getWorld().getUID())
		{
			fme.msg("<b>Sorry, your faction outpost cannot be in the same world as your home.");
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this
		// command has a cost set, make 'em pay
		if (!payForCommand(Conf.ecnCostSetoutpost, "to set the faction outpost", "for setting the faction outpost"))
			return;

		faction.setOutpost(me.getLocation());

		faction.msg("%s<i> set the outpost for your faction. You can now use:", fme.describeTo(myFaction, true));
		faction.sendMessage(p.cmdBase.cmdOutpost.getUseageTemplate());

		if (faction != myFaction)
		{
			fme.msg("<b>You have set the outpost for the " + faction.getTag(fme) + "<i> faction.");
		}
	}
}