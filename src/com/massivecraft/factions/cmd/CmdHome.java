package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.EssentialsFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.SmokeUtil;

public class CmdHome extends FCommand
{
	public CmdHome()
	{
		super();
		this.aliases.add("home");
		this.aliases.add("factionhome");
		
		this.optionalArgs.put("tag", "mine");
		
		this.permission = Permission.HOME.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		if (args.size() > 0)
		{
			if (Permission.OTHER_HOME.has(me))
			{
				Faction targetFaction = this.argAsFaction(0);
				if (targetFaction == null) 
					return;
		    		
				if (targetFaction.hasHome()) 
				{
					Location FactionHome = targetFaction.getHome();
					me.teleport(FactionHome);
					fme.msg("<i>You have been teleported to the Faction home of %s<i>.", targetFaction.describeTo(fme));
				} 
				else 
				{
					fme.msg("<b>That faction doesn't have a home!");
				}
				return;
			}
		}

		if ( ! Conf.homesEnabled)
		{
			fme.msg("<b>Sorry, Faction homes are disabled on this server.");
			return;
		}

		if ( ! Conf.homesTeleportCommandEnabled)
		{
			fme.msg("<b>Sorry, the ability to teleport to Faction homes is disabled on this server.");
			return;
		}
		
		if ( ! myFaction.hasHome())
		{
			fme.msg("<b>Your faction does not have a home. " + (fme.getRole().value < Role.MODERATOR.value ? "<i> Ask your leader to:" : "<i>You should:"));
			fme.sendMessage(p.cmdBase.cmdSethome.getUseageTemplate());
			return;
		}
		
		if ( ! Conf.homesTeleportAllowedFromEnemyTerritory && fme.isInEnemyTerritory())
		{
			fme.msg("<b>You cannot teleport to your faction home while in the territory of an enemy faction.");
			return;
		}
		
		if ( ! Conf.homesTeleportAllowedFromDifferentWorld && me.getWorld().getUID() != myFaction.getHome().getWorld().getUID())
		{
			fme.msg("<b>You cannot teleport to your faction home while in a different world.");
			return;
		}
		
		if (!Permission.BYPASS.has(me) && Conf.homesMustBeGreaterThan > 0 && myFaction.getHome().getBlockY() < Conf.homesMustBeGreaterThan) {
			if (moveHome()) {
				fme.msg("<b>Your faction home has been moved as it was underground.");
			}
		}
		
		Faction faction = Board.getFactionAt(new FLocation(me.getLocation()));
		Location loc = me.getLocation().clone();
		
		if (isEnemyNearby(faction, loc)) return;
		// if player is not in a safe zone or their own faction territory, only allow teleport if no enemies are nearby

		// if Essentials teleport handling is enabled and available, pass the teleport off to it (for delay and cooldown)
		if (EssentialsFeatures.handleTeleport(me, myFaction.getHome())) return;

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostHome, "to teleport to your faction home", "for teleporting to your faction home")) return;

		// Create a smoke effect
		if (Conf.homesTeleportCommandSmokeEffectEnabled)
		{
			List<Location> smokeLocations = new ArrayList<Location>();
			smokeLocations.add(loc);
			smokeLocations.add(loc.add(0, 1, 0));
			smokeLocations.add(myFaction.getHome());
			smokeLocations.add(myFaction.getHome().clone().add(0, 1, 0));
			SmokeUtil.spawnCloudRandom(smokeLocations, Conf.homesTeleportCommandSmokeEffectThickness);
		}
		
		me.teleport(myFaction.getHome());
	}
	
	public boolean moveHome() {
		Location home = myFaction.getHome();
		while (home.getBlockY() < Conf.homesMustBeGreaterThan && home.getBlockY() < 256 && !checkIsValidHome(home)) {
			home = home.add(0, 1, 0);
		}
		
		if (home.getBlockY() == 256) {
			return false;
		}
		
		myFaction.setHome(home);
		return true;
	}
	
	public boolean checkIsValidHome(Location home) {
		if (home.getWorld().getBlockAt(home).getType() == Material.AIR && home.getWorld().getBlockAt(home.clone().add(0, 1, 0)).getType() == Material.AIR && home.getWorld().getBlockAt(home.clone().subtract(0, -1, 0)).getType() != Material.AIR)
			return true;
		return false;
	}
	
}