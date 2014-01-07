package com.massivecraft.factions.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.Board;

public class AutoCleanupTask extends BukkitRunnable
{
	@Override
	public void run()
	{
		Board.autoCleanupClaimsRoutine();
	}
}