package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornnations.types.NPermission;

import org.bukkit.Location;
import org.bukkit.Material;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.EssentialsFeatures;
import com.massivecraft.factions.types.Permission;
import com.massivecraft.factions.types.Role;
import com.massivecraft.factions.util.SmokeUtil;

public class CmdOutpost extends FCommand
{
	public CmdOutpost()
	{
		super();
		this.aliases.add("outpost");

		this.optionalArgs.put("tag", "mine");

		this.permission = Permission.OUTPOST.node;
		this.disableOnLock = false;

		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;

		senderMustHaveNPermission = NPermission.OUTPOST;

		this.setHelpShort("teleport to your faction outpost");
	}

	@Override
	public void perform()
	{
		if (args.size() > 0)
		{
			if (Permission.OUTPOST_OTHERS.has(me))
			{
				Faction targetFaction = this.argAsFaction(0);
				if (targetFaction == null)
					return;

				if (targetFaction.hasOutpost())
				{
					Location Factionoutpost = targetFaction.getOutpost();
					me.teleport(Factionoutpost);
					fme.msg("<i>You have been teleported to the Outpost of %s<i>.", targetFaction.describeTo(fme));
				}
				else
				{
					fme.msg("<b>That faction doesn't have an outpost!");
				}
				return;
			}
		}

		if (!Conf.homesEnabled)
		{
			fme.msg("<b>Sorry, Faction outposts are disabled on this server.");
			return;
		}

		if (!Conf.homesTeleportCommandEnabled)
		{
			fme.msg("<b>Sorry, the ability to teleport to Faction outposts is disabled on this server.");
			return;
		}

		if (!myFaction.hasOutpost())
		{
			fme.msg("<b>Your faction does not have a outpost. "
					+ (fme.getRole().value < Role.MODERATOR.value ? "<i> Ask your leader to:" : "<i>You should:"));
			fme.sendMessage(p.cmdBase.cmdSetoutpost.getUseageTemplate());
			return;
		}

		if (!Conf.homesTeleportAllowedFromEnemyTerritory && fme.isInEnemyTerritory())
		{
			fme.msg("<b>You cannot teleport to your faction outpost while in the territory of an enemy faction.");
			return;
		}

		if (!Conf.homesTeleportAllowedFromDifferentWorld && me.getWorld().getUID() != myFaction.getOutpost().getWorld().getUID())
		{
			fme.msg("<b>You cannot teleport to your faction outpost while in a different world.");
			return;
		}

		if (!Permission.BYPASS.has(me) && Conf.homesMustBeGreaterThan > 0 && myFaction.getOutpost().getBlockY() < Conf.homesMustBeGreaterThan)
		{
			if (moveoutpost())
			{
				fme.msg("<b>Your faction outpost has been moved as it was underground.");
			}
		}

		Faction faction = Board.getFactionAt(new FLocation(me.getLocation()));
		Location loc = me.getLocation().clone();

		if (isEnemyNearby(faction, loc))
			return;
		// if player is not in a safe zone or their own faction territory, only
		// allow teleport if no enemies are nearby

		// if Essentials teleport handling is enabled and available, pass the
		// teleport off to it (for delay and cooldown)
		if (EssentialsFeatures.handleTeleport(me, myFaction.getOutpost()))
			return;

		// if economy is enabled, they're not on the bypass list, and this
		// command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostHome, "to teleport to your faction outpost", "for teleporting to your faction outpost"))
			return;

		// Create a smoke effect
		if (Conf.homesTeleportCommandSmokeEffectEnabled)
		{
			List<Location> smokeLocations = new ArrayList<Location>();
			smokeLocations.add(loc);
			smokeLocations.add(loc.add(0, 1, 0));
			smokeLocations.add(myFaction.getOutpost());
			smokeLocations.add(myFaction.getOutpost().clone().add(0, 1, 0));
			SmokeUtil.spawnCloudRandom(smokeLocations, Conf.homesTeleportCommandSmokeEffectThickness);
		}

		me.teleport(myFaction.getOutpost());
	}

	public boolean moveoutpost()
	{
		Location outpost = myFaction.getOutpost();
		while (outpost.getBlockY() < Conf.homesMustBeGreaterThan && outpost.getBlockY() < 256 && !checkIsValidoutpost(outpost))
		{
			outpost = outpost.add(0, 1, 0);
		}

		if (outpost.getBlockY() == 256)
		{
			return false;
		}

		myFaction.setOutpost(outpost);
		return true;
	}

	public boolean checkIsValidoutpost(Location outpost)
	{
		if (outpost.getWorld().getBlockAt(outpost).getType() == Material.AIR
				&& outpost.getWorld().getBlockAt(outpost.clone().add(0, 1, 0)).getType() == Material.AIR
				&& outpost.getWorld().getBlockAt(outpost.clone().subtract(0, -1, 0)).getType() != Material.AIR)
			return true;
		return false;
	}

}