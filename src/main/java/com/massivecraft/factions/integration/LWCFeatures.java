package com.massivecraft.factions.integration;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

@SuppressWarnings("unused")
public class LWCFeatures
{
//	private static LWC lwc;

	public static void setup()
	{
//		Plugin plugin = Bukkit.getPluginManager().getPlugin("LWC");
//		if (plugin == null || ! plugin.isEnabled())
//			return;
//
//		lwc = ((LWCPlugin) plugin).getLWC();
//		SwornNations.get().log(
//				"Successfully hooked into LWC!"
//						+ (Conf.lwcIntegration ? "" : " Integration is currently disabled, though (\"lwcIntegration\")."));
	}

	public static boolean isEnabled()
	{
		return false;
//		return Conf.lwcIntegration && lwc != null;
	}

	public static void clearOtherChests(FLocation flocation, Faction faction)
	{
//		World world = Bukkit.getWorld(flocation.getWorldName());
//		if (world == null)
//			return; // world not loaded or something? cancel out to prevent
//					// error
//
//		Location location = new Location(world, flocation.getX() * 16, 5, flocation.getZ() * 16);
//		Chunk chunk = location.getChunk();
//		BlockState[] blocks = chunk.getTileEntities();
//		List<Block> chests = new LinkedList<Block>();
//
//		for (BlockState state : blocks)
//		{
//			if (state.getType() == Material.CHEST)
//				chests.add(state.getBlock());
//		}
//
//		for (Block chest : chests)
//		{
//			Protection prot = lwc.findProtection(chest);
//			if (prot != null)
//			{
//				boolean found = false;
//				for (FPlayer fplayer : faction.getFPlayers())
//					if (fplayer.getName().equals(prot.getOwner()))
//						found = true;
//				if (! found)
//					prot.remove();
//			}
//		}
	}

	public static void clearAllChests(FLocation flocation)
	{
//		World world = Bukkit.getWorld(flocation.getWorldName());
//		if (world == null)
//			return; // world not loaded or something? cancel out to prevent error
//
//		Location location = new Location(world, flocation.getX() * 16, 5, flocation.getZ() * 16);
//		Chunk chunk = location.getChunk();
//		BlockState[] blocks = chunk.getTileEntities();
//		List<Block> chests = new LinkedList<Block>();
//
//		for (BlockState state : blocks)
//		{
//			if (state.getType() == Material.CHEST)
//				chests.add(state.getBlock());
//		}
//
//		for (Block chest : chests)
//		{
//			Protection prot = lwc.findProtection(chest);
//			if (prot != null)
//				prot.remove();
//		}
	}
}