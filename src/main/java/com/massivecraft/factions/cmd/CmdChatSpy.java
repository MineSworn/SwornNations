package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.SwornNations;

import com.massivecraft.factions.types.Permission;

public class CmdChatSpy extends FCommand
{
	public CmdChatSpy()
	{
		super();
		this.aliases.add("chatspy");

		this.optionalArgs.put("on/off", "flip");

		this.permission = Permission.CHATSPY.node;
		this.disableOnLock = false;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform()
	{
		fme.setSpyingChat(this.argAsBool(0, !fme.isSpyingChat()));

		if (fme.isSpyingChat())
		{
			fme.msg("<i>You have enabled chat spying mode.");
			SwornNations.get().log(fme.getName() + " has ENABLED chat spying mode.");
		}
		else
		{
			fme.msg("<i>You have disabled chat spying mode.");
			SwornNations.get().log(fme.getName() + " DISABLED chat spying mode.");
		}
	}
}