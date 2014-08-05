package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.types.NPermission;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.types.Permission;

public class CmdPerm extends FCommand
{

	public CmdPerm()
	{
		super();
		this.aliases.add("perm");
		this.requiredArgs.add("p/r");
		this.requiredArgs.add("player name/role");
		this.requiredArgs.add("perm");
		this.requiredArgs.add("allow/deny");

		this.permission = Permission.PERM.node;
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
		if (args.get(0).equalsIgnoreCase("p"))
		{
			FPlayer you = this.argAsBestFPlayerMatch(1);
			if (you == null)
				return;

			if (! you.hasFaction() || ! fme.hasFaction())
				return;

			if (you.getFaction() != fme.getFaction())
			{
				msg("%s<i> is not in your faction.", you.describeTo(fme));
				return;
			}
			NPermission perm = null;
			for (NPermission p : NPermission.values())
			{
				if (p.toString().equalsIgnoreCase(args.get(2)))
					perm = p;
			}
			if (perm == null)
			{
				msg("<i>%s <b>did not match any permissions.", args.get(2));
				return;
			}

			if (args.get(3).equalsIgnoreCase("t") || args.get(3).equalsIgnoreCase("allow"))
			{
				fme.getFaction().addPermission(you, perm);
				msg("<i>%s added to %s.", perm.toString(), you.describeTo(fme));
			}
			else if (args.get(3).equalsIgnoreCase("f") || args.get(3).equalsIgnoreCase("deny"))
			{
				fme.getFaction().removePermission(you, perm);
				msg("<i>%s removed from %s.", perm.toString(), you.describeTo(fme));
			}
		}
		else if (args.get(0).equalsIgnoreCase("r"))
		{
			if (! fme.hasFaction())
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
			else if (args.get(1).equalsIgnoreCase("initiate"))
				role = Role.INITIATE;

			if (role == null)
			{
				msg("<i>%s <b>did not match any roles.", args.get(1));
				return;
			}

			NPermission perm = null;
			for (NPermission p : NPermission.values())
			{
				if (p.toString().equalsIgnoreCase(args.get(2)))
					perm = p;
			}
			if (perm == null)
			{
				msg("<i>%s <b>did not match any permissions.", args.get(2));
				return;
			}

			if (args.get(3).equalsIgnoreCase("t") || args.get(3).equalsIgnoreCase("allow"))
			{
				fme.getFaction().addPermission(role, perm);
				msg("<i>%s added to %s.", perm, role.nicename);
			}
			else if (args.get(3).equalsIgnoreCase("f") || args.get(3).equalsIgnoreCase("deny"))
			{
				fme.getFaction().removePermission(role, perm);
				msg("<i>%s removed from from %s.", perm, role.nicename);
			}
		}
	}

}
