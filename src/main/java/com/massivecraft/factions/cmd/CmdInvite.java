package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.types.NPermission;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.types.Permission;

public class CmdInvite extends FCommand
{
	public CmdInvite()
	{
		super();
		this.aliases.add("invite");
		this.aliases.add("inv");

		this.requiredArgs.add("player name");
		// this.optionalArgs.put("", "");

		this.permission = Permission.INVITE.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		senderMustHaveNPermission = NPermission.INVITE;
	}

	@Override
	public void perform()
	{
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null)
			return;

		if (you.getFaction() == myFaction)
		{
			msg("%s<i> is already a member of %s", you.getName(), myFaction.getTag());
			msg("<i>You might want to: " + p.cmdBase.cmdKick.getUseageTemplate(false));
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this
		// command has a cost set, make 'em pay
		if (! payForCommand(Conf.econCostInvite, "to invite someone", "for inviting someone"))
			return;

		myFaction.invite(you);

		fme.msg("<i>WARNING: This Player could be good, or he could wait for you to go offline and steal your diamonds, TNT your base, and rape your sheep. Are you SURE you want to invite him? (use /f confirm or /f deny)");

		you.msg("%s<i> invited you to %s", fme.describeTo(you, true), myFaction.describeTo(you));
		myFaction.msg("%s<i> invited %s<i> to your faction.", fme.describeTo(myFaction, true), you.describeTo(myFaction));
	}
}