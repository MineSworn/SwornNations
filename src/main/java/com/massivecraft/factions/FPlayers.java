package com.massivecraft.factions;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

import net.dmulloy2.swornnations.SwornNations;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;

import com.massivecraft.factions.persist.PlayerEntityCollection;
import com.massivecraft.factions.types.Role;

public class FPlayers extends PlayerEntityCollection<FPlayer>
{
	public static FPlayers i = new FPlayers();
	private final SwornNations p = SwornNations.get();

	private FPlayers()
	{
		super(FPlayer.class, new CopyOnWriteArrayList<FPlayer>(), new ConcurrentSkipListMap<String, FPlayer>(String.CASE_INSENSITIVE_ORDER),
				new File(SwornNations.get().getDataFolder(), "players.json"), SwornNations.get().gson);

		this.setCreative(true);
	}

	@Override
	public Type getMapType()
	{
		return new TypeToken<Map<String, FPlayer>>()
		{
		}.getType();
	}

	public FPlayer get(Player player)
	{
		return get(player.getUniqueId());
	}

	public FPlayer get(UUID uuid)
	{
		return get(uuid.toString());
	}

	@Override
	public FPlayer get(String id)
	{
		// UUIDs only!
		if (id.length() == 36)
			return super.get(id);

		// If it's a name, look it up
		for (FPlayer fplayer : get())
			if (fplayer.getName().equalsIgnoreCase(id))
				return fplayer;

		// Not found
		return null;
	}

	public Set<FPlayer> getOnline()
	{
		Set<FPlayer> entities = new HashSet<FPlayer>();
		for (Player player : Bukkit.getServer().getOnlinePlayers())
			entities.add(get(player));

		return entities;
	}

	public void clean()
	{
		for (FPlayer fplayer : get())
		{
			if (! Factions.i.exists(fplayer.getFactionId()))
			{
				p.log("Reset faction data (invalid faction) for player " + fplayer.getName());
				fplayer.resetFactionData(false);
			}
		}
	}

	public void autoLeaveOnInactivityRoutine()
	{
		if (Conf.autoLeaveAfterDaysOfInactivity <= 0.0)
		{
			return;
		}

		long now = System.currentTimeMillis();
		double toleranceMillis = Conf.autoLeaveAfterDaysOfInactivity * 24 * 60 * 60 * 1000;

		for (FPlayer fplayer : FPlayers.i.get())
		{
			if (fplayer.isOffline() && now - fplayer.getLastLoginTime() > toleranceMillis)
			{
				if (Conf.logFactionLeave || Conf.logFactionKick)
					SwornNations.get().log("Player " + fplayer.getName() + " was auto-removed due to inactivity.");

				// if player is faction admin, sort out the faction since he's
				// going away
				if (fplayer.getRole() == Role.ADMIN)
				{
					Faction faction = fplayer.getFaction();
					if (faction != null)
						fplayer.getFaction().promoteNewLeader();
				}

				fplayer.leave(false);
				fplayer.detach();
			}
		}
	}

	public void cleanWildernessPlayers()
	{
		for (FPlayer fplayer : FPlayers.i.get())
		{
			if (fplayer.getFaction().isNone())
			{
				fplayer.detach();
			}
		}
	}
}
