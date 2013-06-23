package com.massivecraft.factions.util;

import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;

public class AutoLeaveTask extends BukkitRunnable
{
	double rate;
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
			P.p.startAutoLeaveTask(true);
	}
}