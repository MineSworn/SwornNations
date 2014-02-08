package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.types.NPermission;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdDeny extends FCommand
{
	public CmdDeny()
	{
		super();
		this.aliases.add("deny");
		this.aliases.add("reject");

		this.requiredArgs.add("player name");

		this.permission = Permission.INVITE.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		senderMustHaveNPermission = NPermission.INVITE;

		this.setHelpShort("deny a player's invitation");
	}

	@Override
	public void perform()
	{
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null)
			return;

		if (you.getFaction() == myFaction)
		{
			msg("%s<i> is already a member of %s", you.describeTo(fme), myFaction.describeTo(fme));
			msg("<i>You might want to: " + p.cmdBase.cmdKick.getUseageTemplate(false));
			return;
		}

		if (!myFaction.isInvited(you))
		{
			fme.msg("%s<i> has not yet been invited.", you.describeTo(fme));
			return;
		}

		myFaction.deny(you);

		you.msg("<i>Your invitation to <i>%s<i> has been denied.", fme.getFaction().describeTo(you));

		fme.msg("<i>You have successfully denied <i>%s<i>s invitation!", you.describeTo(fme));
	}
}
