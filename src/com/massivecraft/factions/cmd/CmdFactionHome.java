package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;

import org.bukkit.Location;
 
public class CmdFactionHome extends FCommand
{
	public CmdFactionHome()
	{
		this.aliases.add("factionhome");
		this.requiredArgs.add("tag");
		this.permission = Permission.FACTION_HOME.node;
		this.disableOnLock = false;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = false;
 
		this.setHelpShort("teleport to another Faction's home");
	}
	
	@Override
	public void perform()
	{
		Faction targetFaction = this.argAsFaction(0);
		if (targetFaction == null) 
			return;
    		
		if (targetFaction.hasHome()) 
		{
			Location FactionHome = targetFaction.getHome();
			me.teleport(FactionHome);
			fme.msg("<i>You have been teleported to the Faction home of %s<i>.", targetFaction.describeTo(fme));
		} 
		else 
		{
			fme.msg("<b>That faction doesn't have a home!");
		}
	}
}