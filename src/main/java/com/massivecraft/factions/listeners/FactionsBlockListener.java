package com.massivecraft.factions.listeners;

import me.t7seven7t.factions.util.MyMaterial;
import me.t7seven7t.swornnations.npermissions.NPermission;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;

public class FactionsBlockListener implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (event.isCancelled() || ! event.canBuild())
			return;

		// special case for flint&steel, which should only be prevented by
		// DenyUsage list
		if (event.getBlockPlaced().getType() == Material.FIRE)
			return;

		if (! playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), "build", false, event.getBlock().getType()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (event.isCancelled())
			return;

		if (! playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), "destroy", false, event.getBlock().getType()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockDamage(BlockDamageEvent event)
	{
		if (event.isCancelled())
			return;

		if (event.getInstaBreak()
				&& ! playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), "destroy", false, event.getBlock()
						.getType()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonExtend(BlockPistonExtendEvent event)
	{
		if (event.isCancelled() || ! Conf.pistonProtectionThroughDenyBuild)
			return;

		Faction pistonFaction = Board.getFactionAt(new FLocation(event.getBlock()));

		// target end-of-the-line empty (air) block which is being pushed into,
		// including if piston itself would extend into air
		Block targetBlock = event.getBlock().getRelative(event.getDirection(), event.getLength() + 1);

		// if potentially pushing into air in another territory, we need to
		// check it out
		if (targetBlock.isEmpty() && ! canPistonMoveBlock(pistonFaction, targetBlock.getLocation()))
		{
			event.setCancelled(true);
			return;
		}

		/*
		 * note that I originally was testing the territory of each affected
		 * block, but since I found that pistons can only push up to 12 blocks
		 * and the width of any territory is 16 blocks, it should be safe (and
		 * much more lightweight) to test only the final target block as done
		 * above
		 */
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonRetract(BlockPistonRetractEvent event)
	{
		// if not a sticky piston, retraction should be fine
		if (event.isCancelled() || !event.isSticky() || !Conf.pistonProtectionThroughDenyBuild)
		{
			return;
		}

		Location targetLoc = event.getRetractLocation();

		// if potentially retracted block is just air, no worries
		if (targetLoc.getBlock().isEmpty())
		{
			return;
		}

		Faction pistonFaction = Board.getFactionAt(new FLocation(event.getBlock()));

		if (!canPistonMoveBlock(pistonFaction, targetLoc))
		{
			event.setCancelled(true);
			return;
		}
	}

	private boolean canPistonMoveBlock(Faction pistonFaction, Location target)
	{
		Faction otherFaction = Board.getFactionAt(new FLocation(target));

		if (pistonFaction == otherFaction)
			return true;

		if (otherFaction.isNone())
		{
			if (!Conf.wildernessDenyBuild || Conf.worldsNoWildernessProtection.contains(target.getWorld().getName()))
				return true;

			return false;
		}
		else if (otherFaction.isSafeZone())
		{
			if (!Conf.safeZoneDenyBuild)
				return true;

			return false;
		}
		else if (otherFaction.isWarZone())
		{
			if (!Conf.warZoneDenyBuild)
				return true;

			return false;
		}

		Relation rel = pistonFaction.getRelationTo(otherFaction);

		if (rel.confDenyBuild(otherFaction.hasPlayersOnline()))
			return false;

		return true;
	}

	public static boolean playerCanBuildDestroyBlock(Player player, Location location, String action, boolean justCheck, Material mat)
	{
		String name = player.getName();
		if (Conf.playersWhoBypassAllProtection.contains(name))
			return true;

		FPlayer me = FPlayers.i.get(name);
		if (me.isAdminBypassing())
			return true;

		FLocation loc = new FLocation(location);
		Faction otherFaction = Board.getFactionAt(loc);
		Faction absoluteFaction = Board.getAbsoluteFactionAt(new FLocation(location));

		if (otherFaction.isNone())
		{
			if (! Conf.wildernessDenyBuild || Conf.worldsNoWildernessProtection.contains(location.getWorld().getName()))
			{
				if (mat != Material.TNT || (mat == Material.TNT && absoluteFaction.isNone()))
					return true; // This is not faction territory. Use whatever you like here.
			}

			if (! justCheck)
				me.msg("<b>You can't %s in %s<b>", action, Factions.i.getNone().getTag(me));

			return false;
		}
		else if (otherFaction.isSafeZone())
		{
			if (! Conf.safeZoneDenyBuild || Permission.MANAGE_SAFE_ZONE.has(player))
				return true;

			if (! justCheck)
				me.msg("<b>You can't %s in %s<b>.", action, Factions.i.getSafeZone().getTag(me));

			return false;
		}
		else if (otherFaction.isWarZone())
		{
			if (! Conf.warZoneDenyBuild || Permission.MANAGE_WAR_ZONE.has(player))
				return true;

			if (! justCheck)
				me.msg("<b>You can't %s in %s<b>.", action, Factions.i.getWarZone().getTag(me));

			return false;
		}

		Faction myFaction = me.getFaction();
		Relation rel = myFaction.getRelationTo(otherFaction);
		boolean online = otherFaction.hasPlayersOnline();
		boolean pain = !justCheck && rel.confPainBuild(online);
		boolean deny = rel.confDenyBuild(online);

		// hurt the player for building/destroying in other territory?
		if (pain)
		{
			player.damage(Conf.actionDeniedPainAmount);

			if (!deny)
				me.msg("<b>It is painful to try to " + action + " in the territory of " + otherFaction.getTag(myFaction));
		}

		// cancel building/destroying in other territory?
		if (deny)
		{
			if (!justCheck)
				me.msg("<b>You can't " + action + " in the territory of " + otherFaction.getTag(myFaction));

			return false;
		}

		if (action.equalsIgnoreCase("destroy"))
		{
			if (!otherFaction.playerHasPermission(me, NPermission.BREAK))
			{
				me.msg("<b>You can't " + action + " in the territory of " + otherFaction.getTag(myFaction));
				return false;
			}
		}
		else if (action.equalsIgnoreCase("build"))
		{
			if (!otherFaction.playerHasPermission(me, NPermission.BUILD))
			{
				me.msg("<b>You can't " + action + " in the territory of " + otherFaction.getTag(myFaction));
				return false;
			}
		}

		if (mat.equals(Material.TNT) && me.getFaction().getRelationTo(absoluteFaction).confDenyBuild(otherFaction.hasPlayersOnline()))
		{
			me.msg("<b>You can't place tnt under, in, or over the territory of " + otherFaction.getTag(me.getFaction()));
			return false;
		}

		// Also cancel and/or cause pain if player doesn't have ownership rights
		// for this claim
		if (Conf.ownedAreasEnabled && (Conf.ownedAreaDenyBuild || Conf.ownedAreaPainBuild) && !otherFaction.playerHasOwnershipRights(me, loc))
		{
			if (!pain && Conf.ownedAreaPainBuild && !justCheck)
			{
				player.damage(Conf.actionDeniedPainAmount);

				if (!Conf.ownedAreaDenyBuild)
					me.msg("<b>It is painful to try to " + action + " in this territory, it is owned by: "
							+ otherFaction.getOwnerListString(loc));
			}
			if (Conf.ownedAreaDenyBuild)
			{
				if (!justCheck)
					me.msg("<b>You can't " + action + " in this territory, it is owned by: " + otherFaction.getOwnerListString(loc));

				return false;
			}
		}

		return true;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInitiatePlace(BlockPlaceEvent event)
	{
		if (event.isCancelled())
			return;

		FPlayer fplayer = FPlayers.i.get(event.getPlayer());
		if (fplayer == null)
			return;

		if (fplayer.hasFaction())
		{
			Material mat = event.getBlock().getType();
			if (mat == Material.TNT)
			{
				if (fplayer.getRole() == Role.INITIATE)
				{
					fplayer.msg("<i>You cannot place TNT as an initiate!");
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockedItemPlace(BlockPlaceEvent event)
	{
		if (event.isCancelled())
			return;

		FPlayer fplayer = FPlayers.i.get(event.getPlayer());
		if (fplayer == null)
			return;

		FLocation floc = new FLocation(event.getBlock());
		Faction fac = Board.getFactionAt(floc);
		for (MyMaterial blockedMaterial : Conf.ownTerritoryOnlyMaterials)
		{
			if (event.getBlock().getType() == blockedMaterial.getType())
			{
				if (!canPlaceBlockedItemHere(floc, fac, fplayer, false))
				{
					fplayer.msg("<i>You cannot place this item outside your own territory!");
					event.setCancelled(true);
				}
			}
		}

		for (MyMaterial blockedMaterial : Conf.ownTerritoryAndWildernessMaterials)
		{
			if (event.getBlock().getType() == blockedMaterial.getType())
			{
				if (!canPlaceBlockedItemHere(floc, fac, fplayer, true))
				{
					fplayer.msg("<i>You cannot place this item outside your own territory or wilderness!");
					event.setCancelled(true);
				}
			}
		}
	}

	public boolean canPlaceBlockedItemHere(FLocation floc, Faction fac, FPlayer pl, boolean both)
	{
		if (fac.isSafeZone() || fac.isWarZone())
			return false;

		if (floc.getY() <= 45)
			return true;

		return (both ? (fac == pl.getFaction() || fac.isNone()) : (fac == pl.getFaction()));
	}
}