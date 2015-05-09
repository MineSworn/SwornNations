package com.massivecraft.factions;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

import net.dmulloy2.swornnations.SwornNations;

import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.persist.PlayerEntityCollection;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.TextUtil;

public class FPlayers extends PlayerEntityCollection<FPlayer>
{
	public static FPlayers i = new FPlayers();
	private final SwornNations p = SwornNations.get();

	private FPlayers()
	{
		super(FPlayer.class, new CopyOnWriteArrayList<FPlayer>(), new ConcurrentSkipListMap<String, FPlayer>(String.CASE_INSENSITIVE_ORDER),
				new File(SwornNations.get().getDataFolder(), "players.json"), SwornNations.get().getGson());
		this.setCreative(true);
	}

	@Override
	public FPlayer getBestIdMatch(String pattern)
	{
		@SuppressWarnings("deprecation")
		FPlayer ret = get(pattern);
		if (ret != null)
			return ret;

		Map<String, FPlayer> names = new HashMap<>();
		for (FPlayer player : get())
		{
			names.put(player.getName(), player);
		}

		String id = TextUtil.getBestStartWithCI(names.keySet(), pattern);
		return id != null ? names.get(id) : null;
	}

	@Override
	public Type getMapType()
	{
		return new TypeToken<Map<String, FPlayer>>() { }.getType();
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
			return;

		long now = System.currentTimeMillis();
		double toleranceMillis = Conf.autoLeaveAfterDaysOfInactivity * 24 * 60 * 60 * 1000;

		for (FPlayer fplayer : FPlayers.i.get())
		{
			try
			{
				if (fplayer.isOffline() && now - fplayer.getLastLoginTime() > toleranceMillis)
				{
					if (Conf.logFactionLeave || Conf.logFactionKick)
						SwornNations.get().log("Player " + fplayer.getName() + " was auto-removed due to inactivity.");

					// if player is faction admin, sort out the faction since he's going away
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
			catch (IllegalArgumentException ex)
			{
				// Probably an npc, disregard them
				fplayer.detach();
			}
		}
	}

	public void cleanWildernessPlayers()
	{
		for (FPlayer fplayer : FPlayers.i.get())
		{
			if (fplayer.getFaction().isNone())
				fplayer.detach();
		}
	}
}
