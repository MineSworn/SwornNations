package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.t7seven7t.swornnations.npermissions.NPermission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.event.FactionRelationEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;

public abstract class FRelationCommand extends FCommand
{
	public Relation targetRelation;
	public Map<Faction, Faction> nationWishes = new HashMap<Faction, Faction>();

	public FRelationCommand()
	{
		super();
		this.requiredArgs.add("faction tag");
		// this.optionalArgs.put("player name", "you");

		this.permission = Permission.RELATION.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform()
	{
		if (targetRelation.equals(Relation.ALLY) && !Permission.RELATION_ALLY.has(sender, true))
			return;

		if (targetRelation.equals(Relation.ENEMY) && !Permission.RELATION_ENEMY.has(sender, true))
			return;

		if (targetRelation.equals(Relation.NATION) && !Permission.RELATION_NATION.has(sender, true))
			return;

		Faction them = this.argAsFaction(0);
		if (them == null)
			return;

		if (!them.isNormal())
		{
			msg("<b>Nope! You can't.");
			return;
		}

		if (them == myFaction)
		{
			msg("<b>Nope! You can't declare a relation to yourself :)");
			return;
		}

		if (myFaction.getRelationWish(them) == targetRelation && targetRelation != Relation.NATION)
		{
			msg("<b>You already have that relation wish set with %s.", them.getTag());
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this
		// command has a cost set, make 'em pay
		if (!payForCommand(targetRelation.getRelationCost(), "to change a relation wish", "for changing a relation wish"))
			return;

		// TODO: Make sure nations cannot be overriden with any other relation
		if (targetRelation.equals(Relation.NATION))
		{
			if (myFaction.getRelationWish(them) != Relation.NATION && myFaction.getRelationTo(them) == Relation.NATION)
			{
				myFaction.setRelationWish(them, targetRelation);
				return;
			}

			if (!canAlly(them))
			{
				msg("<i>You can't enter a nation with " + them.getTag(fme) + "<i> because you are at war with their allies.");
				return;
			}

			if (nationWishes.containsKey(them) && nationWishes.get(them) == myFaction)
			{
				if (nationWishes.get(them) == myFaction)
				{
					myFaction.setRelationWish(them, targetRelation);
					them.setRelationWish(myFaction, targetRelation);
					them.msg("<i>Your faction has now formed a Nation with " + myFaction.getRelationTo(them, true).getColor() + myFaction.getTag());
					myFaction.msg("<i>Your faction has now formed a Nation with " + myFaction.getRelationTo(them, true).getColor() + them.getTag());
					nationWishes.remove(them);
					return;
				}
			}
			else
			{
				nationWishes.put(myFaction, them);
				them.msg(myFaction.getRelationTo(them, true).getColor() + myFaction.getTag() + "<i> wishes to form a Nation with you.");
				them.msg("<i>Type <c>/" + Conf.baseCommandAliases.get(0) + " " + targetRelation + " " + myFaction.getTag() + "<i> to accept.");
				myFaction.msg(myFaction.getRelationTo(them, true).getColor() + them.getTag() + "<i> were informed that you wish to form a Nation with them.");
				return;

			}
		}
		else
		{
			nationWishes.remove(myFaction);
		}

		// try to set the new relation
		Relation oldRelation = myFaction.getRelationTo(them, true);
		if (oldRelation.equals(Relation.NATION) && !myFaction.playerHasPermission(fme, NPermission.NATION))
		{
			msg("<b>You don't have permission to break a Nation with %s.", them.getTag());
			return;
		}

		if (targetRelation == Relation.ENEMY)
			breakAllies(them);

		if (targetRelation == Relation.ALLY && !canAlly(them))
		{
			msg("<i>You can't ally " + them.getTag(fme) + "<i> because you are at war with their allies.");
			return;
		}

		myFaction.setRelationWish(them, targetRelation);
		Relation currentRelation = myFaction.getRelationTo(them, true);
		ChatColor currentRelationColor = currentRelation.getColor();

		// if the relation change was successful
		if (targetRelation.value == currentRelation.value)
		{
			// trigger the faction relation event
			FactionRelationEvent relationEvent = new FactionRelationEvent(myFaction, them, oldRelation, currentRelation);
			Bukkit.getServer().getPluginManager().callEvent(relationEvent);

			them.msg("<i>Your faction is now " + currentRelationColor + targetRelation.toString() + "<i> to " + currentRelationColor + myFaction.getTag());
			myFaction.msg("<i>Your faction is now " + currentRelationColor + targetRelation.toString() + "<i> to " + currentRelationColor + them.getTag());
		}
		// inform the other faction of your request
		else
		{
			them.msg(currentRelationColor + myFaction.getTag() + "<i> wishes to be your " + targetRelation.getColor() + targetRelation.toString());
			them.msg("<i>Type <c>/" + Conf.baseCommandAliases.get(0) + " " + targetRelation + " " + myFaction.getTag() + "<i> to accept.");
			myFaction.msg(currentRelationColor + them.getTag() + "<i> were informed that you wish to be " + targetRelation.getColor() + targetRelation);
		}

		if (!targetRelation.isNeutral() && them.isPeaceful())
		{
			them.msg("<i>This will have no effect while your faction is peaceful.");
			myFaction.msg("<i>This will have no effect while their faction is peaceful.");
		}

		if (!targetRelation.isNeutral() && myFaction.isPeaceful())
		{
			them.msg("<i>This will have no effect while their faction is peaceful.");
			myFaction.msg("<i>This will have no effect while your faction is peaceful.");
		}

		if (!targetRelation.isNeutral() && (myFaction.isPermanentWar() || them.isPermanentWar()))
		{
			them.msg("<i>This will have no effect while " + (them.isPermanentWar() ? "their" : "your") + " faction is a permanent war faction.");
			myFaction.msg("<i>This will have no effect while " + (them.isPermanentWar() ? "their" : "your") + " faction is a permanent war faction.");
		}

		if (targetRelation == Relation.ALLY && fme.getRole() == Role.ADMIN)
		{
			fme.msg("<i>Want a better relationship than ally? Use &e/f nation <i>to form an alliance only broken if both parties agree");
		}
	}

	private boolean breakAllies(Faction them)
	{
		List<Faction> allies = new ArrayList<Faction>();

		for (Faction otherFaction : Factions.i.get())
		{
			if (otherFaction == myFaction || otherFaction == them)
				continue;

			Relation rel = otherFaction.getRelationTo(myFaction);
			if (rel.isAtLeast(Relation.ALLY))
				allies.add(otherFaction);
		}

		boolean result = false;
		for (Faction otherFaction : allies)
		{
			if (otherFaction.getRelationTo(them).isAtLeast(Relation.ALLY))
			{
				result = true;
				otherFaction.setRelationWish(myFaction, Relation.ENEMY);
				myFaction.setRelationWish(otherFaction, Relation.ENEMY);
				otherFaction.msg("<i>Your faction is now &cenemy <i>to &c" + myFaction.getTag() + "<i> because they are at war with "
						+ them.getTag(otherFaction));
				myFaction.msg("<i>Your faction is now &cenemy<i> to &c" + otherFaction.getTag());
			}
		}

		return result;
	}

	private boolean canAlly(Faction them)
	{
		List<Faction> enemies = new ArrayList<Faction>();

		for (Faction otherFaction : Factions.i.get())
		{
			if (otherFaction == myFaction || otherFaction == them)
				continue;

			Relation rel = otherFaction.getRelationTo(myFaction);
			if (rel.isEnemy())
				enemies.add(otherFaction);
		}

		boolean result = true;
		for (Faction otherFaction : enemies)
		{
			if (otherFaction.getRelationTo(them).isAtLeast(Relation.ALLY))
			{
				result = false;
				break;
			}
		}

		return result;
	}
}
