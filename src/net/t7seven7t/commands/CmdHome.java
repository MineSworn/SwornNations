/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.EssentialsFeatures;
import com.massivecraft.factions.struct.Relation;

/**
 * @author t7seven7t
 */
public class CmdHome implements CommandExecutor {

	private FPlayer fme;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player))
			return true;
		
		fme = FPlayers.i.get((Player) sender);
		if (!fme.hasHome()) {
			fme.msg("<b>You do not have a home set. Do /sethome to set one.");
			return true;
		}
		
		if (!fme.hasFaction()) {
			fme.msg("You must have a faction to have a home.");
			return true;
		}
		
		if (fme.getFaction().hasHome()) {
			FLocation fHome = new FLocation(fme.getFaction().getHome());
			FLocation home = new FLocation(fme.getHome());
			if (fHome.getDistanceTo(home) > 20.0 || Board.getAbsoluteFactionAt(home) != fme.getFaction()) {
				fme.msg("You're home was set too far away from your faction home, or outside of your territory and has become unset.");
				fme.removeHome();
				return true;
			}
		}
		
		if (isEnemyNearby(Board.getFactionAt(new FLocation(fme.getPlayer().getLocation())), fme.getPlayer().getLocation())) return true;
		
		if (EssentialsFeatures.handleTeleport(fme.getPlayer(), fme.getHome())) return true;
		
		fme.getPlayer().teleport(fme.getHome());
		
		return true;
	}
	
	public boolean isEnemyNearby(Faction faction, Location loc) {
		if (Conf.homesTeleportAllowedEnemyDistance > 0
				&& !faction.isSafeZone()
				&& (!fme.isInOwnTerritory() || (fme.isInOwnTerritory() && !Conf.homesTeleportIgnoreEnemiesIfInOwnTerritory))) {
			World w = loc.getWorld();
			double x = loc.getX();
			double y = loc.getY();
			double z = loc.getZ();

			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (p == null || !p.isOnline() || p.isDead() || p == fme || p.getWorld() != w)
					continue;

				FPlayer fp = FPlayers.i.get(p);
				if (fme.getRelationTo(fp) != Relation.ENEMY)
					continue;

				Location l = p.getLocation();
				double dx = Math.abs(x - l.getX());
				double dy = Math.abs(y - l.getY());
				double dz = Math.abs(z - l.getZ());
				double max = Conf.homesTeleportAllowedEnemyDistance;

				// box-shaped distance check
				if (dx > max || dy > max || dz > max)
					continue;

				fme.msg("<b>You cannot teleport while an enemy is within " + Conf.homesTeleportAllowedEnemyDistance + " blocks of you.");
				return true;
			}
		}
		return false;
	}

}
