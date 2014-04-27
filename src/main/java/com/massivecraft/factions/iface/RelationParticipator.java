package com.massivecraft.factions.iface;

import org.bukkit.ChatColor;

import com.massivecraft.factions.types.Relation;

public interface RelationParticipator extends ChatParticipator
{
	public String describeTo(RelationParticipator that);

	public String describeTo(RelationParticipator that, boolean ucfirst);

	public Relation getRelationTo(RelationParticipator that);

	public Relation getRelationTo(RelationParticipator that, boolean ignorePeaceful);

	public ChatColor getColorTo(RelationParticipator to);
}