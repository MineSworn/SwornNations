package com.massivecraft.factions.cmd;

import java.util.ArrayList;

import net.dmulloy2.swornnations.SwornNations;

import com.massivecraft.factions.types.CommandVisibility;

public class CmdAutoHelp extends MCommand<SwornNations>
{
	public CmdAutoHelp()
	{
		super(SwornNations.get());
		this.aliases.add("?");
		this.aliases.add("h");
		this.aliases.add("help");

		this.setHelpShort("");

		this.optionalArgs.put("page", "1");
	}

	@Override
	public void perform()
	{
		if (this.commandChain.size() == 0)
			return;
		MCommand<?> pcmd = this.commandChain.get(this.commandChain.size() - 1);

		ArrayList<String> lines = new ArrayList<String>();

		lines.addAll(pcmd.helpLong);

		for (MCommand<?> scmd : pcmd.subCommands)
		{
			if (scmd.visibility == CommandVisibility.VISIBLE
					|| (scmd.visibility == CommandVisibility.SECRET && scmd.validSenderPermissions(sender, false)))
			{
				lines.add(scmd.getUseageTemplate(this.commandChain, true));
			}
		}

		sendMessage(p.txt.getPage(lines, this.argAsInt(0, 1), "Help for command \"" + pcmd.aliases.get(0) + "\""));
	}
}
