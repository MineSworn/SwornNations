package com.massivecraft.factions.cmd;

import me.t7seven7t.swornnations.npermissions.NPermission;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;

public class CmdOfficer extends FCommand {

	public CmdOfficer() {
		super();
		this.aliases.add("officer");
		this.requiredArgs.add("player name");
		
		this.permission = Permission.OFFICER.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		senderMustHaveNPermission = NPermission.OFFICER;
	}
	
	@Override
	public void perform() {
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;
		
		boolean permAny = Permission.OFFICER_ANY.has(sender, false);
		Faction targetFaction = you.getFaction();

		if (targetFaction != myFaction && !permAny)
		{
			msg("%s<b> is not a member in your faction.", you.describeTo(fme, true));
			return;
		}

		if (you == fme && !permAny)
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
			msg("<b>The target player is a faction co-admin. Demote them first.");
			return;
		}

		if (you.getRole() == Role.OFFICER)
		{
			// Revoke
			you.setRole(Role.NORMAL);
			targetFaction.msg("%s<i> is no longer officer in your faction.", you.describeTo(targetFaction, true));
			msg("<i>You have removed officer status from %s<i>.", you.describeTo(fme, true));
		}
		else
		{
			// Give
			you.setRole(Role.OFFICER);
			targetFaction.msg("%s<i> was promoted to officer in your faction.", you.describeTo(targetFaction, true));
			msg("<i>You have promoted %s<i> to officer.", you.describeTo(fme, true));
		}
	}
}