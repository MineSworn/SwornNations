package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.SwornNations;
import net.dmulloy2.swornnations.types.NPermission;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.types.Permission;

public class CmdUnclaim extends FCommand
{
	public CmdUnclaim()
	{
		this.aliases.add("unclaim");
		this.aliases.add("declaim");

		// this.requiredArgs.add("");
		// this.optionalArgs.put("", "");

		this.permission = Permission.UNCLAIM.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		senderMustHaveNPermission = NPermission.UNCLAIM;
	}

	@Override
	public void perform()
	{
		FLocation flocation = new FLocation(fme);
		Faction otherFaction = Board.getAbsoluteFactionAt(flocation);

		if (otherFaction.isSafeZone())
		{
			if (Permission.MANAGE_SAFE_ZONE.has(sender))
			{
				Board.removeAt(flocation);
				msg("<i>Safe zone was unclaimed.");

				if (Conf.logLandUnclaims)
					SwornNations.get().log(
							fme.getName() + " unclaimed land at (" + flocation.getCoordString() + ") from the faction: "
									+ otherFaction.getTag());
			}
			else
			{
				msg("<b>This is a safe zone. You lack permissions to unclaim.");
			}
			return;
		}
		else if (otherFaction.isWarZone())
		{
			if (Permission.MANAGE_WAR_ZONE.has(sender))
			{
				Board.removeAt(flocation);
				msg("<i>War zone was unclaimed.");

				if (Conf.logLandUnclaims)
					SwornNations.get().log(
							fme.getName() + " unclaimed land at (" + flocation.getCoordString() + ") from the faction: "
									+ otherFaction.getTag());
			}
			else
			{
				msg("<b>This is a war zone. You lack permissions to unclaim.");
			}
			return;
		}

		if (fme.isAdminBypassing())
		{
			Board.removeAt(flocation);
			otherFaction.msg("%s<i> unclaimed some of your land.", fme.describeTo(otherFaction, true));
			msg("<i>You unclaimed this land.");

			if (Conf.logLandUnclaims)
				SwornNations.get().log(
						fme.getName() + " unclaimed land at (" + flocation.getCoordString() + ") from the faction: " + otherFaction.getTag());

			return;
		}

		if (! assertHasFaction())
		{
			return;
		}

		if (! assertMinRole(Role.MODERATOR))
		{
			return;
		}

		if (myFaction != otherFaction)
		{
			msg("<b>You don't own this land.");
			return;
		}

		LandUnclaimEvent unclaimEvent = new LandUnclaimEvent(flocation, otherFaction, fme);
		SwornNations.get().getServer().getPluginManager().callEvent(unclaimEvent);
		if (unclaimEvent.isCancelled())
			return;

		if (Econ.shouldBeUsed())
		{
			double refund = Econ.calculateClaimRefund(myFaction.getLandRounded());

			if (Conf.bankEnabled && Conf.bankFactionPaysLandCosts)
			{
				if (! Econ.modifyMoney(myFaction, refund, "to unclaim this land", "for unclaiming this land"))
					return;
			}
			else
			{
				if (! Econ.modifyMoney(fme, refund, "to unclaim this land", "for unclaiming this land"))
					return;
			}
		}

		Board.removeAt(flocation);
		myFaction.msg("%s<i> unclaimed some land.", fme.describeTo(myFaction, true));

		if (Conf.logLandUnclaims)
			SwornNations.get().log(
					fme.getName() + " unclaimed land at (" + flocation.getCoordString() + ") from the faction: " + otherFaction.getTag());
	}

}
