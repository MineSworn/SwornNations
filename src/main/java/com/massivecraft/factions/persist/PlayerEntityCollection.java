package com.massivecraft.factions.persist;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;

/**
 * The PlayerEntityCollection is an EntityCollection with the extra features a
 * player skin usually requires. This entity collection is not only creative. It
 * even creates the instance for the player when the player logs in to the
 * server. This way we can be sure that PlayerEntityCollection.get() will
 * contain all entities in PlayerEntityCollection.getOnline()
 * 
 * Most of these features have been moved to FPlayers, for UUID purposes ~dmulloy2
 */
public abstract class PlayerEntityCollection<E extends Entity> extends EntityCollection<E>
{
	public PlayerEntityCollection(Class<E> entityClass, Collection<E> entities, Map<String, E> id2entity, File file, Gson gson)
	{
		super(entityClass, entities, id2entity, file, gson, true);
	}
}
