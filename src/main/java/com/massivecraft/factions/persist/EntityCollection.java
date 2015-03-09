package com.massivecraft.factions.persist;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

import net.dmulloy2.io.UUIDFetcher;
import net.dmulloy2.swornnations.SwornNations;
import net.dmulloy2.swornnations.exception.EnableException;
import net.dmulloy2.util.Util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.OfflinePlayer;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.util.DiscUtil;
import com.massivecraft.factions.util.TextUtil;

public abstract class EntityCollection<E extends Entity>
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	// These must be instantiated in order to allow for different configuration
	// (orders, comparators etc)
	private Collection<E> entities;
	protected Map<String, E> id2entity;

	// If the entities are creative they will create a new instance if a non
	// existent id was requested
	private boolean creative;

	public boolean isCreative()
	{
		return creative;
	}

	public void setCreative(boolean creative)
	{
		this.creative = creative;
	}

	// This is the auto increment for the primary key "id"
	private int nextId;

	// This ugly crap is necessary due to java type erasure
	private Class<E> entityClass;

	public abstract Type getMapType(); // This is special stuff for GSON.

	// Info on how to persist
	private Gson gson;

	public Gson getGson()
	{
		return gson;
	}

	public void setGson(Gson gson)
	{
		this.gson = gson;
	}

	private File file;

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	// -------------------------------------------- //
	// CONSTRUCTORS
	// -------------------------------------------- //

	public EntityCollection(Class<E> entityClass, Collection<E> entities, Map<String, E> id2entity, File file, Gson gson, boolean creative)
	{
		this.entityClass = entityClass;
		this.entities = entities;
		this.id2entity = id2entity;
		this.file = file;
		this.gson = gson;
		this.creative = creative;
		this.nextId = 1;

		EM.setEntitiesCollectionForEntityClass(this.entityClass, this);
	}

	public EntityCollection(Class<E> entityClass, Collection<E> entities, Map<String, E> id2entity, File file, Gson gson)
	{
		this(entityClass, entities, id2entity, file, gson, false);
	}

	// -------------------------------------------- //
	// GET
	// -------------------------------------------- //

	public Collection<E> get()
	{
		return entities;
	}

	public Map<String, E> getMap()
	{
		return this.id2entity;
	}

	public E get(String id)
	{
		if (this.creative)
			return this.getCreative(id);
		return id2entity.get(id);
	}

	public E getCreative(String id)
	{
		E e = id2entity.get(id);
		if (e != null)
			return e;
		return this.create(id);
	}

	public boolean exists(String id)
	{
		if (id == null)
			return false;
		return id2entity.get(id) != null;
	}

	public E getBestIdMatch(String pattern)
	{
		String id = TextUtil.getBestStartWithCI(this.id2entity.keySet(), pattern);
		if (id == null)
			return null;
		return this.id2entity.get(id);
	}

	// -------------------------------------------- //
	// CREATE
	// -------------------------------------------- //

	public synchronized E create()
	{
		return this.create(this.getNextId());
	}

	public synchronized E create(String id)
	{
		if (! this.isIdFree(id))
			return null;

		E e = null;
		try
		{
			e = this.entityClass.newInstance();
		}
		catch (Exception ignored)
		{
			ignored.printStackTrace();
		}

		e.setId(id);
		this.entities.add(e);
		this.id2entity.put(e.getId(), e);
		this.updateNextIdForId(id);
		return e;
	}

	// -------------------------------------------- //
	// ATTACH AND DETACH
	// -------------------------------------------- //

	public void attach(E entity)
	{
		if (entity.getId() != null)
			return;
		entity.setId(this.getNextId());
		this.entities.add(entity);
		this.id2entity.put(entity.getId(), entity);
	}

	public void detach(E entity)
	{
		entity.preDetach();
		this.entities.remove(entity);
		this.id2entity.remove(entity.getId());
		entity.postDetach();
	}

	public void detach(String id)
	{
		E entity = this.id2entity.get(id);
		if (entity == null)
			return;
		this.detach(entity);
	}

	public boolean attached(E entity)
	{
		return this.entities.contains(entity);
	}

	public boolean detached(E entity)
	{
		return ! this.attached(entity);
	}

	// -------------------------------------------- //
	// DISC
	// -------------------------------------------- //

	public boolean saveToDisc()
	{
		Map<String, E> entitiesThatShouldBeSaved = new HashMap<String, E>();
		for (E entity : this.entities)
		{
			if (entity.shouldBeSaved())
			{
				entitiesThatShouldBeSaved.put(entity.getId(), entity);
			}
		}

		return this.saveCore(entitiesThatShouldBeSaved);
	}

	private boolean saveCore(Map<String, E> entities)
	{
		return saveCore(file, entities);
	}

	private boolean saveCore(File file, Map<String, E> entities)
	{
		return DiscUtil.writeCatch(file, gson.toJson(entities));
	}

	public boolean loadFromDisc() throws EnableException
	{
		Map<String, E> id2entity = this.loadCore();
		if (id2entity == null)
			return false;
		this.entities.clear();
		this.entities.addAll(id2entity.values());
		this.id2entity.clear();
		this.id2entity.putAll(id2entity);
		this.fillIds();
		return true;
	}

	@SuppressWarnings("unchecked")
	private Map<String, E> loadCore() throws EnableException
	{
		try
		{
			if (! file.exists())
				return new HashMap<String, E>();

			String content = DiscUtil.readCatch(file);
			if (content == null)
				return null;

			if (gson == null)
				gson = SwornNations.get().getGson();

			Type type = getMapType();
			if (type.toString().contains("FPlayer"))
			{
				Map<String, FPlayer> data = gson.fromJson(content, type);
				Set<String> keys = keysRequiringUpdate(data.keySet());
				Set<String> invalid = new HashSet<String>();
				if (keys.size() > 0)
				{
					// Enable caching since we're converting
					UUIDFetcher.setCachingEnabled(true);

					long start = System.currentTimeMillis();
					SwornNations.get().log("Converting players.json to UUID");

					// Back it up
					File backup = new File(file.getParentFile(), "players.json_old");
					if (! backup.exists())
					{
						try
						{
							backup.createNewFile();
						} catch (IOException ex) { }
					}

					saveCore(backup, (Map<String, E>) data);
					SwornNations.get().log("Backed up old players.json to " + backup);

					SwornNations.get().log("Please wait while SwornNations converts %s names to UUID.", keys.size());

					// Remove duplicates
					int duplicates = 0;
					for (Entry<String, FPlayer> entry : new HashMap<String, FPlayer>(data).entrySet())
					{
						FPlayer player = entry.getValue();
						String name = player.getName();
						String uniqueId = player.getUniqueId();

						inner:
						for (Entry<String, FPlayer> entry1 : data.entrySet())
						{
							FPlayer other = entry1.getValue();
							if (! name.equalsIgnoreCase(other.getName()) && uniqueId.equals(other.getUniqueId()))
							{
								// Remove the older one
								if (player.getLastLoginTime() > other.getLastLoginTime())
									data.remove(entry1.getKey());
								else
									data.remove(entry.getKey());

								duplicates++;
								break inner;
							}
						}
					}

					SwornNations.get().log("Removed %s duplicates", duplicates);

					try
					{
						List<String> names = new ArrayList<String>(keys);
						ImmutableList.Builder<List<String>> builder = ImmutableList.builder();
						int namesCopied = 0;
						while (namesCopied < names.size())
						{
							builder.add(ImmutableList.copyOf(names.subList(namesCopied, Math.min(namesCopied + 100, names.size()))));
							namesCopied += 100;
						}

						List<UUIDFetcher> fetchers = new ArrayList<UUIDFetcher>();
						for (List<String> namesList : builder.build())
						{
							fetchers.add(new UUIDFetcher(namesList));
						}

						// Compile it into a master list
						Map<String, UUID> uuids = new HashMap<>();

						ExecutorService e = Executors.newFixedThreadPool(3);
						List<Future<Map<String, UUID>>> results = e.invokeAll(fetchers);
						for (Future<Map<String, UUID>> result : results)
						{
							try
							{
								Map<String, UUID> map = result.get();
								if (map != null)
									uuids.putAll(map);
							}
							catch (Throwable ex)
							{
								SwornNations.get().log(Level.SEVERE, Util.getUsefulStack(ex, "fetching UUIDs from Mojang"));
							}
						}

						// Actually convert the players
						for (Entry<String, FPlayer> entry : new HashMap<>(data).entrySet())
						{
							String name = entry.getKey();
							FPlayer player = entry.getValue();

							UUID uniqueId = uuids.get(name);
							if (uniqueId == null)
							{
								// Attempt to resolve locally
								uniqueId = player.getUUID();
								if (uniqueId == null)
								{
									OfflinePlayer offline = Util.matchOfflinePlayer(name);
									if (offline != null)
										uniqueId = offline.getUniqueId();
								}

							}

							if (uniqueId == null)
							{
								invalid.add(name);
								continue;
							}

							
							player.setId(uniqueId.toString());

							data.remove(name);
							data.put(uniqueId.toString(), player);
						}

						if (invalid.size() > 0)
						{
							for (String name : invalid)
							{
								data.remove(name);
							}

							SwornNations.get().log("Removed %s invalid names: %s", invalid.size(), StringUtils.join(invalid, ", "));
						}
					}
					catch (Throwable ex)
					{
						SwornNations.get().log(Level.SEVERE, Util.getUsefulStack(ex, "converting players to UUID"));
					}

					SwornNations.get().log("Converted players to UUID. Took %s ms.", System.currentTimeMillis() - start);
				}

				return (Map<String, E>) data;
			}
			else if (type.toString().contains("Faction"))
			{
				Map<String, Faction> data = gson.fromJson(content, type);

				Set<String> keys = new HashSet<String>();
				for (Faction faction : data.values())
				{
					keys.addAll(keysRequiringUpdate(faction.getInvites()));
					Map<FLocation, Set<String>> claims = faction.getClaimOwnership();
					for (FLocation key : faction.getClaimOwnership().keySet())
					{
						keys.addAll(keysRequiringUpdate(claims.get(key)));
					}
				}

				if (keys.size() > 0)
				{
					long start = System.currentTimeMillis();
					SwornNations.get().log("Converting factions.json to UUID");

					try
					{
						// Back it up
						File backup = new File(file.getParentFile(), "factions.json_old");
						if (! backup.exists())
						{
							try
							{
								backup.createNewFile();
							} catch (IOException ex) { }
						}

						saveCore(backup, (Map<String, E>) data);
						SwornNations.get().log("Backed up old factions.json to " + backup);

						SwornNations.get().log("Please wait while SwornNations converts %s factions to UUID.", data.size());

						// Fetch UUIDs from Mojang
						Map<String, UUID> uuids = new HashMap<>();

						try
						{
							UUIDFetcher fetcher = new UUIDFetcher(new ArrayList<>(keys));
							uuids = fetcher.call();

							// Convert names to lower case, since that's how they're stored
							for (Entry<String, UUID> entry : new HashMap<>(uuids).entrySet())
							{
								uuids.remove(entry.getKey());
								uuids.put(entry.getKey().toLowerCase(), entry.getValue());
							}
						}
						catch (Throwable ex)
						{
							SwornNations.get().log(Level.SEVERE, Util.getUsefulStack(ex, "fetching UUIDs from Mojang"));
							SwornNations.get().log("Resolving UUIDs locally...");

							for (String key : keys)
							{
								// Attempt to grab from the cache
								UUID uniqueId = UUIDFetcher.fromCache(key);
								if (uniqueId == null)
								{
									OfflinePlayer player = Util.matchOfflinePlayer(key);
									if (player != null)
										uniqueId = player.getUniqueId();
								}

								if (uniqueId != null)
									uuids.put(key.toLowerCase(), uniqueId);
								else
									SwornNations.get().log(Level.WARNING, "Could not resolve UUID for %s", key);
							}
						}

						for (Entry<String, Faction> entry : data.entrySet())
						{
							Faction faction = entry.getValue();

							// Convert claim ownership
							Map<FLocation, Set<String>> claims = faction.getClaimOwnership();
							for (Entry<FLocation, Set<String>> claim : claims.entrySet())
							{
								Set<String> owners = claim.getValue();
								for (String owner : owners.toArray(new String[0]))
								{
									owners.remove(owner.toLowerCase());

									UUID uniqueId = uuids.get(owner);
									if (uniqueId != null)
										owners.add(uniqueId.toString());
								}

								claims.put(claim.getKey(), owners);
							}

							// Convert invites
							Set<String> invites = faction.getInvites();
							for (String invite : invites.toArray(new String[0]))
							{
								invites.remove(invite.toLowerCase());

								UUID uniqueId = uuids.get(invite);
								if (uniqueId != null)
									invites.add(uniqueId.toString());
							}
						}

						SwornNations.get().log("Converted Factions to UUID. Took %s ms.", System.currentTimeMillis() - start);
					}
					catch (Throwable ex)
					{
						SwornNations.get().log(Level.SEVERE, Util.getUsefulStack(ex, "converting Factions to UUID"));
					}

					return (Map<String, E>) data;
				}
			}

			return gson.fromJson(content, type);
		}
		catch (Throwable ex)
		{
			SwornNations.get().log(Level.SEVERE, "Failed to load core.");
			if (gson == null)
				SwornNations.get().log(Level.SEVERE, "Gson does not exist!");

			throw new EnableException("Failed to load core.", ex);
		}
	}

	private Set<String> keysRequiringUpdate(Set<String> keys)
	{
		Set<String> ret = new HashSet<String>();
		for (String key : keys)
		{
			if (key.length() != 36)
			{
				if (key.matches("[a-zA-Z0-9_]{2,16}"))
					ret.add(key);
			}
		}

		return ret;
	}

	// -------------------------------------------- //
	// ID MANAGEMENT
	// -------------------------------------------- //

	public String getNextId()
	{
		while (! isIdFree(this.nextId))
		{
			this.nextId += 1;
		}
		return Integer.toString(this.nextId);
	}

	public boolean isIdFree(String id)
	{
		return ! this.id2entity.containsKey(id);
	}

	public boolean isIdFree(int id)
	{
		return this.isIdFree(Integer.toString(id));
	}

	protected synchronized void fillIds()
	{
		this.nextId = 1;
		for (Entry<String, E> entry : this.id2entity.entrySet())
		{
			String id = entry.getKey();
			E entity = entry.getValue();
			entity.id = id;
			this.updateNextIdForId(id);
		}
	}

	protected synchronized void updateNextIdForId(int id)
	{
		if (this.nextId < id)
		{
			this.nextId = id + 1;
		}
	}

	protected void updateNextIdForId(String id)
	{
		try
		{
			int idAsInt = Integer.parseInt(id);
			this.updateNextIdForId(idAsInt);
		} catch (Throwable ex) { }
	}
}