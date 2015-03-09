package com.massivecraft.factions.listeners;

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
import com.massivecraft.factions.types.Permission;

public class FactionsFallbackListener implements Listener
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

		if (event.getInstaBreak() && ! playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), "destroy", false,
				event.getBlock().getType()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonExtend(BlockPistonExtendEvent event)
	{
		if (event.isCancelled() || ! Conf.pistonProtectionThroughDenyBuild)
			return;

		String pistonFaction = Board.getIdAt(new FLocation(event.getBlock()));

		// target end-of-the-line empty (air) block which is being pushed into,
		// including if piston itself would extend into air
		@SuppressWarnings("deprecation") // TODO: Find a replacement for this.
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
		if (event.isCancelled() || ! event.isSticky() || ! Conf.pistonProtectionThroughDenyBuild)
			return;

		// This is just a rehash of the deprecated method
		// TODO: Figure out a better solution
		Block targetBlock = event.getBlock().getRelative(event.getDirection(), 2);
		if (targetBlock.isEmpty())
			return;

		String pistonFaction = Board.getIdAt(new FLocation(event.getBlock()));
		if (! canPistonMoveBlock(pistonFaction, targetBlock.getLocation()))
			event.setCancelled(true);
	}

	private boolean canPistonMoveBlock(String pistonFaction, Location target)
	{
		String otherFaction = Board.getIdAt(new FLocation(target));
		if (pistonFaction.equals(otherFaction))
			return true;

		if (otherFaction.equals("0"))
		{
			return ! Conf.wildernessDenyBuild || Conf.worldsNoWildernessProtection.contains(target.getWorld().getName());
		}
		else if (otherFaction.equals("-1"))
		{
			return ! Conf.safeZoneDenyBuild;
		}
		else if (otherFaction.equals("-2"))
		{
			return ! Conf.warZoneDenyBuild;
		}

		return false;
	}

	public static boolean playerCanBuildDestroyBlock(Player player, Location location, String action, boolean justCheck, Material mat)
	{
		String name = player.getName();
		if (Conf.playersWhoBypassAllProtection.contains(name))
			return true;

		FLocation loc = new FLocation(location);
		String otherFaction = Board.getIdAt(loc);
		String absoluteFaction = Board.getIdAt(new FLocation(location));

		if (otherFaction.equals("0"))
		{
			if (! Conf.wildernessDenyBuild || Conf.worldsNoWildernessProtection.contains(location.getWorld().getName()))
			{
				if (mat != Material.TNT || (mat == Material.TNT && absoluteFaction.equals("0")))
					return true; // This is not faction territory. Use whatever you like here.
			}

			if (! justCheck)
				player.sendMessage("You can't " + action + " in " + player.getName());

			return false;
		}
		else if (otherFaction.equals("-1"))
		{
			if (! Conf.safeZoneDenyBuild || Permission.MANAGE_SAFE_ZONE.has(player))
				return true;

			if (! justCheck)
				player.sendMessage("You can't " + action + " in SafeZone.");

			return false;
		}
		else if (otherFaction.equals("-2"))
		{
			if (! Conf.warZoneDenyBuild || Permission.MANAGE_WAR_ZONE.has(player))
				return true;

			if (! justCheck)
				player.sendMessage("You can't " + action + " in WarZone.");

			return false;
		}

		player.sendMessage("SwornNations is disabled. You cannot " + action + " here.");
		return true;
	}
}