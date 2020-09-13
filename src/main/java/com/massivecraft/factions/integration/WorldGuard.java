package com.massivecraft.factions.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.dmulloy2.swornnations.SwornNations;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * WorldGuard region checking
 */

public class WorldGuard
{
	private static WorldGuardPlugin wg;
	private static boolean enabled = false;

	public static void init(Plugin plugin)
	{
		Plugin wgplug = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		if (!(wgplug instanceof WorldGuardPlugin))
		{
			enabled = false;
			wg = null;
			SwornNations.get().log("Could not hook to WorldGuard. WorldGuard checks are disabled.");
		}
		else
		{
			wg = (WorldGuardPlugin) wgplug;
			enabled = true;
			SwornNations.get().log("Successfully hooked to WorldGuard.");
		}
	}

	public static boolean isEnabled()
	{
		return enabled;
	}

	// PVP Flag check
	// Returns:
	// True: PVP is allowed
	// False: PVP is disallowed
	@SuppressWarnings("deprecation")
	public static boolean isPVP(Player player)
	{
		if (! enabled)
		{
			// No WG hooks so we'll always bypass this check.
			return true;
		}

		Location loc = player.getLocation();
		World world = loc.getWorld();
		BlockVector3 pt = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());

		WorldGuardPlatform platform = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform();
		RegionManager regionManager = platform.getRegionContainer().get(BukkitAdapter.adapt(world));
		ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

		return set.queryValue(wg.wrapPlayer(player), Flags.PVP) == StateFlag.State.ALLOW;
	}

	// Check for Regions in chunk the chunk
	// Returns:
	// True: Regions found within chunk
	// False: No regions found within chunk
	public static boolean checkForRegionsInChunk(Location loc)
	{
		if (! enabled)
		{
			// No WG hooks so we'll always bypass this check.
			return false;
		}

		World world = loc.getWorld();
		Chunk chunk = world.getChunkAt(loc);
		int minChunkX = chunk.getX() << 4;
		int minChunkZ = chunk.getZ() << 4;
		int maxChunkX = minChunkX + 15;
		int maxChunkZ = minChunkZ + 15;

		int worldHeight = world.getMaxHeight(); // Allow for heights other than default

		BlockVector3 minChunk = BlockVector3.at(minChunkX, 0, minChunkZ);
		BlockVector3 maxChunk = BlockVector3.at(maxChunkX, worldHeight, maxChunkZ);

		WorldGuardPlatform platform = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform();
		RegionManager regionManager = platform.getRegionContainer().get(BukkitAdapter.adapt(world));

		ProtectedCuboidRegion region = new ProtectedCuboidRegion("wgfactionoverlapcheck", minChunk, maxChunk);
		Map<String, ProtectedRegion> allregions = regionManager.getRegions();
		List<ProtectedRegion> allregionslist = new ArrayList<ProtectedRegion>(allregions.values());
		List<ProtectedRegion> overlaps;
		boolean foundregions = false;

		try
		{
			overlaps = region.getIntersectingRegions(allregionslist);
			if (overlaps != null && !overlaps.isEmpty())
			{
				foundregions = true;
			}
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
		}

		return foundregions;
	}
}