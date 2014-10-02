package com.massivecraft.factions.tasks;

import net.dmulloy2.swornnations.SwornNations;

import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayers;

public class AutoLeaveTask extends BukkitRunnable
{
	private final double rate;

	public AutoLeaveTask()
	{
		this.rate = Conf.autoLeaveRoutineRunsEveryXMinutes;
	}

	@Override
	public void run()
	{
		FPlayers.i.autoLeaveOnInactivityRoutine();

		// maybe setting has been changed? if so, restart task at new rate
		if (this.rate != Conf.autoLeaveRoutineRunsEveryXMinutes)
			SwornNations.get().startAutoLeaveTask(true);
	}
}