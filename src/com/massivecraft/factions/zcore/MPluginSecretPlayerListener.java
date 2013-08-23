package com.massivecraft.factions.zcore;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import com.massivecraft.factions.zcore.persist.EM;
import com.massivecraft.factions.zcore.persist.Entity;
import com.massivecraft.factions.zcore.persist.EntityCollection;
import com.massivecraft.factions.zcore.persist.PlayerEntityCollection;

public class MPluginSecretPlayerListener implements Listener
{
	private MPlugin p;

	public MPluginSecretPlayerListener(MPlugin p)
	{
		this.p = p;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		if (event.isCancelled())
			return;

		if (p.handleCommand(event.getPlayer(), event.getMessage()))
		{
			if (p.logPlayerCommands())
				p.log(event.getPlayer().getName() + " issued command: " + event.getMessage());
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		if (event.isCancelled())
			return;

		if (p.handleCommand(event.getPlayer(), event.getMessage()))
		{
			if (p.logPlayerCommands())
				p.log(event.getPlayer().getName() + " issued command: " + event.getMessage());
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerPreLogin(PlayerLoginEvent event)
	{
		for (EntityCollection<? extends Entity> ecoll : EM.class2Entities.values())
		{
			if (ecoll instanceof PlayerEntityCollection)
			{
				ecoll.get(event.getPlayer().getName());
			}
		}
	}
}
