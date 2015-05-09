package com.massivecraft.factions.persist;

import java.io.File;
import java.lang.reflect.Type;
import java.util.logging.Level;

import lombok.AllArgsConstructor;
import net.dmulloy2.swornnations.SwornNations;
import net.dmulloy2.util.Util;

import com.massivecraft.factions.util.DiscUtil;

@AllArgsConstructor
public class Persist
{
	private final SwornNations plugin;

	// ------------------------------------------------------------ //
	// GET NAME - What should we call this type of object?
	// ------------------------------------------------------------ //

	public static String getName(Class<?> clazz)
	{
		return clazz.getSimpleName().toLowerCase();
	}

	public static String getName(Object o)
	{
		return getName(o.getClass());
	}

	public static String getName(Type type)
	{
		return getName(type.getClass());
	}

	// ------------------------------------------------------------ //
	// GET FILE - In which file would we like to store this object?
	// ------------------------------------------------------------ //

	public File getFile(String name)
	{
		return new File(plugin.getDataFolder(), name + ".json");
	}

	public File getFile(Class<?> clazz)
	{
		return getFile(getName(clazz));
	}

	public File getFile(Object obj)
	{
		return getFile(getName(obj));
	}

	public File getFile(Type type)
	{
		return getFile(getName(type));
	}

	// NICE WRAPPERS

	public <T> T loadOrSaveDefault(T def, Class<T> clazz)
	{
		return loadOrSaveDefault(def, clazz, getFile(clazz));
	}

	public <T> T loadOrSaveDefault(T def, Class<T> clazz, String name)
	{
		return loadOrSaveDefault(def, clazz, getFile(name));
	}

	public <T> T loadOrSaveDefault(T def, Class<T> clazz, File file)
	{
		if (! file.exists())
		{
			plugin.log("Creating default: " + file);
			this.save(def, file);
			return def;
		}

		T loaded = this.load(clazz, file);

		if (loaded == null)
		{
			plugin.log(Level.WARNING, "Using default as I failed to load: " + file);

			// backup bad file, so user can attempt to recover their changes
			// from it
			File backup = new File(file.getPath() + "_bad");
			if (backup.exists())
				backup.delete();
			plugin.log(Level.WARNING, "Backing up copy of bad file to: " + backup);
			file.renameTo(backup);

			return def;
		}

		return loaded;
	}

	// SAVE

	public boolean save(Object instance)
	{
		return save(instance, getFile(instance));
	}

	public boolean save(Object instance, String name)
	{
		return save(instance, getFile(name));
	}

	public boolean save(Object instance, File file)
	{
		return DiscUtil.writeCatch(file, plugin.getGson().toJson(instance));
	}

	// LOAD BY CLASS

	public <T> T load(Class<T> clazz)
	{
		return load(clazz, getFile(clazz));
	}

	public <T> T load(Class<T> clazz, String name)
	{
		return load(clazz, getFile(name));
	}

	public <T> T load(Class<T> clazz, File file)
	{
		String content = DiscUtil.readCatch(file);
		if (content == null)
			return null;

		try
		{
			T instance = plugin.getGson().fromJson(content, clazz);
			return instance;
		}
		catch (Throwable ex)
		{
			plugin.log(Level.WARNING, Util.getUsefulStack(ex, "loading file " + file.getName()));
			return null;
		}
	}

	// LOAD BY TYPE
	@SuppressWarnings("unchecked")
	public <T> T load(Type typeOfT, String name)
	{
		return (T) load(typeOfT, getFile(name));
	}

	@SuppressWarnings("unchecked")
	public <T> T load(Type typeOfT, File file)
	{
		String content = DiscUtil.readCatch(file);
		if (content == null)
		{
			return null;
		}

		return (T) plugin.getGson().fromJson(content, typeOfT);
	}

}
