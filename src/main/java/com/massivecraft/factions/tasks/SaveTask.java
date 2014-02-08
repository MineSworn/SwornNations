package com.massivecraft.factions.tasks;

import lombok.AllArgsConstructor;
import net.dmulloy2.swornnations.SwornNations;

import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.persist.EM;

@AllArgsConstructor
public class SaveTask extends BukkitRunnable
{
	private SwornNations plugin;

	@Override
	public void run()
	{
		if (! plugin.getAutoSave())
			return;

		EM.saveAllToDisc();
	}
}