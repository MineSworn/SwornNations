package com.massivecraft.factions.zcore;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.tasks.SaveTask;
import com.massivecraft.factions.zcore.persist.EM;
import com.massivecraft.factions.zcore.util.PermUtil;
import com.massivecraft.factions.zcore.util.Persist;
import com.massivecraft.factions.zcore.util.TextUtil;

public abstract class MPlugin extends JavaPlugin
{
	// Some utils
	public Persist persist;
	public TextUtil txt;
	public PermUtil perm;

	// Persist related
	public Gson gson;
	private Integer saveTask = null;
	private boolean autoSave = true;
	protected boolean loadSuccessful = false;

	public boolean getAutoSave()
	{
		return this.autoSave;
	}

	public void setAutoSave(boolean val)
	{
		this.autoSave = val;
	}

	public String refCommand = "";

	// Listeners
	private MPluginSecretPlayerListener mPluginSecretPlayerListener;
	private MPluginSecretServerListener mPluginSecretServerListener;

	// Our stored base commands
	private List<MCommand<?>> baseCommands = new ArrayList<MCommand<?>>();

	public List<MCommand<?>> getBaseCommands()
	{
		return this.baseCommands;
	}

	// -------------------------------------------- //
	// ENABLE
	// -------------------------------------------- //
	private long timeEnableStart;

	public boolean preEnable()
	{
		timeEnableStart = System.currentTimeMillis();

		// Ensure basefolder exists!
		this.getDataFolder().mkdirs();

		// Create Utility Instances
		this.perm = new PermUtil(this);
		this.persist = new Persist(this);
		this.gson = this.getGsonBuilder().create();

		this.txt = new TextUtil();
		initTXT();

		// attempt to get first command defined in plugin.yml as reference
		// command, if any commands are defined in there
		// reference command will be used to prevent "unknown command" console
		// messages
		try
		{
			Map<String, Map<String, Object>> refCmd = this.getDescription().getCommands();
			if (refCmd != null && !refCmd.isEmpty())
				this.refCommand = (String) (refCmd.keySet().toArray()[0]);
		}
		catch (ClassCastException ex)
		{
		}

		// Create and register listeners
		this.mPluginSecretPlayerListener = new MPluginSecretPlayerListener(this);
		this.mPluginSecretServerListener = new MPluginSecretServerListener(this);
		getServer().getPluginManager().registerEvents(this.mPluginSecretPlayerListener, this);
		getServer().getPluginManager().registerEvents(this.mPluginSecretServerListener, this);

		// Register recurring tasks
		long saveTicks = 20 * 60 * 30; // Approximately every 30 min
		if (saveTask == null)
		{
			saveTask = new SaveTask(this).runTaskTimer(this, saveTicks, saveTicks).getTaskId();
		}

		loadSuccessful = true;
		return true;
	}

	public void postEnable()
	{
		log("%s has been enabled (%s ms)", getDescription().getFullName(), System.currentTimeMillis() - timeEnableStart);
	}

	@Override
	public void onDisable()
	{
		long start = System.currentTimeMillis();
		
		if (saveTask != null)
		{
			getServer().getScheduler().cancelTask(saveTask);
			saveTask = null;
		}
		
		// only save data if plugin actually loaded successfully
		if (loadSuccessful)
			EM.saveAllToDisc();

		log("%s has been enabled (%s ms)", getDescription().getFullName(), System.currentTimeMillis() - start);
	}

	public void suicide()
	{
		log("Now I suicide!");
		this.getServer().getPluginManager().disablePlugin(this);
	}

	// -------------------------------------------- //
	// Some inits...
	// You are supposed to override these in the plugin if you aren't satisfied
	// with the defaults
	// The goal is that you always will be satisfied though.
	// -------------------------------------------- //

	public GsonBuilder getGsonBuilder()
	{
		return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls()
				.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE);
	}

	// -------------------------------------------- //
	// LANG AND TAGS
	// -------------------------------------------- //

	// These are not supposed to be used directly.
	// They are loaded and used through the TextUtil instance for the plugin.
	public Map<String, String> rawTags = new LinkedHashMap<String, String>();

	public void addRawTags()
	{
		this.rawTags.put("l", "<green>"); // logo
		this.rawTags.put("a", "<gold>"); // art
		this.rawTags.put("n", "<silver>"); // notice
		this.rawTags.put("i", "<yellow>"); // info
		this.rawTags.put("g", "<lime>"); // good
		this.rawTags.put("b", "<rose>"); // bad
		this.rawTags.put("h", "<pink>"); // highligh
		this.rawTags.put("c", "<aqua>"); // command
		this.rawTags.put("p", "<teal>"); // parameter
	}

	public void initTXT()
	{
		this.addRawTags();

		Type type = new TypeToken<Map<String, String>>()
		{
		}.getType();

		Map<String, String> tagsFromFile = this.persist.load(type, "tags");
		if (tagsFromFile != null)
			this.rawTags.putAll(tagsFromFile);
		this.persist.save(this.rawTags, "tags");

		for (Entry<String, String> rawTag : this.rawTags.entrySet())
		{
			this.txt.tags.put(rawTag.getKey(), TextUtil.parseColor(rawTag.getValue()));
		}
	}

	// -------------------------------------------- //
	// COMMAND HANDLING
	// -------------------------------------------- //

	// can be overridden by P method, to provide option
	public boolean logPlayerCommands()
	{
		return true;
	}

	public boolean handleCommand(CommandSender sender, String commandString, boolean testOnly)
	{
		boolean noSlash = true;
		if (commandString.startsWith("/"))
		{
			noSlash = false;
			commandString = commandString.substring(1);
		}

		for (MCommand<?> command : this.getBaseCommands())
		{
			if (noSlash && !command.allowNoSlashAccess)
				continue;

			for (String alias : command.aliases)
			{
				// disallow double-space after alias, so specific commands can
				// be prevented (preventing "f home" won't prevent "f  home")
				if (commandString.startsWith(alias + "  "))
					return false;

				if (commandString.startsWith(alias + " ") || commandString.equalsIgnoreCase(alias))
				{
					List<String> args = new ArrayList<String>(Arrays.asList(commandString.split("\\s+")));
					args.remove(0);
					if (testOnly)
						return true;
					command.execute(sender, args);
					return true;
				}
			}
		}
		return false;
	}

	public boolean handleCommand(CommandSender sender, String commandString)
	{
		return this.handleCommand(sender, commandString, false);
	}

	// -------------------------------------------- //
	// HOOKS
	// -------------------------------------------- //
	public void preAutoSave()
	{

	}

	public void postAutoSave()
	{

	}

	// -------------------------------------------- //
	// LOGGING
	// -------------------------------------------- //
	public void log(String msg)
	{
		log(Level.INFO, msg);
	}

	public void log(String str, Object... args)
	{
		log(Level.INFO, this.txt.parse(str, args));
	}

	public void log(Level level, String str, Object... args)
	{
		log(level, this.txt.parse(str, args));
	}

	public void log(Level level, String msg)
	{
		getLogger().log(level, msg);
	}
}
