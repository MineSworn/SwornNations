package com.massivecraft.factions.util;

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