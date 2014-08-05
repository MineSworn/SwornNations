package com.massivecraft.factions.integration;

import java.util.logging.Level;

import net.dmulloy2.swornnations.SwornNations;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Teleport;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.massivecraft.factions.Conf;

/**
 * This interface handles Essentials Teleportation
 */
public class EssentialsFeatures
{
	private static Essentials essentials;

	/**
	 * Integrate main Essentials plugin
	 */
	public static void setup()
	{
		try
		{
			PluginManager pm = Bukkit.getPluginManager();
			if (pm.isPluginEnabled("Essentials"))
			{
				Plugin essPlugin = pm.getPlugin("Essentials");
				essentials = (Essentials) essPlugin;

				SwornNations.get().log("Essentials integration successful!");
			}
		} catch (Throwable ex) { }
	}

	/**
	 * Handles Essentials Teleportation
	 *
	 * @param player {@link Player} to teleport
	 * @param loc {@link Location} to teleport to
	 * @return Whether or not the teleportation was successful
	 */
	public static boolean handleTeleport(Player player, Location loc)
	{
		try
		{
			if (! Conf.homesTeleportCommandEssentialsIntegration || essentials == null)
				return false;

			User user = essentials.getUser(player);
			Teleport teleport = user.getTeleport();
			Trade chargeFor = new Trade("fhome", essentials);

			teleport.teleport(loc, chargeFor, TeleportCause.COMMAND);
			return true;
		}
		catch (Throwable ex)
		{
			String message = ex.getMessage();
			if (message.contains("Time before next teleport"))
			{
				player.sendMessage(FormatUtil.format("&cError: &4{0}", message));
				return true;
			}

			SwornNations.get().log(Level.WARNING, Util.getUsefulStack(ex, "teleporting " + player.getName() + " with Essentials"));
			player.sendMessage(ChatColor.RED + "Failed to teleport with Essentials: " + ex);
			return false;
		}
	}
}