package com.massivecraft.factions.cmd;

import me.t7seven7t.swornnations.npermissions.NPermission;

import org.bukkit.ChatColor;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;

public class CmdShowPerms extends FCommand
{

	public CmdShowPerms()
	{
		super();
		this.aliases.add("showperms");
		this.requiredArgs.add("p|r");
		this.requiredArgs.add("player name/role");

		this.permission = Permission.PERM_SHOW.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		senderMustHaveNPermission = NPermission.PERM;
	}

	@Override
	public void perform()
	{
		Faction f = fme.getFaction();

		if (args.get(0).equalsIgnoreCase("p"))
		{
			FPlayer you = this.argAsBestFPlayerMatch(1);
			if (you == null)
				return;

			if (!you.hasFaction() || !fme.hasFaction())
				return;

			if (you.getFaction() != fme.getFaction())
			{
				msg("%s<i> is not in your faction.", you.describeTo(fme));
				return;
			}

			msgHeader(you.describeTo(fme));
			StringBuilder ret = new StringBuilder();
			for (NPermission perm : NPermission.values())
			{
				if (f.playerHasPermission(you, perm))
					ret.append(ChatColor.GREEN + perm.toString() + ", ");
				else
					ret.append(ChatColor.RED + perm.toString() + ", ");
			}
			ret.deleteCharAt(ret.lastIndexOf(","));
			msg(ret.toString());
		}
		else if (args.get(0).equalsIgnoreCase("r"))
		{
			if (!fme.hasFaction())
				return;
			Role role = null;
			if (args.get(1).equalsIgnoreCase("mod") || args.get(1).equalsIgnoreCase(Role.MODERATOR.toString()))
				role = Role.MODERATOR;
			else if (args.get(1).equalsIgnoreCase("coadmin") || args.get(1).equalsIgnoreCase("co-admin"))
				role = Role.COADMIN;
			else if (args.get(1).equalsIgnoreCase("officer"))
				role = Role.OFFICER;
			else if (args.get(1).equalsIgnoreCase("default") || args.get(1).equalsIgnoreCase("normal"))
				role = Role.NORMAL;

			if (role == null)
			{
				msg("<i>%s <b>did not match any roles.", args.get(1));
				return;
			}

			msgHeader(role.nicename);

			StringBuilder ret = new StringBuilder();
			for (NPermission perm : NPermission.values())
			{
				if (f.roleHasPermission(role, perm))
					ret.append(ChatColor.GREEN + perm.toString() + ", ");
				else
					ret.append(ChatColor.RED + perm.toString() + ", ");
			}
			ret.deleteCharAt(ret.lastIndexOf(","));
			msg(ret.toString());
		}
	}

	public void msgHeader(String name)
	{
		msg("<a>Faction permissions for " + name + "<a>:");
	}
}
