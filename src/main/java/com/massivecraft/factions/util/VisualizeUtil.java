package com.massivecraft.factions.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

// TODO: Only send blocks in visual range
// TODO: Only send blocks that where changed when clearing?
// TODO: Create packed queue to avoid freezes.

@SuppressWarnings("deprecation")
public class VisualizeUtil
{
	protected static Map<String, Set<Location>> playerLocations = new HashMap<String, Set<Location>>();

	public static Set<Location> getPlayerLocations(Player player)
	{
		return getPlayerLocations(player.getName());
	}

	public static Set<Location> getPlayerLocations(String playerName)
	{
		Set<Location> ret = playerLocations.get(playerName);
		if (ret == null)
		{
			ret = new HashSet<Location>();
			playerLocations.put(playerName, ret);
		}
		return ret;
	}

	// -------------------------------------------- //
	// SINGLE
	// -------------------------------------------- //

	public static void addLocation(Player player, Location location, Material type, byte data)
	{
		getPlayerLocations(player).add(location);
		player.sendBlockChange(location, type, data);
	}

	public static void addLocation(Player player, Location location, Material type)
	{
		getPlayerLocations(player).add(location);
		player.sendBlockChange(location, type, (byte) 0);
	}

	// -------------------------------------------- //
	// CLEAR
	// -------------------------------------------- //

	public static void clear(Player player)
	{
		Set<Location> locations = getPlayerLocations(player);
		if (locations == null)
			return;
		for (Location location : locations)
		{
			Block block = location.getWorld().getBlockAt(location);
			player.sendBlockChange(location, block.getType(), block.getData());
		}
		locations.clear();
	}
}