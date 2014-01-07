package com.massivecraft.factions.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

public class LandUnclaimAllEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	private Faction faction;
	private FPlayer fplayer;

	public LandUnclaimAllEvent(Faction f, FPlayer p)
	{
		faction = f;
		fplayer = p;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	public Faction getFaction()
	{
		return faction;
	}

	public String getFactionId()
	{
		return faction.getId();
	}

	public String getFactionTag()
	{
		return faction.getTag();
	}

	public FPlayer getFPlayer()
	{
		return fplayer;
	}

	public Player getPlayer()
	{
		return fplayer.getPlayer();
	}
}
