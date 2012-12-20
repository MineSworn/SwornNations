package com.massivecraft.factions.util;

import com.massivecraft.factions.Board;

public class AutoCleanupTask implements Runnable {

	public void run() {
		Board.autoCleanupClaimsRoutine();
	}
	
}
