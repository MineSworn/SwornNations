package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.types.NPermission;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.types.Permission;

public class CmdOwner extends FCommand
{
	public CmdOwner()
	{
		super();
		this.aliases.add("owner");

		this.optionalArgs.put("player name", "you");

		this.permission = Permission.OWNER.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		senderMustHaveNPermission = NPermission.OWNER;
	}

	@Override
	public void perform()
	{
		boolean hasBypass = fme.isAdminBypassing();
		if (! hasBypass && ! assertHasFaction())
			return;

		if (! Conf.ownedAreasEnabled)
		{
			fme.msg("<b>Sorry, but owned areas are disabled on this server.");
			return;
		}

		if (! hasBypass && Conf.ownedAreasLimitPerFaction > 0 && myFaction.getCountOfClaimsWithOwners() >= Conf.ownedAreasLimitPerFaction)
		{
			fme.msg("<b>Sorry, but you have reached the server's <h>limit of %d <b>owned areas per faction.", Conf.ownedAreasLimitPerFaction);
			return;
		}

		if (! hasBypass && Conf.ownedAreasLimitPerPlayer > 0 && myFaction.getCountOfClaimsWithOwner(fme) >= Conf.ownedAreasLimitPerPlayer)
		{
			fme.msg("<b>Sorry, but you have reached the server's <h>limit of %d <b>owned areas per player.", Conf.ownedAreasLimitPerPlayer);
			return;
		}

		if (! hasBypass && ! assertMinRole(Conf.ownedAreasModeratorsCanSet ? Role.MODERATOR : Role.ADMIN))
			return;

		FLocation flocation = new FLocation(fme);

		Faction factionHere = Board.getFactionAt(flocation);
		if (factionHere != myFaction)
		{
			if (! hasBypass)
			{
				fme.msg("<b>This land is not claimed by your faction, so you can't set ownership of it.");
				return;
			}

			if (! factionHere.isNormal())
			{
				fme.msg("<b>This land is not claimed by a faction. Ownership is not possible.");
				return;
			}
		}

		FPlayer target = argAsBestFPlayerMatch(0, fme);
		if (target == null)
			return;

		String playerName = target.describeTo(fme);

		if (target.getFaction() != myFaction)
		{
			fme.msg("%s<i> is not a member of this faction.", playerName);
			return;
		}

		if (Conf.ownedAreasPlayersCanOnlyClaimOwn && ! target.getName().equals(fme.getName()) && ! fme.isAdminBypassing())
		{
			fme.msg("<i>You can't change ownership for other players.");
			return;
		}

		if (Conf.ownedAreasPlayersCanOnlyClaimOwn && myFaction.doesLocationHaveOwnersSet(flocation)
				&& ! myFaction.isPlayerInOwnerList(fme.getName(), flocation) && ! fme.isAdminBypassing())
		{
			fme.msg("<i>You cannot claim owner on this plot, it is already owned by %s.", myFaction.getOwnerListString(flocation));
			return;
		}

		// if no player name was passed, and this claim does already have owners
		// set, clear them
		if (args.isEmpty() && myFaction.doesLocationHaveOwnersSet(flocation))
		{
			myFaction.clearClaimOwnership(flocation);
			fme.msg("<i>You have cleared ownership for this claimed area.");
			return;
		}

		if (myFaction.isPlayerInOwnerList(playerName, flocation))
		{
			myFaction.removePlayerAsOwner(playerName, flocation);
			fme.msg("<i>You have removed ownership of this claimed land from %s<i>.", playerName);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this
		// command has a cost set, make 'em pay
		if (! payForCommand(Conf.econCostOwner, "to set ownership of claimed land", "for setting ownership of claimed land"))
			return;

		myFaction.setPlayerAsOwner(target.getName(), flocation);

		fme.msg("<i>You have added %s<i> to the owner list for this claimed land.", playerName);
	}
}
