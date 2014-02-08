package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.types.NPermission;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.types.Permission;
import com.massivecraft.factions.util.TextUtil;

public class CmdTitle extends FCommand
{
	public CmdTitle()
	{
		this.aliases.add("title");

		this.requiredArgs.add("player name");
		this.optionalArgs.put("title", "");

		this.permission = Permission.TITLE.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		senderMustHaveNPermission = NPermission.TITLE;
	}

	@Override
	public void perform()
	{
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null)
			return;

		args.remove(0);
		String title = TextUtil.implode(args, " ");

		if (!canIAdministerYou(fme, you))
			return;

		// if economy is enabled, they're not on the bypass list, and this
		// command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostTitle, "to change a players title", "for changing a players title"))
			return;

		you.setTitle(title);

		// Inform
		myFaction.msg("%s<i> changed a title: %s", fme.describeTo(myFaction, true), you.describeTo(myFaction, true));
	}

}
