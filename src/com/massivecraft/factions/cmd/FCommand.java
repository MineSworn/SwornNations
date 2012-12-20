package com.massivecraft.factions.cmd;

import java.util.List;

import me.t7seven7t.swornnations.npermissions.NPermission;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.MCommand;


public abstract class FCommand extends MCommand<P>
{
	public boolean disableOnLock;
	
	public FPlayer fme;
	public Faction myFaction;
	public boolean senderMustBeMember;
	public boolean senderMustBeOfficer;
	public boolean senderMustBeModerator;
	public boolean senderMustBeAdmin;
	public boolean commandNotNeedFaction;
	public NPermission senderMustHaveNPermission;
	
	public boolean isMoneyCommand;
	
	public FCommand()
	{
		super(P.p);
		
		// Due to safety reasons it defaults to disable on lock.
		disableOnLock = true;
		
		// The money commands must be disabled if money should not be used.
		isMoneyCommand = false;
		
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		commandNotNeedFaction = false;
		senderMustHaveNPermission = null;
	}
	
	@Override
	public void execute(CommandSender sender, List<String> args, List<MCommand<?>> commandChain)
	{
		if (sender instanceof Player)
		{
			this.fme = FPlayers.i.get((Player)sender);
			this.myFaction = this.fme.getFaction();
		}
		else
		{
			this.fme = null;
			this.myFaction = null;
		}
		super.execute(sender, args, commandChain);
	}
	
	@Override
	public boolean isEnabled()
	{
		if (p.getLocked() && this.disableOnLock)
		{
			msg("<b>Factions was locked by an admin. Please try again later.");
			return false;
		}
		
		if (this.isMoneyCommand && ! Conf.econEnabled)
		{
			msg("<b>Faction economy features are disabled on this server.");
			return false;
		}
		
		if (this.isMoneyCommand && ! Conf.bankEnabled)
		{
			msg("<b>The faction bank system is disabled on this server.");
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean validSenderType(CommandSender sender, boolean informSenderIfNot)
	{
		boolean superValid = super.validSenderType(sender, informSenderIfNot);
		if ( ! superValid) return false;
		
		if ( ! (this.senderMustBeMember || this.senderMustBeModerator || this.senderMustBeAdmin || this.senderMustHaveNPermission != null)) return true;
		
		if ( ! (sender instanceof Player)) return false;
		
		FPlayer fplayer = FPlayers.i.get((Player)sender);
		
		if ( fplayer.isAdminBypassing() && commandNotNeedFaction)
			return true;
		
		if ( ! fplayer.hasFaction())
		{
			sender.sendMessage(p.txt.parse("<b>You are not member of any faction."));
			return false;
		}
		
		if (this.senderMustBeOfficer && !fplayer.getRole().isAtLeast(Role.OFFICER)) {
			sender.sendMessage(p.txt.parse("<b>Only faction officers can %s.", this.getHelpShort()));
			return false;
		}
		
		if (this.senderMustBeModerator && ! fplayer.getRole().isAtLeast(Role.MODERATOR))
		{
			sender.sendMessage(p.txt.parse("<b>Only faction moderators can %s.", this.getHelpShort()));
			return false;
		}
		
		if (this.senderMustBeAdmin && ! fplayer.getRole().isAtLeast(Role.ADMIN))
		{
			sender.sendMessage(p.txt.parse("<b>Only faction admins can %s.", this.getHelpShort()));
			return false;
		}
		
		if (this.senderMustHaveNPermission != null && !fplayer.getFaction().playerHasPermission(fplayer, this.senderMustHaveNPermission) && !fplayer.isAdminBypassing()) {
			sender.sendMessage(p.txt.parse("<b>You don't have permission to %s.", this.getHelpShort()));
			return false;
		}
			
		return true;
	}
	
	// -------------------------------------------- //
	// Assertions
	// -------------------------------------------- //

	public boolean assertHasFaction()
	{
		if (me == null) return true;
		
		if ( ! fme.hasFaction())
		{
			sendMessage("You are not member of any faction.");
			return false;
		}
		return true;
	}

	public boolean assertMinRole(Role role)
	{
		if (me == null) return true;
		
		if (fme.getRole().value < role.value)
		{
			msg("<b>You <h>must be "+role+"<b> to "+this.getHelpShort()+".");
			return false;
		}
		return true;
	}
	
	// -------------------------------------------- //
	// Argument Readers
	// -------------------------------------------- //
	
	// FPLAYER ======================
	public FPlayer strAsFPlayer(String name, FPlayer def, boolean msg)
	{
		FPlayer ret = def;
		
		if (name != null)
		{
			FPlayer fplayer = FPlayers.i.get(name);
			if (fplayer != null)
			{
				ret = fplayer;
			}
		}
		
		if (msg && ret == null)
		{
			this.msg("<b>No player \"<p>%s<b>\" could be found.", name);			
		}
		
		return ret;
	}
	public FPlayer argAsFPlayer(int idx, FPlayer def, boolean msg)
	{
		return this.strAsFPlayer(this.argAsString(idx), def, msg);
	}
	public FPlayer argAsFPlayer(int idx, FPlayer def)
	{
		return this.argAsFPlayer(idx, def, true);
	}
	public FPlayer argAsFPlayer(int idx)
	{
		return this.argAsFPlayer(idx, null);
	}
	
	// BEST FPLAYER MATCH ======================
	public FPlayer strAsBestFPlayerMatch(String name, FPlayer def, boolean msg)
	{
		FPlayer ret = def;
		
		if (name != null)
		{
			FPlayer fplayer = FPlayers.i.getBestIdMatch(name);
			if (fplayer != null)
			{
				ret = fplayer;
			}
		}
		
		if (msg && ret == null)
		{
			this.msg("<b>No player match found for \"<p>%s<b>\".", name);
		}
		
		return ret;
	}
	public FPlayer argAsBestFPlayerMatch(int idx, FPlayer def, boolean msg)
	{
		return this.strAsBestFPlayerMatch(this.argAsString(idx), def, msg);
	}
	public FPlayer argAsBestFPlayerMatch(int idx, FPlayer def)
	{
		return this.argAsBestFPlayerMatch(idx, def, true);
	}
	public FPlayer argAsBestFPlayerMatch(int idx)
	{
		return this.argAsBestFPlayerMatch(idx, null);
	}
	
	// FACTION ======================
	public Faction strAsFaction(String name, Faction def, boolean msg)
	{
		Faction ret = def;
		
		if (name != null)
		{
			Faction faction = null;
			
			// First we try an exact match
			if (faction == null)
			{
				faction = Factions.i.getByTag(name);
			}
			
			// Next we match faction tags
			if (faction == null)
			{
				faction = Factions.i.getBestTagMatch(name);
			}
				
			// Next we match player names
			if (faction == null)
			{
				FPlayer fplayer = FPlayers.i.getBestIdMatch(name);
				if (fplayer != null)
				{
					faction = fplayer.getFaction();
				}
			}
			
			if (faction != null)
			{
				ret = faction;
			}
		}
		
		if (msg && ret == null)
		{
			this.msg("<b>The faction or player \"<p>%s<b>\" could not be found.", name);
		}
		
		return ret;
	}
	public Faction argAsFaction(int idx, Faction def, boolean msg)
	{
		return this.strAsFaction(this.argAsString(idx), def, msg);
	}
	public Faction argAsFaction(int idx, Faction def)
	{
		return this.argAsFaction(idx, def, true);
	}
	public Faction argAsFaction(int idx)
	{
		return this.argAsFaction(idx, null);
	}
	
	// -------------------------------------------- //
	// Commonly used logic
	// -------------------------------------------- //
	
	public boolean canIAdministerYou(FPlayer i, FPlayer you)
	{
		if ( ! i.getFaction().equals(you.getFaction()))
		{
			i.sendMessage(p.txt.parse("%s <b>is not in the same faction as you.",you.describeTo(i, true)));
			return false;
		}
		
		if (i.getRole().value > you.getRole().value || i.getRole().equals(Role.ADMIN) )
		{
			return true;
		}
		
		if (you.getRole().equals(Role.ADMIN))
		{
			i.sendMessage(p.txt.parse("<b>Only the faction admin can do that."));
		}
		else if (i.getRole().equals(Role.MODERATOR))
		{
			if ( i == you )
			{
				return true; //Moderators can control themselves
			}
			else
			{
				i.sendMessage(p.txt.parse("<b>Moderators can't control each other..."));
			}
		}
		else 
		{
			//i.sendMessage(p.txt.parse("<b>You must be a faction moderator to do that."));
		}
		
		return false;
	}
	
	// if economy is enabled and they're not on the bypass list, make 'em pay; returns true unless person can't afford the cost
	public boolean payForCommand(double cost, String toDoThis, String forDoingThis)
	{
		if ( ! Econ.shouldBeUsed() || this.fme == null || cost == 0.0 || fme.isAdminBypassing()) return true;

		if(Conf.bankEnabled && Conf.bankFactionPaysCosts && fme.hasFaction())
			return Econ.modifyMoney(myFaction, -cost, toDoThis, forDoingThis);
		else
			return Econ.modifyMoney(fme, -cost, toDoThis, forDoingThis);
	}

	// like above, but just make sure they can pay; returns true unless person can't afford the cost
	public boolean canAffordCommand(double cost, String toDoThis)
	{
		if ( ! Econ.shouldBeUsed() || this.fme == null || cost == 0.0 || fme.isAdminBypassing()) return true;

		if(Conf.bankEnabled && Conf.bankFactionPaysCosts && fme.hasFaction())
			return Econ.hasAtLeast(myFaction, -cost, toDoThis);
		else
			return Econ.hasAtLeast(fme, -cost, toDoThis);
	}
	
	public boolean payPowerForCommandF(int cost) {
		if (cost == 0 || fme.isAdminBypassing()) return true;
		
		Faction faction = this.argAsFaction(0, myFaction);
		
		for (FPlayer fme : faction.getFPlayers()) {
			fme.payPower(cost, true);
		}
		
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

			for (Player p : me.getServer().getOnlinePlayers()) {
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
	
	public boolean payPowerForCommand(int cost) {
		if (cost == 0 || fme.isAdminBypassing()) return true;
				
		return fme.payPower(cost, false);
	}
}
