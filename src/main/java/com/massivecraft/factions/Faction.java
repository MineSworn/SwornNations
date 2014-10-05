package com.massivecraft.factions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.dmulloy2.swornnations.SwornNations;
import net.dmulloy2.swornnations.types.NPermission;
import net.dmulloy2.swornnations.types.NPermissionManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.LWCFeatures;
import com.massivecraft.factions.persist.Entity;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.types.Permission;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.RelationUtil;

public class Faction extends Entity implements EconomyParticipator
{
	// FIELD: relationWish
	private Map<String, Relation> relationWish;

	// FIELD: claimOwnership
	private Map<FLocation, Set<String>> claimOwnership = new ConcurrentHashMap<FLocation, Set<String>>();

	private NPermissionManager permManager;

	// FIELD: fplayers
	// speedy lookup of players in faction
	private transient Set<FPlayer> fplayers = new HashSet<FPlayer>();

	// FIELD: invites
	// Where string is a lowercase player name
	private Set<String> invites;

	public void invite(FPlayer fplayer)
	{
		this.invites.add(fplayer.getName().toLowerCase());
	}

	public void deinvite(FPlayer fplayer)
	{
		this.invites.remove(fplayer.getName().toLowerCase());
	}

	public boolean isInvited(FPlayer fplayer)
	{
		return this.invites.contains(fplayer.getName().toLowerCase());
	}

	// FIELD: open
	private boolean open;

	public boolean getOpen()
	{
		return open;
	}

	public void setOpen(boolean isOpen)
	{
		open = isOpen;
	}

	// FIELD: peaceful
	// "peaceful" status can only be set by server admins/moderators/ops, and
	// prevents PvP and land capture to/from the faction
	private boolean peaceful;

	public boolean isPeaceful()
	{
		return this.peaceful;
	}

	public void setPeaceful(boolean isPeaceful)
	{
		this.peaceful = isPeaceful;
	}

	private boolean permawar;

	public boolean isPermanentWar()
	{
		return this.permawar;
	}

	public void setPermanentWar(boolean isPermanentWar)
	{
		this.permawar = isPermanentWar;
	}

	private boolean gold;

	public boolean isGold()
	{
		return this.gold;
	}

	public void setGold(boolean isGold)
	{
		this.gold = isGold;
	}

	// FIELD: peacefulExplosionsEnabled
	private boolean peacefulExplosionsEnabled;

	public void setPeacefulExplosionsEnabled(boolean val)
	{
		peacefulExplosionsEnabled = val;
	}

	public boolean getPeacefulExplosionsEnabled()
	{
		return this.peacefulExplosionsEnabled;
	}

	public boolean noExplosionsInTerritory()
	{
		return this.peaceful && ! peacefulExplosionsEnabled;
	}

	// FIELD: permanent
	// "permanent" status can only be set by server admins/moderators/ops, and
	// allows the faction to remain even with 0 members
	private boolean permanent;

	public boolean isPermanent()
	{
		return permanent || ! isNormal();
	}

	public void setPermanent(boolean isPermanent)
	{
		permanent = isPermanent;
	}

	// FIELD: tag
	private String tag;

	public String getTag()
	{
		return tag;
	}

	public String getTag(String prefix)
	{
		return prefix + tag;
	}

	public String getTag(RelationParticipator rp)
	{
		if (rp == null)
		{
			return getTag();
		}

		return getTag(getColorTo(rp).toString());
	}

	public void setTag(String str)
	{
		if (Conf.factionTagForceUpperCase)
		{
			str = str.toUpperCase();
		}

		this.tag = str;
	}

	public String getComparisonTag()
	{
		return MiscUtil.getComparisonString(this.tag);
	}

	// FIELD: description
	private String description;

	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(String value)
	{
		this.description = value;
	}

	// FIELD: motd
	private String motd;

	public String getMOTD()
	{
		return this.motd;
	}

	public void setMOTD(String value)
	{
		this.motd = value;
	}

	public boolean hasMOTD()
	{
		return motd != null && ! motd.isEmpty();
	}

	// FIELD: home
	private LazyLocation home;

	public void setHome(Location home)
	{
		this.home = new LazyLocation(home);
	}

	public boolean hasHome()
	{
		return this.getHome() != null;
	}

	public Location getHome()
	{
		confirmValidHome();
		return (this.home != null) ? this.home.getLocation() : null;
	}

	public void confirmValidHome()
	{
		if (! Conf.homesMustBeInClaimedTerritory || home == null
				|| (home.getLocation() != null && Board.getFactionAt(new FLocation(home.getLocation())) == this))
			return;

		msg("<b>Your faction home has been un-set since it is no longer in your territory.");
		this.home = null;
	}

	// FIELD: outpost
	private LazyLocation outpost;

	public void setOutpost(Location outpost)
	{
		this.outpost = new LazyLocation(outpost);
	}

	public boolean hasOutpost()
	{
		return this.getOutpost() != null;
	}

	public Location getOutpost()
	{
		confirmValidOutpost();
		return (outpost != null) ? outpost.getLocation() : null;
	}

	public void confirmValidOutpost()
	{
		if (outpost == null || (outpost.getLocation() != null && Board.getFactionAt(new FLocation(outpost.getLocation())) == this))
			return;

		msg("<b>Your faction outpost has been un-set since it is no longer in your territory.");
		this.outpost = null;
	}

	private LazyLocation warp;
	private long warpSaveTime;

	public void setWarp(Location warp)
	{
		this.warp = new LazyLocation(warp);
		warpSaveTime = System.currentTimeMillis();
	}

	public boolean hasWarp()
	{
		return getWarp() != null;
	}

	public Location getWarp()
	{
		confirmValidWarp();
		return (warp != null) ? warp.getLocation() : null;
	}

	public void confirmValidWarp()
	{
		if ((System.currentTimeMillis() - warpSaveTime) > Conf.warpsDecayTime * 60 * 1000)
		{
			msg("<b>Your faction warp has been un-set.");
			warp = null;
		}
	}

	// FIELD: lastPlayerLoggedOffTime
	private transient long lastPlayerLoggedOffTime;

	// FIELD: account (fake field)
	// Bank functions
	public double money;

	@Override
	public String getAccountId()
	{
		String aid = "faction-" + getId();

		// We need to override the default money given to players.
		if (! Econ.hasAccount(aid))
		{
			Econ.setBalance(aid, 0);
		}

		return aid;
	}

	// FIELD: permanentPower
	private Integer permanentPower;

	public Integer getPermanentPower()
	{
		return this.permanentPower;
	}

	public void setPermanentPower(Integer permanentPower)
	{
		this.permanentPower = permanentPower;
	}

	public boolean hasPermanentPower()
	{
		return this.permanentPower != null;
	}

	// FIELD: powerBoost
	// special increase/decrease to default and max power for this faction
	private double powerBoost;

	public double getPowerBoost()
	{
		return this.powerBoost;
	}

	public void setPowerBoost(double powerBoost)
	{
		this.powerBoost = powerBoost;
	}

	// -------------------------------------------- //
	// Construct
	// -------------------------------------------- //

	public Faction()
	{
		this.relationWish = new HashMap<String, Relation>();
		this.invites = new HashSet<String>();
		this.open = Conf.newFactionsDefaultOpen;
		this.tag = "???";
		this.description = "Default faction description :(";
		this.lastPlayerLoggedOffTime = 0;
		this.peaceful = false;
		this.permawar = false;
		this.peacefulExplosionsEnabled = false;
		this.permanent = false;
		this.money = 0.0;
		this.powerBoost = 0.0;

		if (permManager == null)
			resetPermManager();
	}

	// -------------------------------------------- //
	// Extra Getters And Setters
	// -------------------------------------------- //

	public boolean noPvPInTerritory()
	{
		return isSafeZone() || (peaceful && Conf.peacefulTerritoryDisablePVP);
	}

	public boolean noMonstersInTerritory()
	{
		return isSafeZone() || (peaceful && Conf.peacefulTerritoryDisableMonsters);
	}

	// -------------------------------
	// Understand the types
	// -------------------------------

	public boolean isNormal()
	{
		return ! (this.isNone() || this.isSafeZone() || this.isWarZone());
	}

	public boolean isNone()
	{
		return this.getId().equals("0");
	}

	public boolean isSafeZone()
	{
		return this.getId().equals("-1");
	}

	public boolean isWarZone()
	{
		return this.getId().equals("-2");
	}

	public boolean isPlayerFreeType()
	{
		return this.isSafeZone() || this.isWarZone();
	}

	// -------------------------------
	// Relation and relation colors
	// -------------------------------

	@Override
	public String describeTo(RelationParticipator that, boolean ucfirst)
	{
		return RelationUtil.describeThatToMe(this, that, ucfirst);
	}

	@Override
	public String describeTo(RelationParticipator that)
	{
		return RelationUtil.describeThatToMe(this, that);
	}

	@Override
	public Relation getRelationTo(RelationParticipator rp)
	{
		return RelationUtil.getRelationTo(this, rp);
	}

	@Override
	public Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful)
	{
		return RelationUtil.getRelationTo(this, rp, ignorePeaceful);
	}

	@Override
	public ChatColor getColorTo(RelationParticipator rp)
	{
		return RelationUtil.getColorOfThatToMe(this, rp);
	}

	public Relation getRelationWish(Faction otherFaction)
	{
		if (this.relationWish.containsKey(otherFaction.getId()))
		{
			return this.relationWish.get(otherFaction.getId());
		}
		return Relation.NEUTRAL;
	}

	public Map<String, Relation> getRelationWishes()
	{
		return new HashMap<String, Relation>(relationWish);
	}

	public void removeRelationWish(String key)
	{
		relationWish.remove(key);
	}

	public void setRelationWish(Faction otherFaction, Relation relation)
	{
		if (this.relationWish.containsKey(otherFaction.getId()) && relation.equals(Relation.NEUTRAL))
		{
			this.relationWish.remove(otherFaction.getId());
		}
		else
		{
			this.relationWish.put(otherFaction.getId(), relation);
		}
	}

	// ----------------------------------------------//
	// Power
	// ----------------------------------------------//
	public double getPower()
	{
		if (this.hasPermanentPower())
		{
			return this.getPermanentPower();
		}

		double ret = 0;
		for (FPlayer fplayer : fplayers)
		{
			ret += fplayer.getPower();
		}
		if (Conf.powerFactionMax > 0 && ret > Conf.powerFactionMax)
		{
			ret = Conf.powerFactionMax;
		}
		return ret + this.powerBoost;
	}

	public double getPowerMax()
	{
		if (this.hasPermanentPower())
		{
			return this.getPermanentPower();
		}

		double ret = 0;
		for (FPlayer fplayer : fplayers)
		{
			ret += fplayer.getPowerMax();
		}
		if (Conf.powerFactionMax > 0 && ret > Conf.powerFactionMax)
		{
			ret = Conf.powerFactionMax;
		}
		return ret + this.powerBoost;
	}

	public int getPowerRounded()
	{
		return (int) Math.round(this.getPower());
	}

	public int getPowerMaxRounded()
	{
		return (int) Math.round(this.getPowerMax());
	}

	public int getLandRounded()
	{
		return Board.getFactionCoordCount(this);
	}

	public int getLandRoundedInWorld(String worldName)
	{
		return Board.getFactionCoordCountInWorld(this, worldName);
	}

	public boolean hasLandInflation()
	{
		return this.getLandRounded() > this.getPowerRounded();
	}

	// -------------------------------
	// FPlayers
	// -------------------------------

	// maintain the reference list of FPlayers in this faction
	public void refreshFPlayers()
	{
		fplayers.clear();
		if (this.isPlayerFreeType())
			return;

		for (FPlayer fplayer : FPlayers.i.get())
		{
			if (fplayer.getFaction() == this)
			{
				fplayers.add(fplayer);
			}
		}
	}

	protected boolean addFPlayer(FPlayer fplayer)
	{
		if (this.isPlayerFreeType())
			return false;

		return fplayers.add(fplayer);
	}

	protected boolean removeFPlayer(FPlayer fplayer)
	{
		if (this.isPlayerFreeType())
			return false;

		return fplayers.remove(fplayer);
	}

	public Set<FPlayer> getFPlayers()
	{
		// return a shallow copy of the FPlayer list, to prevent tampering and
		// concurrency issues
		Set<FPlayer> ret = new HashSet<FPlayer>(fplayers);
		return ret;
	}

	public Set<FPlayer> getFPlayersWhereOnline(boolean online)
	{
		Set<FPlayer> ret = new HashSet<FPlayer>();

		for (FPlayer fplayer : fplayers)
		{
			if (fplayer.isOnline() == online)
			{
				ret.add(fplayer);
			}
		}

		return ret;
	}

	public FPlayer getFPlayerAdmin()
	{
		if (! this.isNormal())
			return null;

		for (FPlayer fplayer : fplayers)
		{
			if (fplayer.getRole() == Role.ADMIN)
			{
				return fplayer;
			}
		}
		return null;
	}

	public ArrayList<FPlayer> getFPlayersWhereRole(Role role)
	{
		ArrayList<FPlayer> ret = new ArrayList<FPlayer>();
		if (! this.isNormal())
			return ret;

		for (FPlayer fplayer : fplayers)
		{
			if (fplayer.getRole() == role)
			{
				ret.add(fplayer);
			}
		}

		return ret;
	}

	public List<Player> getOnlinePlayers()
	{
		List<Player> ret = new ArrayList<>();
		if (isPlayerFreeType())
			return ret;

		for (FPlayer fplayer : FPlayers.i.getOnline())
		{
			if (fplayer.getFaction() == this)
				ret.add(fplayer.getPlayer());
		}

		return ret;
	}

	// slightly faster check than getOnlinePlayers() if you just want to see if
	// there are any players online
	public boolean hasPlayersOnline()
	{
		// only real factions can have players online, not safe zone / war zone
		if (isPlayerFreeType())
			return false;

		for (FPlayer fplayer : FPlayers.i.getOnline())
		{
			if (fplayer.getFaction() == this)
				return true;
		}

		// even if all players are technically logged off, maybe someone was on
		// recently enough to not consider them officially offline yet
		return (Conf.considerFactionsReallyOfflineAfterXMinutes > 0 && System.currentTimeMillis() < lastPlayerLoggedOffTime
				+ (Conf.considerFactionsReallyOfflineAfterXMinutes * 60000));
	}

	public void memberLoggedOff()
	{
		if (this.isNormal())
		{
			lastPlayerLoggedOffTime = System.currentTimeMillis();
		}
	}

	// used when current leader is about to be removed from the faction;
	// promotes new leader, or disbands faction if no other members left
	public void promoteNewLeader()
	{
		if (! this.isNormal())
			return;
		if (this.isPermanent() && Conf.permanentFactionsDisableLeaderPromotion)
			return;

		FPlayer oldLeader = this.getFPlayerAdmin();

		// get list of moderators, or list of normal members if there are no
		// moderators
		List<FPlayer> replacements = this.getFPlayersWhereRole(Role.MODERATOR);
		if (replacements == null || replacements.isEmpty())
			replacements = this.getFPlayersWhereRole(Role.NORMAL);

		if (replacements == null || replacements.isEmpty()) // faction admin is the only member; one-man faction
		{
			if (this.isPermanent())
			{
				if (oldLeader != null)
					oldLeader.setRole(Role.NORMAL);
				return;
			}

			// no members left and faction isn't permanent, so disband it
			if (Conf.logFactionDisband)
				SwornNations.get().log(
						"The faction " + this.getTag() + " (" + this.getId() + ") has been disbanded since it has no members left.");

			for (FPlayer fplayer : FPlayers.i.getOnline())
			{
				fplayer.msg("<i>The faction %s<i> was disbanded.", this.getTag(fplayer));
			}

			this.detach();
		}
		else
		{
			// promote new faction admin
			if (oldLeader != null)
				oldLeader.setRole(Role.NORMAL);
			replacements.get(0).setRole(Role.ADMIN);
			this.msg("<i>Faction admin <h>%s<i> has been removed. %s<i> has been promoted as the new faction admin.", oldLeader == null ? ""
					: oldLeader.getName(), replacements.get(0).getName());
			SwornNations.get().log(
					"Faction " + this.getTag() + " (" + this.getId() + ") admin was removed. Replacement admin: "
							+ replacements.get(0).getName());
		}
	}

	// ----------------------------------------------//
	// Messages
	// ----------------------------------------------//
	@Override
	public void msg(String message, Object... args)
	{
		message = SwornNations.get().txt.parse(message, args);

		for (FPlayer fplayer : this.getFPlayersWhereOnline(true))
		{
			fplayer.sendMessage(message);
		}
	}

	@Override
	public void sendMessage(String message)
	{
		for (FPlayer fplayer : this.getFPlayersWhereOnline(true))
		{
			fplayer.sendMessage(message);
		}
	}

	public void sendMessage(List<String> messages)
	{
		for (FPlayer fplayer : this.getFPlayersWhereOnline(true))
		{
			fplayer.sendMessage(messages);
		}
	}

	// ----------------------------------------------//
	// Ownership of specific claims
	// ----------------------------------------------//

	public void clearAllClaimOwnership()
	{
		claimOwnership.clear();
	}

	public void clearClaimOwnership(FLocation loc)
	{
		if (Conf.onUnclaimResetLwcLocks && LWCFeatures.isEnabled())
		{
			LWCFeatures.clearAllChests(loc);
			// Bukkit.getServer().broadcastMessage("boardclearat / clearclaim");
		}

		claimOwnership.remove(loc);
	}

	public void clearClaimOwnership(String playerName)
	{
		if (playerName == null || playerName.isEmpty())
		{
			return;
		}

		Set<String> ownerData;
		String player = playerName.toLowerCase();

		for (Entry<FLocation, Set<String>> entry : claimOwnership.entrySet())
		{
			ownerData = entry.getValue();

			if (ownerData == null)
				continue;

			Iterator<String> iter = ownerData.iterator();
			while (iter.hasNext())
			{
				if (iter.next().equals(player))
				{
					iter.remove();
				}
			}

			if (ownerData.isEmpty())
			{
				if (Conf.onUnclaimResetLwcLocks && LWCFeatures.isEnabled())
				{
					LWCFeatures.clearAllChests(entry.getKey());
				}
				claimOwnership.remove(entry.getKey());
			}
		}
	}

	public int getCountOfClaimsWithOwner(FPlayer player)
	{
		int count = 0;
		String playerName = player.getName().toLowerCase();
		for (Set<String> ownerData : claimOwnership.values())
		{
			if (ownerData.contains(playerName))
				count++;
		}
		return count;
	}

	public int getCountOfClaimsWithOwners()
	{
		return claimOwnership.isEmpty() ? 0 : claimOwnership.size();
	}

	public boolean doesLocationHaveOwnersSet(FLocation loc)
	{
		if (claimOwnership.isEmpty() || ! claimOwnership.containsKey(loc))
		{
			return false;
		}

		Set<String> ownerData = claimOwnership.get(loc);
		return ownerData != null && ! ownerData.isEmpty();
	}

	public boolean isPlayerInOwnerList(String playerName, FLocation loc)
	{
		if (claimOwnership.isEmpty())
		{
			return false;
		}
		Set<String> ownerData = claimOwnership.get(loc);
		if (ownerData == null)
		{
			return false;
		}
		if (ownerData.contains(playerName.toLowerCase()))
		{
			return true;
		}

		return false;
	}

	public void setPlayerAsOwner(String playerName, FLocation loc)
	{
		Set<String> ownerData = claimOwnership.get(loc);
		if (ownerData == null)
		{
			ownerData = new HashSet<String>();
		}
		ownerData.add(playerName.toLowerCase());
		claimOwnership.put(loc, ownerData);
	}

	public void removePlayerAsOwner(String playerName, FLocation loc)
	{
		Set<String> ownerData = claimOwnership.get(loc);
		if (ownerData == null)
		{
			return;
		}
		ownerData.remove(playerName.toLowerCase());
		claimOwnership.put(loc, ownerData);
	}

	public Set<String> getOwnerList(FLocation loc)
	{
		return claimOwnership.get(loc);
	}

	public String getOwnerListString(FLocation loc)
	{
		Set<String> ownerData = claimOwnership.get(loc);
		if (ownerData == null || ownerData.isEmpty())
		{
			return "";
		}

		String ownerList = "";

		Iterator<String> iter = ownerData.iterator();
		while (iter.hasNext())
		{
			if (! ownerList.isEmpty())
			{
				ownerList += ", ";
			}
			ownerList += iter.next();
		}
		return ownerList;
	}

	public void cleanOwnerList()
	{
		if (claimOwnership.isEmpty())
			return;

		String nameRegex = "^[a-zA-Z_0-9]";
		for (Entry<FLocation, Set<String>> entry : new HashMap<FLocation, Set<String>>(claimOwnership).entrySet())
		{
			if (entry.getValue().isEmpty())
				continue;

			// Fun little concurrency hack
			String[] names = entry.getValue().toArray(new String[0]);

			for (String name : names)
			{
				if (! name.matches(nameRegex))
					entry.getValue().remove(name);
			}
		}
	}

	public boolean playerHasOwnershipRights(FPlayer fplayer, FLocation loc)
	{
		// in own faction, with sufficient role or permission to bypass
		// ownership?
		if (fplayer.getFaction() == this
				&& ((fplayer.getRole().isAtLeast(Conf.ownedAreaModeratorsBypass ? Role.MODERATOR : Role.ADMIN) && fplayer.getFaction()
						.playerHasPermission(fplayer, NPermission.OWNER)) || Permission.OWNERSHIP_BYPASS.has(fplayer.getPlayer())))
		{
			return true;
		}

		// make sure claimOwnership is initialized
		if (claimOwnership.isEmpty())
			return true;

		// need to check the ownership list, then
		Set<String> ownerData = claimOwnership.get(loc);

		// if no owner list, owner list is empty, or player is in owner list,
		// they're allowed
		if (ownerData == null || ownerData.isEmpty() || ownerData.contains(fplayer.getName().toLowerCase()))
			return true;

		return false;
	}

	public boolean playerHasPermission(FPlayer player, NPermission perm)
	{
		if (player.getFaction() == this)
		{
			if (this.isNone())
				return true;
			if (permManager.isDeniedPerm(player, perm))
				return false;
			if (permManager.hasPerm(player, perm))
				return true;
			if (permManager.hasPerm(player.getRole(), perm))
				return true;
		}
		return false;
	}

	public boolean roleHasPermission(Role role, NPermission perm)
	{
		if (permManager.hasPerm(role, perm))
			return true;
		return false;
	}

	public void addPermission(FPlayer player, NPermission perm)
	{
		if (player.getFaction() == this)
		{
			permManager.addPerm(player, perm);
		}
	}

	public void addPermission(Role role, NPermission perm)
	{
		permManager.addPerm(role, perm);
	}

	public void removePermission(FPlayer player, NPermission perm)
	{
		if (player.getFaction() == this)
		{
			permManager.removePerm(player, perm);
		}
	}

	public void removePermission(Role role, NPermission perm)
	{
		permManager.removePerm(role, perm);
	}

	public void resetPermManager()
	{
		permManager = new NPermissionManager();
	}

	// ----------------------------------------------//
	// Persistance and entity management
	// ----------------------------------------------//

	@Override
	public void postDetach()
	{
		if (Econ.shouldBeUsed())
		{
			Econ.setBalance(getAccountId(), 0);
		}

		// Clean the board
		Board.clean();

		// Clean the fplayers
		FPlayers.i.clean();
	}

}
