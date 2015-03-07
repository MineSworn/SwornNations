package com.massivecraft.factions.persist;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.dmulloy2.util.Util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.google.gson.Gson;

/**
 * The PlayerEntityCollection is an EntityCollection with the extra features a
 * player skin usually requires. This entity collection is not only creative. It
 * even creates the instance for the player when the player logs in to the
 * server. This way we can be sure that PlayerEntityCollection.get() will
 * contain all entities in PlayerEntityCollection.getOnline()
 */
public abstract class PlayerEntityCollection<E extends Entity> extends EntityCollection<E>
{
	public PlayerEntityCollection(Class<E> entityClass, Collection<E> entities, Map<String, E> id2entity, File file, Gson gson)
	{
		super(entityClass, entities, id2entity, file, gson, true);
	}

	public E get(Player player)
	{
		return super.get(player.getUniqueId().toString());
	}

	public E get(OfflinePlayer player)
	{
		return super.get(player.getUniqueId().toString());
	}

	@Override
	@Deprecated
	public E get(String id)
	{
		// Ensure it's a UUID
		if (id.length() != 36)
		{
			OfflinePlayer player = Util.matchOfflinePlayer(id);
			if (player != null)
				id = player.getUniqueId().toString();
		}

		return super.get(id);
	}

	@Override
	@Deprecated
	public E getCreative(String id)
	{
		return super.getCreative(id);
	}

	@Override
	@Deprecated
	public synchronized E create(String id)
	{
		return super.create(id);
	}

	@Override
	@Deprecated
	public E getBestIdMatch(String pattern)
	{
		return super.getBestIdMatch(pattern);
	}

	public Set<E> getOnline()
	{
		Set<E> online = new HashSet<>();

		for (Player player : Util.getOnlinePlayers())
		{
			online.add(get(player));
		}

		return online;
	}
}