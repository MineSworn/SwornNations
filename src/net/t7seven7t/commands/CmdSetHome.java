/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Role;

/**
 * @author t7seven7t
 */
public class CmdSetHome implements CommandExecutor {

	private FPlayer fme;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player))
			return true;
		
		fme = FPlayers.i.get((Player) sender);
		if (! fme.hasFaction()) {
			fme.msg("<b>You must have a faction to do this!");
			return true;
		}
		
		// Just go ahead and override
		if (Conf.homeBalanceOverride) {
			fme.setHome(fme.getPlayer().getLocation());
			fme.msg("<i>Home set!");
			return true;
		}
		
		if (Board.getFactionAt(new FLocation(fme)) != fme.getFaction()) {
			fme.msg("<b>Sorry, your faction home can only be set inside your own claimed territory.");
			return true;
		}
		
		if (fme.getFaction().hasHome()) {
			FLocation fHome = new FLocation(fme.getFaction().getHome());
			FLocation loc = new FLocation(fme.getPlayer().getLocation());
			if (fHome.getDistanceTo(loc) > 20.0 || Board.getAbsoluteFactionAt(loc) != fme.getFaction()) {
				fme.msg("<b>You're too far away from your faction home to set you're home.");
				return true;
			}
		} else {
			fme.msg("<b>Please set a faction home first. " + (fme.getRole().value < Role.MODERATOR.value ? "<i> Ask your leader to:" : "<i>You should:"));
			fme.sendMessage(P.p.cmdBase.cmdSethome.getUseageTemplate());
			return true;
		}
		
		fme.setHome(fme.getPlayer().getLocation());
		fme.msg("<i>Home set!");
		return true;
	}

}
