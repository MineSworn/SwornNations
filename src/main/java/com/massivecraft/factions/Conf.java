package com.massivecraft.factions;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.dmulloy2.swornnations.SwornNations;
import net.dmulloy2.swornnations.types.NPermission;
import net.dmulloy2.types.MyMaterial;

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
	public static boolean opsBypassByDefault = true;
	public static boolean convertGoldFactions = true;
	public static boolean cleanOwnerLists = false;

	// Colors
	public static ChatColor colorMember = ChatColor.GREEN;
	public static ChatColor colorNation = ChatColor.DARK_PURPLE;
	public static ChatColor colorAlly = ChatColor.LIGHT_PURPLE;
	public static ChatColor colorEnemy = ChatColor.RED;
	public static ChatColor colorNeutral = ChatColor.WHITE;

	// Power
	public static double powerPlayerMax = 10.0;
	public static double powerPlayerMin = - 10.0;
	public static double powerPlayerStarting = 0.0;
	public static double powerPerMinute = 0.2;
	public static double powerPerDeath = 3.0;
	public static boolean powerRegenOffline = false;
	public static double powerOfflineLossPerDay = 0.25;
	public static double powerOfflineLossLimit = 0.0;
	public static double powerFactionMax = 0.0;

	public static String prefixAdmin = "**";
	public static String prefixCoadmin = "~*";
	public static String prefixMod = "*";
	public static String prefixOfficer = "~";
	public static String prefixInitiate = "-";

	public static int factionTagLengthMin = 3;
	public static int factionTagLengthMax = 7;
	public static boolean factionTagForceUpperCase = false;

	public static boolean newFactionsDefaultOpen = false;

	public static int factionMemberLimit = 0;

	public static String newPlayerStartingFactionID = "0";

	public static boolean showMapFactionKey = true;
	public static boolean showNeutralFactionsOnMap = false;
	public static boolean showEnemyFactionsOnMap = false;

	public static boolean canLeaveWithNegativePower = true;

	// Chat options
	public static boolean factionOnlyChat = true;

	public static boolean chatTagEnabled = true;
	public static transient boolean chatTagHandledByAnotherPlugin = false;
	public static boolean chatTagRelationColored = true;

	public static String chatTagReplaceString = "[FACTION]";
	public static String chatTagPrefix = "";
	public static String chatTagSuffix = "";
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

	public static boolean playerHomesEnabled = true;
	public static boolean playerHomesOverride = false;

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

	public static int radiusClaimFailureLimit = 9;

	public static double considerFactionsReallyOfflineAfterXMinutes = 10.0;

	public static double actionDeniedPainAmount = 0;

	public static Set<String> permanentFactionMemberDenyCommands = new LinkedHashSet<String>();

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

	public static transient Set<EntityType> safeZoneNerfedCreatureTypes = EnumSet.noneOf(EntityType.class);

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

	public static boolean bankEnabled = true;
	public static boolean bankMembersCanWithdraw = false;
	public static boolean bankFactionPaysCosts = true;
	public static boolean bankFactionPaysLandCosts = true;
	public static boolean bankPayOutToDisbander = true;

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
		bannedFactionNames.add("bach");
		bannedFactionNames.add("monk");

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
		territoryProtectedMaterials.add(new MyMaterial(Material.TRAPPED_CHEST));
		territoryProtectedMaterials.add(new MyMaterial(Material.IRON_PLATE));
		territoryProtectedMaterials.add(new MyMaterial(Material.GOLD_PLATE));
		territoryProtectedMaterials.add(new MyMaterial(Material.DROPPER));
		territoryProtectedMaterials.add(new MyMaterial(Material.HOPPER));

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
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.TRAPPED_CHEST));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.IRON_PLATE));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.GOLD_PLATE));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.DROPPER));
		territoryProtectedMaterialsWhenOffline.add(new MyMaterial(Material.HOPPER));

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
		SwornNations.get().persist.loadOrSaveDefault(i, Conf.class, "conf");

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
		SwornNations.get().persist.save(i);
	}

	public static void addPerms()
	{
		defaultNationPermissions.add(NPermission.BUILD);
		defaultNationPermissions.add(NPermission.BREAK);
		defaultNationPermissions.add(NPermission.CHEST);
		defaultNationPermissions.add(NPermission.OUTPOST);
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
		coadminNationPermissions.add(NPermission.INITIATE);
		coadminNationPermissions.add(NPermission.MODERATOR);
		coadminNationPermissions.add(NPermission.PERM);
		coadminNationPermissions.add(NPermission.NATION);
		coadminNationPermissions.add(NPermission.SETOUTPOST);
	}
}