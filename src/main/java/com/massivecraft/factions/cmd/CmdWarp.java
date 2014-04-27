package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornnations.types.NPermission;

import org.bukkit.Location;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.EssentialsFeatures;
import com.massivecraft.factions.types.Permission;
import com.massivecraft.factions.types.Role;
import com.massivecraft.factions.util.SmokeUtil;

public class CmdWarp extends FCommand
{

	public CmdWarp()
	{
		this.aliases.add("warp");

		this.permission = Permission.WARP.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		senderMustHaveNPermission = NPermission.WARP;
	}

	@Override
	public void perform()
	{
		if (! Conf.warpsEnabled)
		{
			fme.msg("<b>Sorry, Faction warps are disabled on this server.");
			return;
		}

		if (! myFaction.hasWarp())
		{
			fme.msg("<b>Your faction does not have a warp. "
					+ (fme.getRole().value < Role.ADMIN.value ? "<i> Ask your leader to:" : "<i>You should:"));
			fme.sendMessage(p.cmdBase.cmdSetwarp.getUseageTemplate());
			return;
		}

		if (! Conf.warpsTeleportAllowedFromEnemyTerritory && fme.isInEnemyTerritory())
		{
			fme.msg("<b>You cannot teleport to your faction warp while in the territory of an enemy faction.");
			return;
		}

		Faction faction = Board.getFactionAt(new FLocation(me.getLocation()));
		Location loc = me.getLocation().clone();

		if (isEnemyNearby(faction, loc))
			return;

		if (! payPowerForCommand(Conf.warpsPowerCostToUse))
			return;

		if (EssentialsFeatures.handleTeleport(me, myFaction.getWarp()))
			return;

		// Create a smoke effect
		if (Conf.warpsTeleportCommandSmokeEffectEnabled)
		{
			List<Location> smokeLocations = new ArrayList<Location>();
			smokeLocations.add(loc);
			smokeLocations.add(loc.add(0, 1, 0));
			smokeLocations.add(myFaction.getWarp());
			smokeLocations.add(myFaction.getWarp().clone().add(0, 1, 0));
			SmokeUtil.spawnCloudRandom(smokeLocations, Conf.warpsTeleportCommandSmokeEffectThickness);
		}

		me.teleport(myFaction.getWarp());
	}

}
