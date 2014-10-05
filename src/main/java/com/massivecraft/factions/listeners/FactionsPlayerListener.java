package com.massivecraft.factions.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.dmulloy2.swornnations.SwornNations;
import net.dmulloy2.swornnations.types.NPermission;
import net.dmulloy2.types.MyMaterial;
import net.dmulloy2.util.Util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.types.Permission;
import com.massivecraft.factions.util.VisualizeUtil;

public class FactionsPlayerListener implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		// Make sure that all online players do have a fplayer.
		Player player = event.getPlayer();
		FPlayer me = FPlayers.i.get(player);

		if (Conf.opsBypassByDefault && player.isOp())
		{
			me.setIsAdminBypassing(true);
			me.setSpyingChat(true);
		}

		// UUID Stuff
		me.updateUniqueId();

		// Update the lastLoginTime for this fplayer
		me.setLastLoginTime(System.currentTimeMillis());

		// Faction MOTD
		Faction faction = me.getFaction();
		if (faction != null && faction.hasMOTD())
		{
			me.msg("&d[Faction MOTD]: &e%s", faction.getMOTD());
		}

		handlePlayerMove(player, player.getLocation(), null, false);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		onPlayerDisconnect(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event)
	{
		if (! event.isCancelled())
		{
			onPlayerDisconnect(event.getPlayer());
		}
	}

	public void onPlayerDisconnect(Player player)
	{
		FPlayer me = FPlayers.i.get(player);

		// Make sure player's power is up to date when they log off.
		me.getPower();

		// Update their last login time to point to when the logged off, for
		// auto-remove routine
		me.setLastLoginTime(System.currentTimeMillis());

		Faction myFaction = me.getFaction();
		if (myFaction != null)
		{
			myFaction.memberLoggedOff();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if (! event.isCancelled())
		{
			handlePlayerMove(event.getPlayer(), event.getTo(), event.getFrom(), true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		if (! event.isCancelled())
		{
			handlePlayerMove(event.getPlayer(), event.getTo(), event.getFrom(), false);
		}
	}

	public void handlePlayerMove(Player player, Location locationTo, Location locationFrom, boolean moveEvent)
	{
		if (moveEvent)
		{
			if (locationFrom.getBlockX() >> 4 == locationTo.getBlockX() >> 4 && locationFrom.getBlockZ() >> 4 == locationTo.getBlockZ() >> 4
					&& locationFrom.getWorld() == locationTo.getWorld())
				return;
		}

		FPlayer me = FPlayers.i.get(player);

		// Did we change coord?
		FLocation from = me.getLastStoodAt();
		FLocation to = new FLocation(locationTo);

		// No, return.
		if (from.yequals(to))
			return;

		// Yes, continue
		me.setLastStoodAt(to);

		// Update map
		if (me.isMapAutoUpdating())
		{
			me.sendMessage(Board.getMap(me, to, player.getLocation().getYaw()));
		}

		// Did we change "host" faction?
		Faction factionFrom = Board.getFactionAt(from);
		Faction factionTo = Board.getFactionAt(to);
		boolean changedFaction = factionFrom != factionTo;

		// Yes
		if (changedFaction)
		{
			me.sendFactionHereMessage();

			// Update map
			if (me.isMapAutoUpdating())
			{
				me.sendMessage(Board.getMap(me, to, player.getLocation().getYaw()));
			}

			if (! moveEvent)
			{
				if (me.getAutoClaimFor() != null)
				{
					me.setAutoClaimFor(null);
				}
				else if (me.isAutoSafeClaimEnabled())
				{
					me.setIsAutoSafeClaimEnabled(false);
				}
				else if (me.isAutoWarClaimEnabled())
				{
					me.setIsAutoWarClaimEnabled(false);
				}
			}
			else
			{
				if (me.getAutoClaimFor() != null)
				{
					me.attemptClaim(me.getAutoClaimFor(), locationTo, true);
				}
				else if (me.isAutoSafeClaimEnabled())
				{
					if (! Permission.MANAGE_SAFE_ZONE.has(player))
					{
						me.setIsAutoSafeClaimEnabled(false);
					}
					else
					{
						if (! Board.getFactionAt(to).isSafeZone())
						{
							Board.setFactionAt(Factions.i.getSafeZone(), to);
							me.msg("<i>This land is now a %s<i>", Factions.i.getSafeZone().describeTo(me));
						}
					}
				}
				else if (me.isAutoWarClaimEnabled())
				{
					if (! Permission.MANAGE_WAR_ZONE.has(player))
					{
						me.setIsAutoWarClaimEnabled(false);
					}
					else
					{
						if (! Board.getFactionAt(to).isWarZone())
						{
							Board.setFactionAt(Factions.i.getWarZone(), to);
							me.msg("<i>This land is now a %s<i>", Factions.i.getWarZone().describeTo(me));
						}
					}
				}

				// TODO: /f autounclaim ?
			}
		}

		Faction myFaction = me.getFaction();
		if (myFaction.isNone())
			return;

		if (myFaction == factionTo)
		{
			if (Conf.ownedAreasEnabled)
			{
				if (Conf.ownedMessageOnBorder)
				{
					String ownersTo = myFaction.getOwnerListString(to);
					String ownersFrom = myFaction.getOwnerListString(from);
					if (Conf.ownedMessageByChunk || ! ownersFrom.equals(ownersTo))
					{
						if (! ownersTo.isEmpty())
						{
							me.sendMessage(Conf.ownedLandMessage + ownersTo);
						}
						else
						{
							if (! Conf.publicLandMessage.isEmpty())
							{
								me.sendMessage(Conf.publicLandMessage);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.isCancelled() || ! event.hasBlock())
			return;

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.PHYSICAL)
			return;

		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		FPlayer fme = FPlayers.i.get(player);

		if (! canPlayerUseBlock(player, block, false))
		{
			event.setCancelled(true);
			if (Conf.handleExploitInteractionSpam)
			{
				String name = player.getName();
				InteractAttemptSpam attempt = interactSpammers.get(name);
				if (attempt == null)
				{
					attempt = new InteractAttemptSpam();
					interactSpammers.put(name, attempt);
				}
				int count = attempt.increment();
				if (count >= 10)
				{
					fme.msg("<b>Ouch, that is starting to hurt. You should give it a rest.");
					player.damage(Math.floor(count / 10.0D));
				}
			}

			return;
		}

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (! playerCanUseItemHere(player, block.getLocation(), event.getItem(), false))
		{
			event.setCancelled(true);
			return;
		}

		if (block.getType() == Material.BED_BLOCK && Conf.playerHomesEnabled)
		{
			if (Board.getFactionAt(new FLocation(block)) != fme.getFaction())
				return;

			if (! Conf.playerHomesOverride)
			{
				if (fme.getFaction().hasHome())
				{
					FLocation fHome = new FLocation(fme.getFaction().getHome());
					FLocation loc = new FLocation(fme.getPlayer().getLocation());
					if (fHome.getDistanceTo(loc) > 20.0 || Board.getAbsoluteFactionAt(loc) != fme.getFaction())
					{
						return;
					}
				}
				else
				{
					return;
				}
			}

			fme.setHome(fme.getPlayer().getLocation());
			fme.msg("<i>Home set!");
			return;
		}
	}

	// for handling people who repeatedly spam attempts to open a door (or
	// similar) in another faction's territory
	private Map<String, InteractAttemptSpam> interactSpammers = new HashMap<String, InteractAttemptSpam>();

	private static class InteractAttemptSpam
	{
		private int attempts = 0;
		private long lastAttempt = System.currentTimeMillis();

		// returns the current attempt count
		public int increment()
		{
			long Now = System.currentTimeMillis();
			if (Now > lastAttempt + 2000)
				attempts = 1;
			else
				attempts++;
			lastAttempt = Now;
			return attempts;
		}
	}

	public static boolean playerCanUseItemHere(Player player, Location location, ItemStack item, boolean justCheck)
	{
		String name = player.getName();
		if (Conf.playersWhoBypassAllProtection.contains(name))
			return true;

		FPlayer me = FPlayers.i.get(player);
		if (me.isAdminBypassing())
			return true;

		FLocation loc = new FLocation(location);
		Faction otherFaction = Board.getFactionAt(loc);

		if (otherFaction.isNormal())
		{
			if (otherFaction.hasPlayersOnline())
			{
				if (item != null)
				{
					if (! Conf.territoryDenyUseageMaterials.contains(new MyMaterial(item.getType())))
						return true; // Item isn't one we're preventing for
										// online factions.
				}
			}
			else
			{
				if (item != null)
				{
					if (! Conf.territoryDenyUseageMaterialsWhenOffline.contains(new MyMaterial(item.getType())))
						return true; // Item isn't one we're preventing for
										// offline factions.
				}
			}
		}

		if (otherFaction.isNone())
		{
			if (! Conf.wildernessDenyUseage || Conf.worldsNoWildernessProtection.contains(location.getWorld().getName()))
				return true; // This is not faction territory. Use whatever you
								// like here.

			if (! justCheck)
				me.msg("<b>You can't use that in %s<b>.", Factions.i.getNone().getTag(me));

			return false;
		}
		else if (otherFaction.isSafeZone())
		{
			if (! Conf.safeZoneDenyUseage || Permission.MANAGE_SAFE_ZONE.has(player))
				return true;

			if (item == null)
				return true; // Clicking with air

			if (item != null)
			{
				if (! Conf.safeZoneDenyUseageMaterials.contains(new MyMaterial(item.getType())))
					return true; // Item isn't one we're preventing for
									// safezones.
			}

			if (! justCheck)
				me.msg("<b>You can't use that in %s<b>.", Factions.i.getSafeZone().getTag(me));

			return false;
		}
		else if (otherFaction.isWarZone())
		{
			if (! Conf.warZoneDenyUseage || Permission.MANAGE_WAR_ZONE.has(player))
				return true;

			if (item == null)
				return true; // Clicking with air

			if (! justCheck)
				me.msg("<b>You can't use that in %s<b>.", Factions.i.getWarZone().getTag(me));

			return false;
		}

		Faction myFaction = me.getFaction();
		Relation rel = myFaction.getRelationTo(otherFaction);

		// Cancel if we are not in our own territory
		if (rel.confDenyUseage())
		{
			if (! justCheck)
				me.msg("<b>You can't use that in the territory of <h>%s<b>.", otherFaction.getTag(myFaction));

			return false;
		}

		// Also cancel if player doesn't have ownership rights for this claim
		if (Conf.ownedAreasEnabled && Conf.ownedAreaDenyUseage && ! otherFaction.playerHasOwnershipRights(me, loc))
		{
			if (! justCheck)
			{
				me.msg("<b>You can't use that in this territory, it is owned by: %s<b>.", otherFaction.getOwnerListString(loc));
				return false;
			}
		}

		return true;
	}

	public static boolean canPlayerUseBlock(Player player, Block block, boolean justCheck)
	{
		String name = player.getName();
		if (Conf.playersWhoBypassAllProtection.contains(name))
			return true;

		FPlayer me = FPlayers.i.get(player);
		if (me.isAdminBypassing())
			return true;

		FLocation loc = new FLocation(block);
		Faction otherFaction = Board.getFactionAt(loc);

		// no door/chest/whatever protection in wilderness, war zones, or safe
		// zones
		if (! otherFaction.isNormal())
			return true;

		if (otherFaction.isSafeZone())
		{
			if (Permission.MANAGE_SAFE_ZONE.has(player))
				return true;

			if (block != null)
			{
				if (! Conf.safeZoneProtectedMaterials.contains(new MyMaterial(block.getType())))
					return true; // Block isn't one we're protecting for
									// safezones.
			}

			return false;
		}

		if (otherFaction.isWarZone())
			return true;

		// We only care about some material types.
		if (otherFaction.hasPlayersOnline())
		{
			if (block != null)
			{
				if (! Conf.territoryProtectedMaterials.contains(new MyMaterial(block.getType())))
					return true;
			}
		}
		else
		{
			if (block != null)
			{
				if (! Conf.territoryProtectedMaterialsWhenOffline.contains(new MyMaterial(block.getType())))
					return true;
			}
		}

		Faction myFaction = me.getFaction();
		Relation rel = myFaction.getRelationTo(otherFaction);

		// You may use any block unless it is another faction's territory...
		if (rel.isNeutral() || (rel.isEnemy() && Conf.territoryEnemyProtectMaterials) || (rel.isAlly() && Conf.territoryAllyProtectMaterials)
				|| (rel.isNation() && Conf.territoryNationProtectMaterials))
		{
			if (block != null)
			{
				if (! justCheck)
					me.msg("<b>You can't %s this in the territory of <h>%s<b>.", block.getType() == Material.SOIL ? "trample" : "use",
							otherFaction.getTag(myFaction));
			}
			return false;
		}

		if (block != null)
		{
			if (block.getType() == Material.CHEST)
			{
				if (! me.getFaction().playerHasPermission(me, NPermission.CHEST))
				{
					me.msg("<b>You can't %s this in the territory of <h>%s<b>.", block.getType() == Material.SOIL ? "trample" : "use",
							otherFaction.getTag(myFaction));
					return false;
				}
			}
			if (block.getType() == Material.LEVER || block.getType() == Material.STONE_BUTTON || block.getType() == Material.STONE_PLATE
					|| block.getType() == Material.WOOD_PLATE)
			{
				if (! me.getFaction().playerHasPermission(me, NPermission.SWITCH))
				{
					me.msg("<b>You can't %s this in the territory of <h>%s<b>.", block.getType() == Material.SOIL ? "trample" : "use",
							otherFaction.getTag(myFaction));
					return false;
				}
			}
		}

		// Also cancel if player doesn't have ownership rights for this claim
		if (Conf.ownedAreasEnabled && Conf.ownedAreaProtectMaterials && ! otherFaction.playerHasOwnershipRights(me, loc))
		{
			if (! justCheck)
				me.msg("<b>You can't use this in this territory, it is owned by: %s<b>.", otherFaction.getOwnerListString(loc));

			return false;
		}

		return true;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		FPlayer me = FPlayers.i.get(event.getPlayer());

		me.getPower(); // update power, so they won't have gained any while dead

		Location home = me.getFaction().getHome();
		if (Conf.homesEnabled && Conf.homesTeleportToOnDeath && home != null
				&& (Conf.homesRespawnFromNoPowerLossWorlds || ! Conf.worldsNoPowerLoss.contains(event.getPlayer().getWorld().getName())))
		{
			event.setRespawnLocation(home);
		}
	}

	// For some reason onPlayerInteract() sometimes misses bucket events
	// depending on distance (something like 2-3 blocks away isn't detected),
	// but these separate bucket events below always fire without fail
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
	{
		if (event.isCancelled())
			return;

		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if (! playerCanUseItemHere(player, block.getLocation(), event.getItemStack(), false))
		{
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBucketFill(PlayerBucketFillEvent event)
	{
		if (event.isCancelled())
			return;

		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if (! playerCanUseItemHere(player, block.getLocation(), event.getItemStack(), false))
		{
			event.setCancelled(true);
			return;
		}
	}

	public static boolean preventCommand(String fullCmd, Player player)
	{
		fullCmd = fullCmd.toLowerCase();

		FPlayer me = FPlayers.i.get(player);

		String shortCmd; // command without the slash at the beginning
		if (fullCmd.startsWith("/"))
		{
			shortCmd = fullCmd.substring(1);
		}
		else
		{
			shortCmd = fullCmd;
			fullCmd = "/" + fullCmd;
		}

		if (me.hasFaction() && ! me.isAdminBypassing() && ! Conf.permanentFactionMemberDenyCommands.isEmpty()
				&& me.getFaction().isPermanent() && isCommandInList(fullCmd, shortCmd, Conf.permanentFactionMemberDenyCommands))
		{
			me.msg("<b>You can't use the command \"" + fullCmd + "\" because you are in a permanent faction.");
			return true;
		}

		for (String territorycommands : Conf.ownTerritoryOnlyCommands)
		{
			String[] args = fullCmd.split(" ");
			if (args[0].equalsIgnoreCase("/" + territorycommands) || args[0].equalsIgnoreCase("/e" + territorycommands))
			{
				if (! me.hasFaction())
				{
					me.msg("<b>You need to be in a faction to use this command.");
					return true;
				}

				if (! me.getFaction().hasHome() && ! me.getFaction().hasOutpost())
				{
					me.msg("<b>Please set a faction home first. "
							+ (me.getRole().value < Role.MODERATOR.value ? "<i>Ask your leader to:" : "<i>You should:"));
					me.sendMessage(SwornNations.get().cmdBase.cmdSethome.getUseageTemplate());
					return true;
				}

				if (args.length > 1)
				{
					Player target = Util.matchPlayer(args[1]);
					if (target != null)
					{
						FPlayer fplayer = FPlayers.i.get(target);
						if (fplayer != null)
						{
							if (fplayer.isOnlineAndVisibleTo(player))
							{
								FLocation loc = new FLocation(fplayer);

								boolean isCloseToHome = false;
								boolean isCloseToOutpost = false;

								if (me.getFaction().hasHome())
								{
									FLocation fHome = new FLocation(me.getFaction().getHome());
									isCloseToHome = fHome.getDistanceTo(loc) < 40.0D;
								}

								if (me.getFaction().hasOutpost())
								{
									FLocation outpost = new FLocation(me.getFaction().getOutpost());
									isCloseToOutpost = outpost.getDistanceTo(loc) < 40.0;
								}

								if (! isCloseToHome && ! isCloseToOutpost)
								{
									me.msg("<b>You can't use that command for players outside of 40 chunks from your faction home or outpost");
									return true;
								}
							}
							else
							{
								me.msg("<b>Player <b>%s<b> not found", args[1]);
								return true;
							}
						}
						else
						{
							me.msg("<b>Player <b>%s<b> not found", args[1]);
							return true;
						}
					}
					else
					{
						me.msg("<b>Player <b>%s<b> not found", args[1]);
						return true;
					}
				}
			}
		}

		// A NullPointerException is intermittently thrown here, something about
		// a null FLocation

		try
		{
			if (me == null || me.getPlayer() == null || new FLocation(me) == null)
				return false;
		}
		catch (Exception e)
		{
			return false;
		}

		if (! me.isInOthersTerritory())
			return false;

		Relation rel = me.getRelationToLocation();
		if (rel.isAtLeast(Relation.ALLY))
			return false;

		if (rel.isNeutral() && ! Conf.territoryNeutralDenyCommands.isEmpty() && ! me.isAdminBypassing()
				&& isCommandInList(fullCmd, shortCmd, Conf.territoryNeutralDenyCommands))
		{
			me.msg("<b>You can't use the command \"" + fullCmd + "\" in neutral territory.");
			return true;
		}

		if (rel.isEnemy() && ! Conf.territoryEnemyDenyCommands.isEmpty() && ! me.isAdminBypassing()
				&& isCommandInList(fullCmd, shortCmd, Conf.territoryEnemyDenyCommands))
		{
			me.msg("<b>You can't use the command \"" + fullCmd + "\" in enemy territory.");
			return true;
		}

		return false;
	}

	private static boolean isCommandInList(String fullCmd, String shortCmd, Set<String> list)
	{
		for (String cmdCheck : list)
		{
			cmdCheck = cmdCheck.toLowerCase();
			if (shortCmd.matches(cmdCheck + ".*") || fullCmd.matches(cmdCheck + ".*"))
				return true;
		}

		return false;
	}

	// -------------------------------------------- //
	// Player Homes
	// -------------------------------------------- //

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		String message = event.getMessage().toLowerCase();
		if (message.startsWith("/home") && Conf.playerHomesEnabled)
		{
			event.setCancelled(true);
			SwornNations.get().getServer().getLogger().info(event.getPlayer().getName() + " issued server command: /home");

			FPlayer fme = FPlayers.i.get(event.getPlayer());
			if (! fme.hasHome())
			{
				fme.msg("<b>You do not have a home set. Do /sethome to set one.");
				return;
			}

			if (! fme.hasFaction())
			{
				fme.msg("You must have a faction to have a home.");
				return;
			}

			// Just go ahead and override
			if (Conf.playerHomesOverride)
			{
				fme.goHome();
				return;
			}

			if (Board.getFactionAt(new FLocation(fme.getHome())) != fme.getFaction())
			{
				fme.msg("<b>Your home is no longer in claimed territory and has been unset.");
				fme.removeHome();
				return;
			}

			boolean isCloseToHome = false;
			boolean isCloseToOutpost = false;

			if (fme.getFaction().hasHome())
			{
				FLocation fHome = new FLocation(fme.getFaction().getHome());
				FLocation home = new FLocation(fme.getHome());
				isCloseToHome = fHome.getDistanceTo(home) < 20.0D;
			}

			if (fme.getFaction().hasOutpost())
			{
				FLocation outpost = new FLocation(fme.getFaction().getOutpost());
				FLocation home = new FLocation(fme.getHome());
				isCloseToOutpost = outpost.getDistanceTo(home) < 20.0D;
			}

			if (! isCloseToHome && ! isCloseToOutpost)
			{
				fme.msg("<b>Your home is far away from your faction home or outpost and has been unset.");
				fme.removeHome();
				return;
			}

			if (isEnemyNearby(fme, Board.getFactionAt(new FLocation(fme.getPlayer().getLocation())), fme.getPlayer().getLocation()))
				return;

			fme.goHome();
			return;
		}
		else if (message.startsWith("/sethome") && Conf.playerHomesEnabled)
		{
			event.setCancelled(true);
			SwornNations.get().getServer().getLogger().info(event.getPlayer().getName() + " issued server command: /sethome");

			FPlayer fme = FPlayers.i.get(event.getPlayer());
			if (! fme.hasFaction())
			{
				fme.msg("<b>You must have a faction to do this!");
				return;
			}

			// Just go ahead and override
			if (Conf.playerHomesOverride)
			{
				fme.setHome(fme.getPlayer().getLocation());
				fme.msg("<i>Home set!");
				return;
			}

			if (Board.getFactionAt(new FLocation(fme)) != fme.getFaction())
			{
				fme.msg("<b>Sorry, your home can only be set inside your own claimed territory.");
				return;
			}

			boolean isCloseToHome = false;
			boolean isCloseToOutpost = false;

			if (fme.getFaction().hasHome())
			{
				FLocation fHome = new FLocation(fme.getFaction().getHome());
				FLocation home = new FLocation(fme);
				isCloseToHome = fHome.getDistanceTo(home) < 20.0D;
			}

			if (fme.getFaction().hasOutpost())
			{
				FLocation outpost = new FLocation(fme.getFaction().getOutpost());
				FLocation home = new FLocation(fme);
				isCloseToOutpost = outpost.getDistanceTo(home) < 20.0D;
			}

			if (! isCloseToHome && ! isCloseToOutpost)
			{
				fme.msg("<b>You're too far away from your faction home to set your home.");
				fme.removeHome();
				return;
			}

			fme.setHome(fme.getPlayer().getLocation());
			fme.msg("<i>Home set!");
			return;
		}
	}

	public static boolean isEnemyNearby(FPlayer fme, Faction faction, Location loc)
	{
		if (Conf.homesTeleportAllowedEnemyDistance > 0 && ! faction.isSafeZone()
				&& (! fme.isInOwnTerritory() || (fme.isInOwnTerritory() && ! Conf.homesTeleportIgnoreEnemiesIfInOwnTerritory)))
		{
			World w = loc.getWorld();
			double x = loc.getX();
			double y = loc.getY();
			double z = loc.getZ();

			for (Player p : Util.getOnlinePlayers())
			{
				if (p == null || ! p.isOnline() || p.isDead() || p == fme || p.getWorld() != w)
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInitiateInteract(PlayerInteractEvent event)
	{
		if (event.isCancelled() || ! event.hasBlock())
			return;

		FPlayer fplayer = FPlayers.i.get(event.getPlayer());
		if (! fplayer.hasFaction())
			return;

		Role role = fplayer.getRole();
		if (role != Role.INITIATE)
			return;

		Faction faction = fplayer.getFaction();
		Faction fLoc = Board.getFactionAt(new FLocation(fplayer));
		if (fLoc == null || fLoc != faction)
			return;

		Block block = event.getClickedBlock();
		BlockState state = block.getState();
		if (state instanceof Chest)
		{
			FLocation loc = new FLocation(block);
			if (! faction.doesLocationHaveOwnersSet(loc) || ! faction.playerHasOwnershipRights(fplayer, loc))
			{
				fplayer.msg("<i>You do not have permission to access chests not in your area.");
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockedItemInteract(PlayerInteractEvent event)
	{
		if (event.isCancelled() || ! event.hasItem())
			return;

		FPlayer fme = FPlayers.i.get(event.getPlayer());
		FLocation floc = new FLocation(event.getPlayer());
		Faction fac = Board.getFactionAt(floc);

		if (Conf.ownTerritoryOnlyMaterials.contains(new MyMaterial(event.getItem().getType())))
		{
			if (! canUseBlockedItemHere(floc, fac, fme, false))
			{
				fme.msg("<i>You cannot use this item outside your own territory!");
				event.setCancelled(true);
			}
		}

		if (Conf.ownTerritoryAndWildernessMaterials.contains(new MyMaterial(event.getItem().getType())))
		{
			if (! canUseBlockedItemHere(floc, fac, fme, true))
			{
				fme.msg("<i>You cannot use this item outside your own territory or wilderness!");
				event.setCancelled(true);
			}
		}
	}

	public static boolean canUseBlockedItemHere(FLocation floc, Faction fac, FPlayer pl, boolean both)
	{
		if (fac.isSafeZone() || fac.isWarZone())
			return false;

		if (floc.getY() <= 45)
			return true;

		return (both ? (fac == pl.getFaction() || fac.isNone()) : (fac == pl.getFaction()));
	}

	// -------------------------------------------- //
	// VisualizeUtil
	// -------------------------------------------- //

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMoveClearVisualizations(PlayerMoveEvent event)
	{
		if (event.isCancelled())
			return;

		Block blockFrom = event.getFrom().getBlock();
		Block blockTo = event.getTo().getBlock();
		if (blockFrom.equals(blockTo))
			return;

		VisualizeUtil.clear(event.getPlayer());
	}
}