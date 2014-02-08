package com.massivecraft.factions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import net.dmulloy2.swornnations.SwornNations;

import org.bukkit.ChatColor;

import com.massivecraft.factions.integration.LWCFeatures;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.AsciiCompass;

public class Board
{
	private static transient ConcurrentHashMap<FLocation, String> flocationIds = new ConcurrentHashMap<FLocation, String>();
	private static transient ConcurrentHashMap<FLocation, Long> flocationClaimTimes = new ConcurrentHashMap<FLocation, Long>();
	private static Map<String, Map<String, String>> worldCoordIds;
	private static Map<String, String> flocationClaimSaves;

	// ----------------------------------------------//
	// Get and Set
	// ----------------------------------------------//
	public static String getIdAt(FLocation flocation)
	{
		if (!flocationIds.containsKey(flocation))
		{
			return "0";
		}

		return flocationIds.get(flocation);
	}

	public static Faction getFactionAt(FLocation flocation)
	{
		if (flocation.getY() < Conf.territoryProtectMinimumHeight || flocation.getY() > Conf.territoryProtectMaximumHeight)
		{
			if (getAbsoluteFactionAt(flocation).isWarZone() || getAbsoluteFactionAt(flocation).isSafeZone()
					|| getAbsoluteFactionAt(flocation).isPeaceful())
				return getAbsoluteFactionAt(flocation);

			return Factions.i.getNone();
		}

		return Factions.i.get(getIdAt(flocation));
	}

	public static Faction getAbsoluteFactionAt(FLocation flocation)
	{
		return Factions.i.get(getIdAt(flocation));
	}

	public static void setIdAt(String id, FLocation flocation)
	{
		clearOwnershipAt(flocation);

		if (id == "0")
		{
			removeAt(flocation);
		}

		flocationClaimTimes.put(flocation, System.currentTimeMillis());
		flocationIds.put(flocation, id);
	}

	public static void setFactionAt(Faction faction, FLocation flocation)
	{
		setIdAt(faction.getId(), flocation);
	}

	public static void removeAt(FLocation flocation)
	{
		clearOwnershipAt(flocation);
		flocationClaimTimes.remove(flocation);
		flocationIds.remove(flocation);
	}

	// not to be confused with claims, ownership referring to further
	// member-specific ownership of a claim
	public static void clearOwnershipAt(FLocation flocation)
	{
		Faction faction = getAbsoluteFactionAt(flocation);
		if (faction != null && faction.isNormal())
		{
			faction.clearClaimOwnership(flocation);
		}
	}

	public static void unclaimAll(String factionId)
	{
		Faction faction = Factions.i.get(factionId);
		if (faction != null && faction.isNormal())
		{
			faction.clearAllClaimOwnership();
		}

		Iterator<Entry<FLocation, String>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext())
		{
			Entry<FLocation, String> entry = iter.next();
			if (entry.getValue().equals(factionId))
			{
				if (Conf.onUnclaimResetLwcLocks && LWCFeatures.isEnabled())
				{
					LWCFeatures.clearAllChests(entry.getKey());
				}

				iter.remove();
			}
		}
	}

	// Is this coord NOT completely surrounded by coords claimed by the same
	// faction?
	// Simpler: Is there any nearby coord with a faction other than the faction
	// here?
	public static boolean isBorderLocation(FLocation flocation)
	{
		Faction faction = getFactionAt(flocation);
		FLocation a = flocation.getRelative(1, 0);
		FLocation b = flocation.getRelative(-1, 0);
		FLocation c = flocation.getRelative(0, 1);
		FLocation d = flocation.getRelative(0, -1);
		return faction != getAbsoluteFactionAt(a) || faction != getAbsoluteFactionAt(b) || faction != getAbsoluteFactionAt(c)
				|| faction != getAbsoluteFactionAt(d);
	}

	// Is this coord connected to any coord claimed by the specified faction?
	public static boolean isConnectedLocation(FLocation flocation, Faction faction)
	{
		FLocation a = flocation.getRelative(1, 0);
		FLocation b = flocation.getRelative(-1, 0);
		FLocation c = flocation.getRelative(0, 1);
		FLocation d = flocation.getRelative(0, -1);
		return faction == getAbsoluteFactionAt(a) || faction == getAbsoluteFactionAt(b) || faction == getAbsoluteFactionAt(c)
				|| faction == getAbsoluteFactionAt(d);
	}

	// Cleanup faction claims which are not near home OR outpost
	public static void autoCleanupClaimsRoutine()
	{
		for (Entry<FLocation, Long> entry : flocationClaimTimes.entrySet())
		{
			Faction faction = getAbsoluteFactionAt(entry.getKey());
			if (faction.isNormal())
			{
				if ((entry.getValue() - System.currentTimeMillis()) < (long) (60 * 1000 * 60 * Conf.autoCleanupClaimsAfterXHours))
				{
					if (faction.hasHome() && faction.hasOutpost())
					{
						FLocation home = new FLocation(faction.getHome());
						FLocation outpost = new FLocation(faction.getOutpost());
						if ((home.getDistanceTo(entry.getKey()) > 20.0) && outpost.getDistanceTo(entry.getKey()) > 20.0)
						{
							removeAt(entry.getKey());
						}
					}

					if (faction.hasHome() && !faction.hasOutpost())
					{
						FLocation home = new FLocation(faction.getHome());
						if (home.getDistanceTo(entry.getKey()) > 20.0)
						{
							removeAt(entry.getKey());
						}
					}

					if (!faction.hasHome() && faction.hasOutpost())
					{
						FLocation outpost = new FLocation(faction.getOutpost());
						if (outpost.getDistanceTo(entry.getKey()) > 20.0)
						{
							removeAt(entry.getKey());
						}
					}
				}
			}
		}
	}

	public static int cleanupClaims()
	{
		int x = 0;
		for (Entry<FLocation, String> entry : flocationIds.entrySet())
		{
			Faction faction = getAbsoluteFactionAt(entry.getKey());
			if (faction.isNormal())
			{
				SwornNations.get().log("Faction is normal: " + faction.getTag());
				if (faction.hasHome() && faction.hasOutpost())
				{
					SwornNations.get().log("Faction has home and outpost");
					FLocation home = new FLocation(faction.getHome());
					FLocation outpost = new FLocation(faction.getOutpost());
					if ((home.getDistanceTo(entry.getKey()) > 20.0) && outpost.getDistanceTo(entry.getKey()) > 20.0)
					{
						removeAt(entry.getKey());
						x++;
					}
				}

				if (faction.hasHome() && !faction.hasOutpost())
				{
					SwornNations.get().log("Faction has home and no outpost");
					FLocation home = new FLocation(faction.getHome());
					if (home.getDistanceTo(entry.getKey()) > 20.0)
					{
						removeAt(entry.getKey());
						x++;
					}
				}

				if (!faction.hasHome() && faction.hasOutpost())
				{
					SwornNations.get().log("Faction has no home and an outpost");
					FLocation outpost = new FLocation(faction.getOutpost());
					if (outpost.getDistanceTo(entry.getKey()) > 20.0)
					{
						removeAt(entry.getKey());
						x++;
					}
				}
			}
		}
		return x;
	}

	// ----------------------------------------------//
	// Cleaner. Remove orphaned foreign keys
	// ----------------------------------------------//

	public static void clean()
	{
		Iterator<Entry<FLocation, String>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext())
		{
			Entry<FLocation, String> entry = iter.next();
			if (!Factions.i.exists(entry.getValue()))
			{
				if (Conf.onUnclaimResetLwcLocks && LWCFeatures.isEnabled())
				{
					LWCFeatures.clearAllChests(entry.getKey());
				}
				SwornNations.get().log("Board cleaner removed " + entry.getValue() + " from " + entry.getKey());
				iter.remove();
			}
		}
	}

	// ----------------------------------------------//
	// Coord count
	// ----------------------------------------------//

	public static int getFactionCoordCount(String factionId)
	{
		int ret = 0;
		for (String thatFactionId : flocationIds.values())
		{
			if (thatFactionId.equals(factionId))
			{
				ret += 1;
			}
		}
		return ret;
	}

	public static int getFactionCoordCount(Faction faction)
	{
		return getFactionCoordCount(faction.getId());
	}

	public static int getFactionCoordCountInWorld(Faction faction, String worldName)
	{
		String factionId = faction.getId();
		int ret = 0;
		Iterator<Entry<FLocation, String>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext())
		{
			Entry<FLocation, String> entry = iter.next();
			if (entry.getValue().equals(factionId) && entry.getKey().getWorldName().equals(worldName))
			{
				ret += 1;
			}
		}
		return ret;
	}

	// ----------------------------------------------//
	// Map generation
	// ----------------------------------------------//

	/**
	 * Alias for {@link Board#getMap(FPlayer, FLocation, double)}
	 */
	public static List<String> getMap(FPlayer fplayer)
	{
		FLocation flocation = new FLocation(fplayer);
		double inDegrees = fplayer.getPlayer().getLocation().getYaw();
		return getMap(fplayer, flocation, inDegrees);
	}

	/**
	 * The map is relative to a coord and a faction north is in the direction of
	 * decreasing x east is in the direction of decreasing z
	 */
	public static List<String> getMap(FPlayer fplayer, FLocation flocation, double inDegrees)
	{
		Faction faction = fplayer.getFaction();
		List<String> ret = new ArrayList<String>();
		Faction factionLoc = getAbsoluteFactionAt(flocation);
		ret.add(SwornNations.get().txt.titleize("(" + flocation.getCoordString() + ") " + factionLoc.getTag(faction)));

		int halfWidth = Conf.mapWidth / 2;
		int halfHeight = Conf.mapHeight / 2;
		FLocation topLeft = flocation.getRelative(-halfWidth, -halfHeight);
		int width = halfWidth * 2 + 1;
		int height = halfHeight * 2 + 1;

		if (Conf.showMapFactionKey)
		{
			height--;
		}

		Map<String, Character> fList = new HashMap<String, Character>();
		int chrIdx = 0;

		// For each row
		for (int dz = 0; dz < height; dz++)
		{
			// Draw and add that row
			String row = "";
			for (int dx = 0; dx < width; dx++)
			{
				if (dx == halfWidth && dz == halfHeight)
				{
					row += ChatColor.AQUA + "+";
				}
				else
				{
					FLocation flocationHere = topLeft.getRelative(dx, dz);
					Faction factionHere = getAbsoluteFactionAt(flocationHere);
					Relation relation = faction.getRelationTo(factionHere);
					if (factionHere.isNone())
					{
						row += ChatColor.GRAY + "-";
					}
					else if (factionHere.isSafeZone())
					{
						row += Conf.colorPeaceful + "+";
					}
					else if (factionHere.isWarZone())
					{
						row += ChatColor.DARK_RED + "+";
					}
					else if (factionHere == faction || factionHere == factionLoc || relation.isAtLeast(Relation.ALLY)
							|| (Conf.showNeutralFactionsOnMap && relation.equals(Relation.NEUTRAL))
							|| (Conf.showEnemyFactionsOnMap && relation.equals(Relation.ENEMY))
							|| fplayer.isAdminBypassing())
					{
						if (! fList.containsKey(factionHere.getTag()))
							fList.put(factionHere.getTag(), Conf.mapKeyChrs[chrIdx++]);

						char tag = fList.get(factionHere.getTag());
						row += factionHere.getColorTo(faction) + "" + tag;
					}
					else
					{
						row += ChatColor.GRAY + "-";
					}
				}
			}

			ret.add(row);
		}

		// Get the compass
		List<String> asciiCompass = AsciiCompass.getAsciiCompass(inDegrees, ChatColor.RED, SwornNations.get().txt.parse("<a>"));

		// Add the compass
		ret.set(1, asciiCompass.get(0) + ret.get(1).substring(3 * 3));
		ret.set(2, asciiCompass.get(1) + ret.get(2).substring(3 * 3));
		ret.set(3, asciiCompass.get(2) + ret.get(3).substring(3 * 3));

		// Add the faction key
		if (Conf.showMapFactionKey)
		{
			String fRow = "";
			for (String key : fList.keySet())
			{
				fRow += String.format("%s%s: %s ", ChatColor.GRAY, fList.get(key), key);
			}
			ret.add(fRow);
		}

		return ret;
	
	}

	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //

	public static Map<String, Map<String, String>> dumpAsSaveFormat()
	{
		worldCoordIds = new HashMap<String, Map<String, String>>();
		flocationClaimSaves = new HashMap<String, String>();

		for (Entry<FLocation, Long> entry : flocationClaimTimes.entrySet())
		{
			flocationClaimSaves.put(entry.getKey().getWorldName() + ":" + entry.getKey().getCoordString(), entry.getValue().toString());
		}

		String worldName, coords;
		String id;

		for (Entry<FLocation, String> entry : flocationIds.entrySet())
		{
			worldName = entry.getKey().getWorldName();
			coords = entry.getKey().getCoordString();
			id = entry.getValue();
			if (!worldCoordIds.containsKey(worldName))
			{
				worldCoordIds.put(worldName, new TreeMap<String, String>());
			}

			worldCoordIds.get(worldName).put(coords, id);
		}

		return worldCoordIds;
	}

	public static void loadFromSaveFormat(Map<String, Map<String, String>> worldCoordIds, Map<String, String> flocationClaimTs)
	{
		flocationIds.clear();
		flocationClaimTimes.clear();

		for (Entry<String, String> entry : flocationClaimTs.entrySet())
		{
			String[] ll = entry.getKey().split(":");
			String[] coords = ll[1].split(",");
			try
			{
				flocationClaimTimes.put(new FLocation(ll[0], Integer.parseInt(coords[0]), Integer.parseInt(coords[1])),
						Long.parseLong(entry.getValue()));
			}
			catch (NumberFormatException ex)
			{
				SwornNations.get().log("Could not load claim time data from board.json");
			}
		}

		String worldName;
		String[] coords;
		int x, z;
		String factionId;

		for (Entry<String, Map<String, String>> entry : worldCoordIds.entrySet())
		{
			worldName = entry.getKey();
			for (Entry<String, String> entry2 : entry.getValue().entrySet())
			{
				coords = entry2.getKey().trim().split("[,\\s]+");
				x = Integer.parseInt(coords[0]);
				z = Integer.parseInt(coords[1]);
				factionId = entry2.getValue();
				flocationIds.put(new FLocation(worldName, x, z), factionId);
			}
		}
	}

	public static transient Board i = new Board();

	public static boolean save()
	{
		SwornNations.get().log("Saving board to disk");
		dumpAsSaveFormat();
		SwornNations.get().persist.save(i);
		return true;
	}

	public static boolean load()
	{
		SwornNations.get().log("Loading board from disk");

		SwornNations.get().persist.loadOrSaveDefault(i, Board.class, "board");
		if (worldCoordIds != null && flocationClaimSaves != null)
			loadFromSaveFormat(worldCoordIds, flocationClaimSaves);

		return true;
	}
}