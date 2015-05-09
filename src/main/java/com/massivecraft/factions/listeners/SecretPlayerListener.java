package com.massivecraft.factions.listeners;

import lombok.AllArgsConstructor;
import net.dmulloy2.swornnations.SwornNations;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import com.massivecraft.factions.persist.EM;
import com.massivecraft.factions.persist.EntityCollection;
import com.massivecraft.factions.persist.PlayerEntityCollection;

@AllArgsConstructor
public class SecretPlayerListener implements Listener
{
	private final SwornNations plugin;

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		if (plugin.handleCommand(event.getPlayer(), event.getMessage()))
		{
			if (plugin.logPlayerCommands())
				plugin.getServer().getLogger().info(event.getPlayer().getName() + " issued server command: " + event.getMessage());
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		if (plugin.handleCommand(event.getPlayer(), event.getMessage()))
		{
			if (plugin.logPlayerCommands())
				plugin.getServer().getLogger().info(event.getPlayer().getName() + " issued server command: " + event.getMessage());
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		for (EntityCollection<?> ecoll : EM.class2Entities.values())
		{
			if (ecoll instanceof PlayerEntityCollection)
				ecoll.get(event.getPlayer().getUniqueId().toString());
		}
	}
}