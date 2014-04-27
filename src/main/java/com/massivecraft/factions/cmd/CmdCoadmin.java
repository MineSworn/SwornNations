package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.types.Permission;
import com.massivecraft.factions.types.Role;

public class CmdCoadmin extends FCommand
{

	public CmdCoadmin()
	{
		super();
		this.aliases.add("coadmin");

		this.requiredArgs.add("player name");
		// this.optionalArgs.put("", "");

		this.permission = Permission.COADMIN.node;
		this.disableOnLock = true;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform()
	{
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null)
			return;

		boolean permAny = Permission.COADMIN_ANY.has(sender, false);
		Faction targetFaction = you.getFaction();

		if (targetFaction != myFaction && ! permAny)
		{
			msg("%s<b> is not a member in your faction.", you.describeTo(fme, true));
			return;
		}

		if (fme != null && fme.getRole() != Role.ADMIN && ! permAny)
		{
			msg("<b>You are not the faction admin.");
			return;
		}

		if (you == fme && ! permAny)
		{
			msg("<b>The target player musn't be yourself.");
			return;
		}

		if (you.getRole() == Role.ADMIN)
		{
			msg("<b>The target player is a faction admin. Demote them first.");
			return;
		}

		if (you.getRole() == Role.COADMIN)
		{
			// Revoke
			you.setRole(Role.NORMAL);
			targetFaction.msg("%s<i> is no longer a co-admin in your nation.", you.describeTo(targetFaction, true));
			msg("<i>You have removed co-admin status from %s<i>.", you.describeTo(fme, true));
		}
		else
		{
			// Give
			you.setRole(Role.COADMIN);
			targetFaction.msg("%s<i> was promoted to co-admin in your faction.", you.describeTo(targetFaction, true));
			msg("<i>You have promoted %s<i> to co-admin.", you.describeTo(fme, true));
		}
	}
}