package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Role;

/**
 * @author dmulloy2
 */

public class CmdInitiate extends FCommand
{
	public CmdInitiate()
	{
		super();
		this.aliases.add("initiate");
		
		this.requiredArgs.add("player name");
		
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = true;
		
		this.setHelpShort("remove initiate status from a player");
	}
	
	@Override
	public void perform()
	{
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;
		
		Faction myFaction = fme.getFaction();
		if (myFaction == null)
		{
			fme.msg("<i>You must be in a faction to do this!");
		}
		
		if ((myFaction != fme.getFaction()) && !fme.isAdminBypassing())
		{
			fme.msg("<b>You can only do this for players in your faction!");
			return;
		}
		
		if (you.getRole() != Role.INITIATE)
		{
			fme.msg("<i>You can only do this for initiates!");
			return;
		}
		
		you.setRole(Role.NORMAL);
		
		fme.msg("<i>You have removed initiate status from <i>%s<i>.", you.describeTo(fme));
		
		myFaction.msg(fme.describeTo(myFaction) + " <i>has removed initiate status from <i>" + you.describeTo(myFaction));
	}
}