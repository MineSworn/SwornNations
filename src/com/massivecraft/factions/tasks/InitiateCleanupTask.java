package com.massivecraft.factions.tasks;

import java.util.Iterator;

import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class InitiateCleanupTask extends BukkitRunnable
{
	@Override
	public void run()
	{
		Iterator<Faction> factions = Factions.i.get().iterator();
		while (factions.hasNext())
		{
			Faction f = factions.next();
			Iterator<FPlayer> fplayers = f.getFPlayers().iterator();
			while (fplayers.hasNext())
			{
				FPlayer fp = fplayers.next();
				if (fp.getTitle().contains("Initiate"))
				{
					fp.setTitle("");
				}
			}
		}
	}
}
