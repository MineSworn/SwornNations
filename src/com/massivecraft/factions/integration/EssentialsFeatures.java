package com.massivecraft.factions.integration;

import net.ess3.api.IEssentials;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.Teleport;
import com.earth2me.essentials.Trade;
import com.massivecraft.factions.Conf;

/*
 * This Essentials integration handler is for Teleport Handling
 */

// Silence deprecation warnings with this old interface
@SuppressWarnings("deprecation")
public class EssentialsFeatures
{
	private static IEssentials essentials;

	public static void setup()
	{
		// Integrate main essentials plugin
		// TODO: This is the old Essentials method not supported in 3.0... probably needs to eventually be moved to EssentialsOldVersionFeatures and new method implemented
		if (essentials == null)
		{
			Plugin ess = Bukkit.getPluginManager().getPlugin("Essentials");
			if (ess != null && ess.isEnabled())
				essentials = (IEssentials)ess;
		}
	}

	// Return false if feature is disabled or Essentials isn't available
	public static boolean handleTeleport(Player player, Location loc)
	{
		if ( ! Conf.homesTeleportCommandEssentialsIntegration || essentials == null) return false;

		Teleport teleport = (Teleport) essentials.getUser(player).getTeleport();
		Trade trade = new Trade(Conf.econCostHome, essentials);
		try
		{
			teleport.teleport(loc, trade);
		}
		catch (Exception e)
		{
			player.sendMessage(ChatColor.RED + "" + e.getMessage());
			return false;
		}
		
		return true;
	}
}