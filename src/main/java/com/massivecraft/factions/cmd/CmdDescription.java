package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.types.NPermission;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.types.Permission;
import com.massivecraft.factions.util.TextUtil;

public class CmdDescription extends FCommand
{
	public CmdDescription()
	{
		super();
		this.aliases.add("desc");

		this.requiredArgs.add("desc");
		this.errorOnToManyArgs = false;
		// this.optionalArgs

		this.permission = Permission.DESCRIPTION.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		senderMustHaveNPermission = NPermission.DESCRIPTION;
	}

	@Override
	public void perform()
	{
		// if economy is enabled, they're not on the bypass list, and this
		// command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostDesc, "to change faction description", "for changing faction description"))
			return;

		// if
		// (sender.hasPermission("pexchat.color")||sender.hasPermission("essentials.chat.color"))
		// myFaction.setDescription(TextUtil.implode(args, " ").replaceAll("&",
		// "�")); // If player has permission for chat color, let 'em use it
		// else
		myFaction.setDescription(TextUtil.implode(args, " ").replaceAll("(&([a-f0-9]))", "")); // If
																								// not,
																								// replace
																								// the
																								// color
																								// tags
																								// with
																								// ""

		// Broadcast the description to everyone
		for (FPlayer fplayer : FPlayers.i.getOnline())
		{
			fplayer.msg("<i>The faction %s<i> changed their description to:", myFaction.describeTo(fplayer));
			fplayer.sendMessage(myFaction.getDescription());
		}
	}
}