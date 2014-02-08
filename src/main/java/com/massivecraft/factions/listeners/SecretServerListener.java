package com.massivecraft.factions.listeners;

import net.dmulloy2.swornnations.SwornNations;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

public class SecretServerListener implements Listener
{
	@EventHandler(priority = EventPriority.LOWEST)
	public void onServerCommand(ServerCommandEvent event)
	{
		if (event.getCommand().length() == 0)
			return;

		if (SwornNations.get().handleCommand(event.getSender(), event.getCommand()))
		{
			event.setCommand(SwornNations.get().refCommand);
		}
	}

}
