package com.massivecraft.factions.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.integration.SpoutFeatures;

public class FactionsServerListener implements Listener
{	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginDisable(PluginDisableEvent event)
	{
		String name = event.getPlugin().getDescription().getName();
		if (name.equals("Spout"))
		{
			SpoutFeatures.setAvailable(false, "");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginEnable(PluginEnableEvent event)
	{
		Plugin plug = event.getPlugin();
		String name = plug.getDescription().getName();
		if (name.equals("Spout"))
		{
			SpoutFeatures.setAvailable(true, plug.getDescription().getFullName());
		}
	}
}