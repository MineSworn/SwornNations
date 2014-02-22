package net.dmulloy2.swornnations;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import net.dmulloy2.swornnations.adapters.MyMaterialTypeAdapter;
import net.dmulloy2.swornnations.adapters.NPermissionManagerTypeAdapter;
import net.dmulloy2.swornnations.types.MyMaterial;
import net.dmulloy2.swornnations.types.NPermissionManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.CmdAutoHelp;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.cmd.MCommand;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.EssentialsFeatures;
import com.massivecraft.factions.integration.LWCFeatures;
import com.massivecraft.factions.integration.WorldGuard;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.listeners.FactionsChatListener;
import com.massivecraft.factions.listeners.FactionsEntityListener;
import com.massivecraft.factions.listeners.FactionsExploitListener;
import com.massivecraft.factions.listeners.FactionsPlayerListener;
import com.massivecraft.factions.listeners.SecretPlayerListener;
import com.massivecraft.factions.listeners.SecretServerListener;
import com.massivecraft.factions.persist.EM;
import com.massivecraft.factions.persist.Persist;
import com.massivecraft.factions.tasks.AutoCleanupTask;
import com.massivecraft.factions.tasks.AutoLeaveTask;
import com.massivecraft.factions.tasks.SaveTask;
import com.massivecraft.factions.types.ChatMode;
import com.massivecraft.factions.types.Relation;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.util.MapFLocToStringSetTypeAdapter;
import com.massivecraft.factions.util.MyLocationTypeAdapter;
import com.massivecraft.factions.util.PermUtil;
import com.massivecraft.factions.util.TextUtil;

/**
 * This plugin was forked from Olof Larsson and Brett Flannigan's original
 * Factions plugin: https://github.com/MassiveCraft/Factions
 * <p>
 * The goal of the SwornFactions fork is to add extra functionality to the
 * original plugin and continue support of the 1.6 branch of Factions for future
 * Minecraft updates.
 * <p>
 * Maintainers:
 * <ul>
 * <li>t7seven7t - 2012 - 2013</li>
 * <li>dmulloy2 - current</li>
 * </ul>
 */

public class SwornNations extends JavaPlugin
{
	private static SwornNations i;
	public static SwornNations get() { return i; }
	
	// Listeners
	public final FactionsPlayerListener playerListener;
	public final FactionsChatListener chatListener;
	public final FactionsEntityListener entityListener;
	public final FactionsExploitListener exploitListener;
	public final FactionsBlockListener blockListener;

	// Persistance related
	private boolean locked = false;

	private String lockReason = "locked by Admin";

	public boolean getLocked()
	{
		return this.locked;
	}

	public void setLocked(boolean val)
	{
		this.locked = val;
		this.setAutoSave(val);
	}

	public String getLockReason()
	{
		return lockReason;
	}

	public void setLockReason(String val)
	{
		this.lockReason = val;
	}

	// MPlugin start

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

	// Seeeecret Listeners
	private SecretPlayerListener mPluginSecretPlayerListener;
	private SecretServerListener mPluginSecretServerListener;

	// Our stored base commands
	private List<MCommand<?>> baseCommands = new ArrayList<MCommand<?>>();

	public List<MCommand<?>> getBaseCommands()
	{
		return this.baseCommands;
	}
	// MPlugin end

	private Integer AutoLeaveTask = null;

	// Commands
	public FCmdRoot cmdBase;
	public CmdAutoHelp cmdAutoHelp;

	public SwornNations()
	{
		i = this;
		this.playerListener = new FactionsPlayerListener();
		this.chatListener = new FactionsChatListener();
		this.entityListener = new FactionsEntityListener();
		this.exploitListener = new FactionsExploitListener();
		this.blockListener = new FactionsBlockListener();
	}

	@Override
	public void onEnable()
	{
		// MPlugin start
		long start = System.currentTimeMillis();
		this.loadSuccessful = false;

		// Ensure basefolder exists!
		getDataFolder().mkdirs();

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
		this.mPluginSecretPlayerListener = new SecretPlayerListener();
		this.mPluginSecretServerListener = new SecretServerListener();
		getServer().getPluginManager().registerEvents(this.mPluginSecretPlayerListener, this);
		getServer().getPluginManager().registerEvents(this.mPluginSecretServerListener, this);

		// Register recurring tasks
		long saveTicks = 20 * 60 * 30; // Approximately every 30 min
		if (saveTask == null)
		{
			saveTask = new SaveTask(this).runTaskTimer(this, saveTicks, saveTicks).getTaskId();
		}
		// MPlugin end

		// Load Conf from disk
		Conf.load();
		FPlayers.i.loadFromDisc();
		Factions.i.loadFromDisc();
		Board.load();

		if (Conf.resetAllPerms)
		{
			for (Faction f : Factions.i.get())
			{
				f.resetPermManager();
			}

			Conf.resetAllPerms = false;
			Factions.i.saveToDisc();
			Conf.save();
		}

		// Standardizes gold factions' enemies and tag
		if (Conf.convertGoldFactions)
		{
			for (Faction f : Factions.i.get())
			{
				if (f.getTag().startsWith(ChatColor.GOLD.toString()))
				{
					f.setGold(true);
					f.setTag(ChatColor.stripColor(f.getTag()));

					for (Entry<String, Relation> relation : f.getRelationWishes().entrySet())
					{
						if (relation.getValue() == Relation.ENEMY)
						{
							f.getRelationWishes().remove(relation.getKey());
						}
					}
				}
			}

			Conf.convertGoldFactions = false;
			Factions.i.saveToDisc();
			Conf.save();
		}

		// Add Base Commands
		this.cmdBase = new FCmdRoot();
		this.cmdAutoHelp = new CmdAutoHelp();
		this.getBaseCommands().add(cmdBase);

		EssentialsFeatures.setup();
		Econ.setup();
		LWCFeatures.setup();

		if (Conf.worldGuardChecking)
		{
			WorldGuard.init(this);
		}

		// start up task which runs the autoRemoveClaimsAfterTime routine
		if (Conf.autoCleanupClaimsEnabled)
			startAutoCleanupTask();

		// start up task which runs the autoLeaveAfterDaysOfInactivity routine
		startAutoLeaveTask(false);

		// Register Listeners
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
		pm.registerEvents(chatListener, this);
		pm.registerEvents(entityListener, this);
		pm.registerEvents(exploitListener, this);
		pm.registerEvents(blockListener, this);

		// Since some other plugins execute commands directly through this
		// command interface, provide it
		getCommand(refCommand).setExecutor(this);

		log("%s has been enabled (%s ms)", getDescription().getFullName(), System.currentTimeMillis() - start);
		this.loadSuccessful = true;
	}

	public GsonBuilder getGsonBuilder()
	{
		Type mapFLocToStringSetType = new TypeToken<Map<FLocation, Set<String>>>(){}.getType();

		return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
				.registerTypeAdapter(LazyLocation.class, new MyLocationTypeAdapter())
				.registerTypeAdapter(MyMaterial.class, new MyMaterialTypeAdapter())
				.registerTypeAdapter(mapFLocToStringSetType, new MapFLocToStringSetTypeAdapter())
				.registerTypeAdapter(NPermissionManager.class, new NPermissionManagerTypeAdapter());
	}

	@Override
	public void onDisable()
	{
		long start = System.currentTimeMillis();
		
		// Only save data if plugin actually completely loaded successfully
		if (this.loadSuccessful)
		{
			Board.save();
			Conf.save();
		}

		if (AutoLeaveTask != null)
		{
			getServer().getScheduler().cancelTask(AutoLeaveTask);
			this.AutoLeaveTask = null;
		}

		if (saveTask != null)
		{
			getServer().getScheduler().cancelTask(saveTask);
			saveTask = null;
		}
		
		// only save data if plugin actually loaded successfully
		if (loadSuccessful)
			EM.saveAllToDisc();

		log("%s has been disabled (%s ms)", getDescription().getFullName(), System.currentTimeMillis() - start);
	}

	// MPlugin Start
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

	public void suicide()
	{
		log("Now I suicide!");
		this.getServer().getPluginManager().disablePlugin(this);
	}
	// MPlugin end

	public void startAutoLeaveTask(boolean restartIfRunning)
	{
		if (AutoLeaveTask != null)
		{
			if (! restartIfRunning)
				return;

			getServer().getScheduler().cancelTask(AutoLeaveTask);
		}

		if (Conf.autoLeaveRoutineRunsEveryXMinutes > 0.0)
		{
			long ticks = (long) (20 * 60 * Conf.autoLeaveRoutineRunsEveryXMinutes);
			AutoLeaveTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoLeaveTask(), ticks, ticks);
		}
	}

	public void startAutoCleanupTask()
	{
		if (Conf.autoCleanupClaimsRunsEveryXMinutes > 0.0)
		{
			long ticks = (long) (20 * 60 * Conf.autoCleanupClaimsRunsEveryXMinutes);
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoCleanupTask(), ticks, ticks);
		}
	}

	public void postAutoSave()
	{
		Board.save();
		Conf.save();
	}

	public boolean logPlayerCommands()
	{
		return Conf.logPlayerCommands;
	}

	public boolean handleCommand(CommandSender sender, String commandString, boolean testOnly)
	{
		if (sender instanceof Player && FactionsPlayerListener.preventCommand(commandString, (Player) sender))
			return true;

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

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split)
	{
		// if bare command at this point, it has already been handled by
		// MPlugin's command listeners
		if (split == null || split.length == 0)
			return true;

		// otherwise, needs to be handled; presumably another plugin directly
		// ran the command
		String cmd = Conf.baseCommandAliases.isEmpty() ? "/f" : "/" + Conf.baseCommandAliases.get(0);
		return handleCommand(sender, cmd + " " + TextUtil.implode(Arrays.asList(split), " "), false);
	}

	// -------------------------------------------- //
	// Functions for other plugins to hook into
	// -------------------------------------------- //

	// This value will be updated whenever new hooks are added
	public int hookSupportVersion()
	{
		return 3;
	}

	// If another plugin is handling insertion of chat tags, this should be used
	// to notify Factions
	public void handleFactionTagExternally(boolean notByFactions)
	{
		Conf.chatTagHandledByAnotherPlugin = notByFactions;
	}

	// Simply put, should this chat event be left for Factions to handle? For
	// now, that means players with Faction Chat
	// enabled or use of the Factions f command without a slash; combination of
	// isPlayerFactionChatting() and isFactionsCommand()

	public boolean shouldLetFactionsHandleThisChat(AsyncPlayerChatEvent event)
	{
		if (event == null)
			return false;

		return isPlayerFactionChatting(event.getPlayer()) || isFactionsCommand(event.getMessage());
	}

	// Does player have Faction Chat enabled? If so, chat plugins should
	// preferably not do channels,
	// local chat, or anything else which targets individual recipients, so
	// Faction Chat can be done
	public boolean isPlayerFactionChatting(Player player)
	{
		if (player == null)
			return false;

		FPlayer me = FPlayers.i.get(player);
		if (me == null)
			return false;

		return me.getChatMode().isAtLeast(ChatMode.ALLIANCE);
	}

	// Is this chat message actually a Factions command, and thus should be left
	// alone by other plugins?

	// TODO: GET THIS BACK AND WORKING

	public boolean isFactionsCommand(String check)
	{
		if (check == null || check.isEmpty())
			return false;

		return handleCommand(null, check, true);
	}

	// Get a player's faction tag (faction name), mainly for usage by chat
	// plugins for local/channel chat
	public String getPlayerFactionTag(Player player)
	{
		return getPlayerFactionTagRelation(player, null);
	}

	// Same as above, but with relation (enemy/neutral/ally) coloring
	// potentially added to the tag
	public String getPlayerFactionTagRelation(Player speaker, Player listener)
	{
		String tag = "~";

		if (speaker == null)
			return tag;

		FPlayer me = FPlayers.i.get(speaker);
		if (me == null)
			return tag;

		// if listener isn't set, or config option is disabled, give back
		// uncolored tag
		if (listener == null || !Conf.chatTagRelationColored)
		{
			tag = me.getChatTag().trim();
		}
		else
		{
			FPlayer you = FPlayers.i.get(listener);
			if (you == null)
				tag = me.getChatTag().trim();
			else
				// everything checks out, give the colored tag
				tag = me.getChatTag(you).trim();
		}

		if (tag.isEmpty())
			tag = "~";

		return tag;
	}

	// Get a player's title within their faction, mainly for usage by chat
	// plugins for local/channel chat
	public String getPlayerTitle(Player player)
	{
		if (player == null)
			return "";

		FPlayer me = FPlayers.i.get(player);
		if (me == null)
			return "";

		return me.getTitle().trim();
	}

	// Get a list of all faction tags (names)
	public Set<String> getFactionTags()
	{
		Set<String> tags = new HashSet<String>();
		for (Faction faction : Factions.i.get())
		{
			tags.add(faction.getTag());
		}

		return tags;
	}

	// Get a list of all players in the specified faction
	public Set<String> getPlayersInFaction(String factionTag)
	{
		Set<String> players = new HashSet<String>();
		Faction faction = Factions.i.getByTag(factionTag);
		if (faction != null)
		{
			for (FPlayer fplayer : faction.getFPlayers())
			{
				players.add(fplayer.getName());
			}
		}

		return players;
	}

	// Get a list of all online players in the specified faction
	public Set<String> getOnlinePlayersInFaction(String factionTag)
	{
		Set<String> players = new HashSet<String>();
		Faction faction = Factions.i.getByTag(factionTag);
		if (faction != null)
		{
			for (FPlayer fplayer : faction.getFPlayersWhereOnline(true))
			{
				players.add(fplayer.getName());
			}
		}

		return players;
	}

	// check if player is allowed to build/destroy in a particular location
	public boolean isPlayerAllowedToBuildHere(Player player, Location location)
	{
		return FactionsBlockListener.playerCanBuildDestroyBlock(player, location, "", true, location.getWorld().getBlockAt(location)
				.getType());
	}

	// check if player is allowed to interact with the specified block
	// (doors/chests/whatever)
	public boolean isPlayerAllowedToInteractWith(Player player, Block block)
	{
		return FactionsPlayerListener.canPlayerUseBlock(player, block, true);
	}

	// check if player is allowed to use a specified item (flint&steel, buckets,
	// etc) in a particular location
	public boolean isPlayerAllowedToUseThisHere(Player player, Location location, ItemStack item)
	{
		return FactionsPlayerListener.playerCanUseItemHere(player, location, item, true);
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