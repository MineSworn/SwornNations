package com.massivecraft.factions.cmd;

import me.t7seven7t.swornnations.npermissions.NPermission;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;

/**
 * @author dmulloy2
 */

public class CmdConfirm extends FCommand
{
	public CmdConfirm()
	{
		super();
		this.aliases.add("confirm");
		this.aliases.add("accept");
		
		this.requiredArgs.add("player name");
		
		this.permission = Permission.INVITE.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		senderMustHaveNPermission = NPermission.INVITE;
		
		this.setHelpShort("confirm a player's invitation");
	}
	
	@Override
	public void perform()
	{
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;
		
		if (you.getFaction() == myFaction)
		{
			msg("%s<i> is already a member of %s", you.describeTo(fme), myFaction.describeTo(fme));
			msg("<i>You might want to: " +  p.cmdBase.cmdKick.getUseageTemplate(false));
			return;
		}
		
		if (myFaction.isConfirmed(you))
		{
			fme.msg("%s<i> has already been confirmed.", you.describeTo(fme));
			return;
		}
		
		if (!myFaction.isInvited(you))
		{
			fme.msg("%s<i> has not yet been invited. You should:", you.describeTo(fme));
			fme.msg(p.cmdBase.cmdInvite.getUseageTemplate(false));
			return;
		}
		
		myFaction.confirm(you);
		
		you.msg("<i>Your invitation to <i>%s<i> has been confirmed!", fme.getFaction().describeTo(you));
		
		fme.msg("<i>You have successfully confirmed <i>%s<i>s invitation!", you.describeTo(fme));
	}
}