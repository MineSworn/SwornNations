package com.massivecraft.factions;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import me.t7seven7t.factions.util.MyMaterial;
import me.t7seven7t.swornnations.npermissions.NPermission;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class Conf
{
	public static boolean resetAllPerms = true;
	public static List<String> baseCommandAliases = new ArrayList<String>();
	public static List<String> ownTerritoryOnlyCommands = new ArrayList<String>();
	public static boolean allowNoSlashCommand = true;

	public static boolean debug = false;

	// Colors
	public static ChatColor colorMember = ChatColor.GREEN;
	public static ChatColor colorNation = ChatColor.DARK_PURPLE;
	public static ChatColor colorAlly = ChatColor.LIGHT_PURPLE;
	public static ChatColor colorNeutral = ChatColor.WHITE;
	public static ChatColor colorEnemy = ChatColor.RED;

	public static ChatColor colorPeaceful = ChatColor.GOLD;
	public static ChatColor colorWar = ChatColor.DARK_RED;
	// public static ChatColor colorWilderness = ChatColor.DARK_GREEN;

	// Power
	public static double powerPlayerMax = 10.0;
	public static double powerPlayerMin = -10.0;
	public static double powerPlayerStarting = 0.0;
	public static double powerPerMinute = 0.2; // Default health rate... it
												// takes 5 min to heal one power
	public static double powerPerDeath = 3.0; // A death makes you lose 4 power
	public static boolean powerRegenOffline = false; // does player power
														// regenerate even while
														// they're offline?
	public static double powerOfflineLossPerDay = 0.25; // players will lose
														// this much power per
														// day offline
	public static double powerOfflineLossLimit = 0.0; // players will no longer
														// lose power from being
														// offline once their
														// power drops to this
														// amount or less
	public static double powerFactionMax = 0.0; // if greater than 0, the cap on
												// how much power a faction can
												// have (additional power from
												// players beyond that will act
												// as a "buffer" of sorts)

	public static String prefixAdmin = "**";
	public static String prefixCoadmin = "~*";
	public static String prefixMod = "*";
	public static String prefixOfficer = "~";
	public static String prefixInitiate = "-";

	public static int factionTagLengthMin = 3;
	public static int factionTagLengthMax = 7;
	public static boolean factionTagForceUpperCase = true;

	public static boolean newFactionsDefaultOpen = false;

	// when faction membership hits this limit, players will no longer be able
	// to join using /f join; default is 0, no limit
	public static int factionMemberLimit = 0;

	// what faction ID to start new players in when they first join the server;
	// default is 0, "no faction"
	public static String newPlayerStartingFactionID = "0";

	public static boolean showMapFactionKey = true;
	public static boolean showNeutralFactionsOnMap = false;
	public static boolean showEnemyFactionsOnMap = false;

	// Disallow joining/leaving/kicking while power is negative
	public static boolean canLeaveWithNegativePower = true;

	// Configuration for faction-only chat
	public static boolean factionOnlyChat = true;
	// Configuration on the Faction tag in chat messages.
	public static boolean chatTagEnabled = true;
	public static transient boolean chatTagHandledByAnotherPlugin = false;
	public static boolean chatTagRelationColored = true;
	public static String chatTagReplaceString = "[FACTION]";
	public static String chatTagInsertAfterString = "";
	public static String chatTagInsertBeforeString = "";
	public static int chatTagInsertIndex = 1;
	public static boolean chatTagPadBefore = false;
	public static boolean chatTagPadAfter = false;
	public static String chatTagFormat = "%s" + ChatColor.WHITE;
	public static String factionChatFormat = "%s:" + ChatColor.WHITE + " %s";
	public static String allianceChatFormat = ChatColor.LIGHT_PURPLE + "%s:" + ChatColor.WHITE + " %s";
	public static String nationChatFormat = ChatColor.DARK_PURPLE + "%s:" + ChatColor.WHITE + " %s";

	public static double autoLeaveAfterDaysOfInactivity = 14.0;
	public static double autoLeaveRoutineRunsEveryXMinutes = 15.0;
	public static boolean removePlayerDataWhenBanned = false;

	public static boolean autoCleanupClaimsEnabled = true;
	public static double autoCleanupClaimsAfterXHours = 6.0;
	public static double autoCleanupClaimsRunsEveryXMinutes = 15.0;

	public static boolean worldGuardChecking = false;

	// LWC
	public static boolean lwcIntegration = false;
	public static boolean onUnclaimResetLwcLocks = false;
	public static boolean onCaptureResetLwcLocks = false;

	// server logging options
	public static boolean logFactionCreate = true;
	public static boolean logFactionDisband = true;
	public static boolean logFactionJoin = true;
	public static boolean logFactionKick = true;
	public static boolean logFactionLeave = true;
	public static boolean logLandClaims = true;
	public static boolean logLandUnclaims = true;
	public static boolean logMoneyTransactions = true;
	public static boolean logPlayerCommands = true;

	// prevent some potential exploits
	public static boolean handleExploitObsidianGenerators = true;
	public static boolean handleExploitEnderPearlClipping = true;
	public static boolean handleExploitInteractionSpam = false;
	public static boolean handleExploitTNTWaterlog = false;

	public static boolean homeBalanceOverride = false;

	public static boolean homesEnabled = true;
	public static boolean homesMustBeInClaimedTerritory = true;
	public static int homesMustBeGreaterThan = 65;
	public static boolean homesMustBeLastClaimed = true;
	public static boolean homesTeleportToOnDeath = false;
	public static boolean homesRespawnFromNoPowerLossWorlds = true;
	public static boolean homesTeleportCommandEnabled = true;
	public static boolean homesTeleportCommandEssentialsIntegration = true;
	public static boolean homesTeleportCommandSmokeEffectEnabled = true;
	public static float homesTeleportCommandSmokeEffectThickness = 3f;
	public static boolean homesTeleportAllowedFromEnemyTerritory = true;
	public static boolean homesTeleportAllowedFromDifferentWorld = true;
	public static double homesTeleportAllowedEnemyDistance = 32.0;
	public static boolean homesTeleportIgnoreEnemiesIfInOwnTerritory = false;

	public static boolean warpsEnabled = true;
	public static boolean warpsMustBeInClaimedTerritory = false;
	public static boolean warpsTeleportAllowedFromEnemyTerritory = false;
	public static boolean warpsTeleportCommandSmokeEffectEnabled = true;
	public static float warpsTeleportCommandSmokeEffectThickness = 3f;
	public static int warpsDecayTime = 20;
	public static int warpsPowerCostPerPlayerToSet = 1;
	public static int warpsPowerCostToUse = 4;
	public static boolean warpsNotInOtherTerritory = true;

	public static boolean disablePVPBetweenNeutralFactions = false;
	public static boolean disablePVPForFactionlessPlayers = false;
	public static boolean enablePVPAgainstFactionlessInAttackersLand = true;

	public static int noPVPDamageToOthersForXSecondsAfterLogin = 3;

	public static boolean peacefulTerritoryDisablePVP = true;
	public static boolean peacefulTerritoryDisableMonsters = false;
	public static boolean peacefulMembersDisablePowerLoss = true;

	public static boolean permanentFactionsDisableLeaderPromotion = false;

	public static boolean claimsMustBeConnected = true;
	public static boolean claimsCanBeUnconnectedIfOwnedByOtherFaction = true;
	public static int claimsRequireMinFactionMembers = 2;
	public static int claimedLandsMax = 0;

	// if someone is doing a radius claim and the process fails to claim land
	// this many times in a row, it will exit
	public static int radiusClaimFailureLimit = 9;

	public static double considerFactionsReallyOfflineAfterXMinutes = 10.0;

	public static double actionDeniedPainAmount = 0;

	// commands which will be prevented if the player is a member of a permanent
	// faction
	public static Set<String> permanentFactionMemberDenyCommands = new LinkedHashSet<String>();

	// commands which will be prevented when in claimed territory of another
	// faction
	public static Set<String> territoryNeutralDenyCommands = new LinkedHashSet<String>();
	public static Set<String> territoryEnemyDenyCommands = new LinkedHashSet<String>();

	public static int territoryProtectMinimumHeight = 45;
	public static int territoryProtectMaximumHeight = 200;

	public static double territoryShieldFactor = 0.25;
	public static boolean territoryDenyBuild = true;
	public static boolean territoryDenyBuildWhenOffline = true;
	public static boolean territoryPainBuild = false;
	public static boolean territoryPainBuildWhenOffline = false;
	public static boolean territoryDenyUseage = true;
	public static boolean territoryEnemyDenyBuild = true;
	public static boolean territoryEnemyDenyBuildWhenOffline = true;
	public static boolean territoryEnemyPainBuild = false;
	public static boolean territoryEnemyPainBuildWhenOffline = false;
	public static boolean territoryEnemyDenyUseage = true;
	public static boolean territoryEnemyProtectMaterials = true;
	public static boolean territoryNationDenyBuild = true;
	public static boolean territoryNationDenyBuildWhenOffline = true;
	public static boolean territoryNationDenyUseage = true;
	public static boolean territoryNationProtectMaterials = true;
	public static boolean territoryAllyDenyBuild = true;
	public static boolean territoryAllyDenyBuildWhenOffline = true;
	public static boolean territoryAllyPainBuild = false;
	public static boolean territoryAllyPainBuildWhenOffline = false;
	public static boolean territoryAllyDenyUseage = true;
	public static boolean territoryAllyProtectMaterials = true;
	public static boolean territoryBlockCreepers = false;
	public static boolean territoryBlockCreepersWhenOffline = false;
	public static boolean territoryBlockFireballs = false;
	public static boolean territoryBlockFireballsWhenOffline = true;
	public static boolean territoryBlockTNT = false;
	public static boolean territoryBlockTNTWhenOffline = false;
	public static boolean territoryDenyEndermanBlocks = true;
	public static boolean territoryDenyEndermanBlocksWhenOffline = true;

	public static boolean safeZoneDenyBuild = true;
	public static boolean safeZoneDenyUseage = false;
	public static boolean safeZoneBlockTNT = true;
	public static boolean safeZonePreventAllDamageToPlayers = true;
	public static boolean safeZoneDenyEndermanBlocks = true;

	public static boolean warZoneDenyBuild = true;
	public static boolean warZoneDenyUseage = false;
	public static boolean warZoneBlockCreepers = false;
	public static boolean warZoneBlockFireballs = false;
	public static boolean warZoneBlockTNT = true;
	public static boolean warZonePowerLoss = false;
	public static boolean warZoneFriendlyFire = true;
	public static boolean warZoneDenyEndermanBlocks = true;

	public static boolean wildernessDenyBuild = false;
	public static boolean wildernessDenyUseage = false;
	public static boolean wildernessBlockCreepers = false;
	public static boolean wildernessBlockFireballs = false;
	public static boolean wildernessBlockTNT = false;
	public static boolean wildernessPowerLoss = true;
	public static boolean wildernessDenyEndermanBlocks = false;

	// for claimed areas where further faction-member ownership can be defined
	public static boolean ownedAreasEnabled = true;
	public static int ownedAreasLimitPerFaction = 0;
	public static int ownedAreasLimitPerPlayer = 0;
	public static boolean ownedAreasModeratorsCanSet = false;
	public static boolean ownedAreaModeratorsBypass = true;
	public static boolean ownedAreasPlayersCanOnlyClaimOwn = false;
	public static boolean ownedAreaDenyBuild = true;
	public static boolean ownedAreaPainBuild = false;
	public static boolean ownedAreaProtectMaterials = true;
	public static boolean ownedAreaDenyUseage = true;

	public static String ownedLandMessage = "Owner(s): ";
	public static String publicLandMessage = "Public. Use /f owner [name]";
	public static boolean ownedMessageOnBorder = true;
	public static boolean ownedMessageInsideTerritory = true;
	public static boolean ownedMessageByChunk = true;

	public static boolean pistonProtectionThroughDenyBuild = true;

	public static Set<MyMaterial> ownTerritoryOnlyMaterials = new HashSet<MyMaterial>();
	public static Set<MyMaterial> ownTerritoryAndWildernessMaterials = new HashSet<MyMaterial>();

	public static Set<MyMaterial> territoryProtectedMaterials = new HashSet<MyMaterial>();
	public static Set<MyMaterial> territoryDenyUseageMaterials = new HashSet<MyMaterial>();
	public static Set<MyMaterial> territoryProtectedMaterialsWhenOffline = new HashSet<MyMaterial>();
	public static Set<MyMaterial> territoryDenyUseageMaterialsWhenOffline = new HashSet<MyMaterial>();
	public static Set<MyMaterial> safeZoneDenyUseageMaterials = new HashSet<MyMaterial>();
	public static Set<MyMaterial> safeZoneProtectedMaterials = new HashSet<MyMaterial>();

	// public static Set<Material> territoryProtectedMaterials =
	// EnumSet.noneOf(Material.class);
	// public static Set<Material> territoryDenyUseageMaterials =
	// EnumSet.noneOf(Material.class);
	// public static Set<Material> territoryProtectedMaterialsWhenOffline =
	// EnumSet.noneOf(Material.class);
	// public static Set<Material> territoryDenyUseageMaterialsWhenOffline =
	// EnumSet.noneOf(Material.class);

	public static transient Set<EntityType> safeZoneNerfedCreatureTypes = EnumSet.noneOf(EntityType.class);

	// Spout features
	public static boolean spoutFactionTagsOverNames = false; // show faction
																// tags over
																// names over
																// player heads
	public static boolean spoutFactionTitlesOverNames = false; // whether to
																// include
																// player's
																// title in that
	public static boolean spoutFactionAdminCapes = false; // Show capes on
															// faction admins,
															// colored based on
															// the viewer's
															// relation to the
															// target player
	public static boolean spoutFactionModeratorCapes = false; // same, but for
																// faction
																// moderators
	public static int spoutTerritoryDisplayPosition = 1; // permanent territory
															// display, instead
															// of by chat; 0 =
															// disabled, 1 = top
															// left, 2 = top
															// center, 3+ = top
															// right
	public static float spoutTerritoryDisplaySize = 1.0f; // text scale (size)
															// for territory
															// display
	public static boolean spoutTerritoryDisplayShowDescription = false; // whether
																		// to
																		// show
																		// the
																		// faction
																		// description,
																		// not
																		// just
																		// the
																		// faction
																		// tag
	public static boolean spoutTerritoryOwnersShow = false; // show territory
															// owner list as
															// well
	public static boolean spoutTerritoryNoticeShow = false; // show additional
															// brief territory
															// notice near
															// center of screen,
															// to be sure player
															// notices
															// transition
	public static int spoutTerritoryNoticeTop = 40; // how far down the screen
													// to place the additional
													// notice
	public static boolean spoutTerritoryNoticeShowDescription = false; // whether
																		// to
																		// show
																		// the
																		// faction
																		// description
																		// in
																		// the
																		// notice,
																		// not
																		// just
																		// the
																		// faction
																		// tag
	public static float spoutTerritoryNoticeSize = 1.5f; // text scale (size)
															// for notice
	public static float spoutTerritoryNoticeLeaveAfterSeconds = 2.00f; // how
																		// many
																		// seconds
																		// before
																		// the
																		// notice
																		// goes
																		// away
	public static String capeAlly = "https://github.com/MassiveCraft/Factions/raw/master/capes/ally.png";
	public static String capeEnemy = "https://github.com/MassiveCraft/Factions/raw/master/capes/enemy.png";
	public static String capeMember = "https://github.com/MassiveCraft/Factions/raw/master/capes/member.png";
	public static String capeNeutral = "https://github.com/MassiveCraft/Factions/raw/master/capes/neutral.png";
	public static String capePeaceful = "https://github.com/MassiveCraft/Factions/raw/master/capes/peaceful.png";

	// Economy settings
	public static boolean econEnabled = true;
	public static String econUniverseAccount = "";
	public static double econCostClaimWilderness = 30.0;
	public static double econCostClaimFromFactionBonus = 30.0;
	public static double econClaimAdditionalMultiplier = 0.5;
	public static double econClaimRefundMultiplier = 0.7;
	public static double econClaimUnconnectedFee = 0.0;
	public static double econCostCreate = 200.0;
	public static double econCostOwner = 15.0;
	public static double econCostSethome = 30.0;
	public static double ecnCostSetoutpost = 5000.0;
	public static double econCostJoin = 0.0;
	public static double econCostLeave = 0.0;
	public static double econCostKick = 0.0;
	public static double econCostInvite = 0.0;
	public static double econCostHome = 0.0;
	public static double econCostTag = 100.0;
	public static double econCostDesc = 100.0;
	public static double econCostTitle = 0.0;
	public static double econCostList = 0.0;
	public static double econCostMap = 0.0;
	public static double econCostPower = 0.0;
	public static double econCostShow = 0.0;
	public static double econCostOpen = 0.0;
	public static double econCostAlly = 0.0;
	public static double econCostNation = 0.0;
	public static double econCostEnemy = 0.0;
	public static double econCostNeutral = 0.0;
	public static double econCostNoBoom = 0.0;

	// Faction banks, to pay for land claiming and other costs instead of
	// individuals paying for them
	public static boolean bankEnabled = true;
	public static boolean bankMembersCanWithdraw = false; // Have to be at least
															// moderator to
															// withdraw or pay
															// money to another
															// faction
	public static boolean bankFactionPaysCosts = true; // The faction pays for
														// faction command
														// costs, such as
														// sethome
	public static boolean bankFactionPaysLandCosts = true; // The faction pays
															// for land claiming
															// costs.

	// mainly for other plugins/mods that use a fake player to take actions,
	// which shouldn't be subject to our protections
	public static Set<String> playersWhoBypassAllProtection = new LinkedHashSet<String>();

	public static Set<String> worldsNoClaiming = new LinkedHashSet<String>();
	public static Set<String> worldsNoPowerLoss = new LinkedHashSet<String>();
	public static Set<String> worldsIgnorePvP = new LinkedHashSet<String>();
	public static Set<String> worldsNoWildernessProtection = new LinkedHashSet<String>();

	public static Set<String> bannedFactionNames = new LinkedHashSet<String>();

	public static HashSet<NPermission> initiateNationPermissions = new HashSet<NPermission>();
	public static HashSet<NPermission> defaultNationPermissions = new HashSet<NPermission>();
	public static HashSet<NPermission> officerNationPermissions = new HashSet<NPermission>();
	public static HashSet<NPermission> moderatorNationPermissions = new HashSet<NPermission>();
	public static HashSet<NPermission> coadminNationPermissions = new HashSet<NPermission>();

	public static transient int mapHeight = 8;
	public static transient int mapWidth = 39;
	public static transient char[] mapKeyChrs = "\\/#?$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz".toCharArray();

	static
	{
		baseCommandAliases.add("f");

		addPerms();

		bannedFactionNames.add("xivilai");
		ownTerritoryOnlyCommands.add("tpa");

		territoryEnemyDenyCommands.add("home");
		territoryEnemyDenyCommands.add("sethome");
		territoryEnemyDenyCommands.add("spawn");
		territoryEnemyDenyCommands.add("tpahere");
		territoryEnemyDenyCommands.add("tpaccept");
		territoryEnemyDenyCommands.add("tpa");

		safeZoneProtectedMaterials.add(new MyMaterial(Material.DISPENSER));
		safeZoneProtectedMaterials.add(new MyMaterial(Material.DIODE_BLOCK_ON));
		safeZoneProtectedMaterials.add(new MyMaterial(Material.DIODE_BLOCK_OFF));

		safeZoneDenyUseageMaterials.add(new MyMaterial(Material.WATER_BUCKET));
		safeZoneDenyUseageMaterials.add(new MyMaterial(Material.LAVA_BUCKET));
		safeZoneDenyUseageMaterials.add(new MyMaterial(Material.BUCKET));
		safeZoneDenyUseageMaterials.add(new MyMaterial(Material.FLINT_AND_STEEL));

		ownTerritoryOnlyMaterials.add(new MyMaterial(Material.EXPLOSIVE_MINECART));

		territoryProtectedMaterials.add(new MyMaterial(Material.WOODEN_DOOR));
		territoryProtectedMaterials.add(new MyMaterial(Material.TRAP_DOOR));
		territoryProtectedMaterials.add(new MyMaterial(Material.FENCE_GATE));
		territoryProtectedMaterials.add(new MyMaterial(Material.DISPENSER));
		territoryProtectedMaterials.add(new MyMaterial(Material.CHEST));
		territoryProtectedMaterials.add(new MyMaterial(Material.FURNACE));
		territoryProtectedMaterials.add(new MyMaterial(Material.BURNING_FURNACE));
		territoryProtectedMaterials.add(new MyMaterial(Material.DIODE_BLOCK_OFF));
		territoryProtectedMaterials.add(new MyMaterial(Material.DIODE_BLOCK_ON));
		territoryProtectedMaterials.add(new MyMaterial(Material.JUKEBOX));
		territoryProtectedMaterials.add(new MyMaterial(Material.BREWING_STAND));
		territoryProtectedMaterials.add(new MyMaterial(Material.ENCHANTMENT_TABLE));
		territoryProtectedMaterials.add(new MyMaterial(Material.CAULDRON));
		territoryProtectedMaterials.add(new MyMaterial(Material.SOIL));
		territoryProtectedMaterials.add(new MyMaterial(Material.WOOD_PLATE));
		territoryProtectedMaterials.add(new MyMaterial(Material.LEVER));
		territoryProtectedMaterials.add(new MyMaterial(Material.STONE_BUTTON));
		territoryProtectedMaterials.add(new MyMaterial(Material.STONE_PLATE));
		territoryProtectedMaterials.add(new MyMaterial(Material.ENDER_CHEST));

		territoryDenyUseageMaterials.add(new MyMaterial(Material.FLINT_AND_STEEL));
		territoryDenyUseageMaterials.add(new MyMaterial(Material.BUCKET));
		territoryDenyUseageMaterials.add(new MyMaterial(Material.WATER_BUCKET));
		territoryDenyUseageMaterials.add(new MyMaterial(Material.LAVA_BUCKET));

		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.WOODEN_DOOR));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.TRAP_DOOR));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.FENCE_GATE));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.DISPENSER));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.CHEST));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.FURNACE));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.BURNING_FURNACE));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.DIODE_BLOCK_OFF));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.DIODE_BLOCK_ON));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.JUKEBOX));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.BREWING_STAND));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.ENCHANTMENT_TABLE));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.CAULDRON));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.SOIL));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.WOOD_PLATE));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.LEVER));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.STONE_BUTTON));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.STONE_PLATE));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.ENDER_CHEST));

		territoryDenyUseageMaterialsWhenOffline.add(new MyMaterial(Material.FLINT_AND_STEEL));
		territoryDenyUseageMaterialsWhenOffline.add(new MyMaterial(Material.BUCKET));
		territoryDenyUseageMaterialsWhenOffline.add(new MyMaterial(Material.WATER_BUCKET));
		territoryDenyUseageMaterialsWhenOffline.add(new MyMaterial(Material.LAVA_BUCKET));

		safeZoneNerfedCreatureTypes.add(EntityType.BLAZE);
		safeZoneNerfedCreatureTypes.add(EntityType.CAVE_SPIDER);
		safeZoneNerfedCreatureTypes.add(EntityType.CREEPER);
		safeZoneNerfedCreatureTypes.add(EntityType.ENDER_DRAGON);
		safeZoneNerfedCreatureTypes.add(EntityType.ENDERMAN);
		safeZoneNerfedCreatureTypes.add(EntityType.GHAST);
		safeZoneNerfedCreatureTypes.add(EntityType.MAGMA_CUBE);
		safeZoneNerfedCreatureTypes.add(EntityType.PIG_ZOMBIE);
		safeZoneNerfedCreatureTypes.add(EntityType.SILVERFISH);
		safeZoneNerfedCreatureTypes.add(EntityType.SKELETON);
		safeZoneNerfedCreatureTypes.add(EntityType.SPIDER);
		safeZoneNerfedCreatureTypes.add(EntityType.SLIME);
		safeZoneNerfedCreatureTypes.add(EntityType.ZOMBIE);
	}

	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	private static transient Conf i = new Conf();

	public static void load()
	{
		P.p.persist.loadOrSaveDefault(i, Conf.class, "conf");

		if (Conf.resetAllPerms)
		{
			initiateNationPermissions.clear();
			defaultNationPermissions.clear();
			officerNationPermissions.clear();
			moderatorNationPermissions.clear();
			coadminNationPermissions.clear();
			addPerms();
		}
	}

	public static void save()
	{
		P.p.persist.save(i);
	}

	public static void addPerms()
	{
		defaultNationPermissions.add(NPermission.BUILD);
		defaultNationPermissions.add(NPermission.BREAK);
		defaultNationPermissions.add(NPermission.CHEST);
		defaultNationPermissions.add(NPermission.SWITCH);
		defaultNationPermissions.add(NPermission.WARP);

		initiateNationPermissions.addAll(defaultNationPermissions);

		officerNationPermissions.addAll(defaultNationPermissions);
		officerNationPermissions.add(NPermission.INVITE);
		officerNationPermissions.add(NPermission.TITLE);

		moderatorNationPermissions.addAll(officerNationPermissions);
		moderatorNationPermissions.add(NPermission.CLAIM);
		moderatorNationPermissions.add(NPermission.KICK);
		moderatorNationPermissions.add(NPermission.OFFICER);
		moderatorNationPermissions.add(NPermission.SETHOME);
		moderatorNationPermissions.add(NPermission.UNCLAIM);
		moderatorNationPermissions.add(NPermission.TAG);
		moderatorNationPermissions.add(NPermission.NEUTRAL);
		moderatorNationPermissions.add(NPermission.OWNER);
		moderatorNationPermissions.add(NPermission.ALLY);
		moderatorNationPermissions.add(NPermission.ENEMY);
		moderatorNationPermissions.add(NPermission.DESCRIPTION);

		coadminNationPermissions.addAll(moderatorNationPermissions);
		coadminNationPermissions.add(NPermission.MODERATOR);
		coadminNationPermissions.add(NPermission.PERM);
		coadminNationPermissions.add(NPermission.NATION);
		coadminNationPermissions.add(NPermission.SETOUTPOST);
	}
}