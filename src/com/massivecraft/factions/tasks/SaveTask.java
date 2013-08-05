package com.massivecraft.factions.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.zcore.MPlugin;
import com.massivecraft.factions.zcore.persist.EM;

public class SaveTask extends BukkitRunnable
{
	private MPlugin p;
	public SaveTask(MPlugin p)
	{
		this.p = p;
	}
	
	public void run()
	{
		if ( ! p.getAutoSave()) return;
		p.preAutoSave();
		EM.saveAllToDisc();
		p.postAutoSave();
	}
}
