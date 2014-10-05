package com.massivecraft.factions.cmd;

import java.util.Collections;

import com.massivecraft.factions.Conf;

public class FCmdRoot extends FCommand
{
	public CmdAdmin cmdAdmin = new CmdAdmin();
	public CmdAutoClaim cmdAutoClaim = new CmdAutoClaim();
	public CmdBoom cmdBoom = new CmdBoom();
	public CmdBypass cmdBypass = new CmdBypass();
	public CmdChat cmdChat = new CmdChat();
	public CmdChatSpy cmdChatSpy = new CmdChatSpy();
	public CmdClaim cmdClaim = new CmdClaim();
	public CmdClean cmdClean = new CmdClean();
	public CmdCoadmin cmdCoadmin = new CmdCoadmin();
	public CmdConfig cmdConfig = new CmdConfig();
	public CmdCreate cmdCreate = new CmdCreate();
	public CmdDeinvite cmdDeinvite = new CmdDeinvite();
	public CmdDescription cmdDescription = new CmdDescription();
	public CmdDisband cmdDisband = new CmdDisband();
	public CmdGold cmdGold = new CmdGold();
	public CmdHelp cmdHelp = new CmdHelp();
	public CmdHome cmdHome = new CmdHome();
	public CmdInitiate cmdInitiate = new CmdInitiate();
	public CmdInvite cmdInvite = new CmdInvite();
	public CmdJoin cmdJoin = new CmdJoin();
	public CmdKick cmdKick = new CmdKick();
	public CmdLeave cmdLeave = new CmdLeave();
	public CmdList cmdList = new CmdList();
	public CmdLock cmdLock = new CmdLock();
	public CmdMap cmdMap = new CmdMap();
	public CmdMod cmdMod = new CmdMod();
	public CmdMoney cmdMoney = new CmdMoney();
	public CmdMotd cmdMotd = new CmdMotd();
	public CmdOfficer cmdOfficer = new CmdOfficer();
	public CmdOpen cmdOpen = new CmdOpen();
	public CmdOutpost cmdOutpost = new CmdOutpost();
	public CmdOwner cmdOwner = new CmdOwner();
	public CmdOwnerList cmdOwnerList = new CmdOwnerList();
	public CmdPeaceful cmdPeaceful = new CmdPeaceful();
	public CmdPerm cmdPerm = new CmdPerm();
	public CmdPermanent cmdPermanent = new CmdPermanent();
	public CmdPermanentPower cmdPermanentPower = new CmdPermanentPower();
	public CmdPermanentWar cmdPermanentWar = new CmdPermanentWar();
	public CmdPower cmdPower = new CmdPower();
	public CmdPowerBoost cmdPowerBoost = new CmdPowerBoost();
	public CmdPruneClaims cmdPruneClaims = new CmdPruneClaims();
	public CmdRelationAlly cmdRelationAlly = new CmdRelationAlly();
	public CmdRelationEnemy cmdRelationEnemy = new CmdRelationEnemy();
	public CmdRelationNation cmdRelationNation = new CmdRelationNation();
	public CmdRelationNeutral cmdRelationNeutral = new CmdRelationNeutral();
	public CmdReload cmdReload = new CmdReload();
	public CmdSafeunclaimall cmdSafeunclaimall = new CmdSafeunclaimall();
	public CmdSaveAll cmdSaveAll = new CmdSaveAll();
	public CmdSeeChunk cmdSeeChunk = new CmdSeeChunk();
	public CmdSethome cmdSethome = new CmdSethome();
	public CmdSetoutpost cmdSetoutpost = new CmdSetoutpost();
	public CmdSetwarp cmdSetwarp = new CmdSetwarp();
	public CmdShow cmdShow = new CmdShow();
	public CmdShowPerms cmdShowPerms = new CmdShowPerms();
	public CmdShowRoles cmdShowRoles = new CmdShowRoles();
	public CmdTag cmdTag = new CmdTag();
	public CmdTitle cmdTitle = new CmdTitle();
	public CmdUnclaim cmdUnclaim = new CmdUnclaim();
	public CmdUnclaimall cmdUnclaimall = new CmdUnclaimall();
	public CmdVersion cmdVersion = new CmdVersion();
	public CmdWarp cmdWarp = new CmdWarp();
	public CmdWarunclaimall cmdWarunclaimall = new CmdWarunclaimall();

	public FCmdRoot()
	{
		super();
		this.aliases.addAll(Conf.baseCommandAliases);
		this.aliases.removeAll(Collections.singletonList(null));
		this.allowNoSlashAccess = Conf.allowNoSlashCommand;

		// this.requiredArgs.add("");
		// this.optionalArgs.put("", "");

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;

		this.disableOnLock = false;

		this.setHelpShort("The faction base command");
		this.helpLong.add(p.txt.parseTags("<i>This command contains all faction stuff."));

		// this.subCommands.add(p.cmdHelp);

		this.addSubCommand(this.cmdAdmin);
		this.addSubCommand(this.cmdAutoClaim);
		this.addSubCommand(this.cmdBoom);
		this.addSubCommand(this.cmdBypass);
		this.addSubCommand(this.cmdChat);
		this.addSubCommand(this.cmdChatSpy);
		this.addSubCommand(this.cmdClaim);
		this.addSubCommand(this.cmdCoadmin);
		this.addSubCommand(this.cmdConfig);
		this.addSubCommand(this.cmdCreate);
		this.addSubCommand(this.cmdDeinvite);
		this.addSubCommand(this.cmdDescription);
		this.addSubCommand(this.cmdDisband);
		this.addSubCommand(this.cmdHelp);
		this.addSubCommand(this.cmdHome);
		this.addSubCommand(this.cmdInvite);
		this.addSubCommand(this.cmdJoin);
		this.addSubCommand(this.cmdKick);
		this.addSubCommand(this.cmdLeave);
		this.addSubCommand(this.cmdList);
		this.addSubCommand(this.cmdLock);
		this.addSubCommand(this.cmdMap);
		this.addSubCommand(this.cmdMod);
		this.addSubCommand(this.cmdMoney);
		this.addSubCommand(this.cmdOfficer);
		this.addSubCommand(this.cmdOpen);
		this.addSubCommand(this.cmdOwner);
		this.addSubCommand(this.cmdOwnerList);
		this.addSubCommand(this.cmdPeaceful);
		this.addSubCommand(this.cmdPerm);
		this.addSubCommand(this.cmdPermanent);
		this.addSubCommand(this.cmdPermanentPower);
		this.addSubCommand(this.cmdPermanentWar);
		this.addSubCommand(this.cmdPower);
		this.addSubCommand(this.cmdPowerBoost);
		this.addSubCommand(this.cmdPruneClaims);
		this.addSubCommand(this.cmdRelationAlly);
		this.addSubCommand(this.cmdRelationEnemy);
		this.addSubCommand(this.cmdRelationNation);
		this.addSubCommand(this.cmdRelationNeutral);
		this.addSubCommand(this.cmdReload);
		this.addSubCommand(this.cmdSafeunclaimall);
		this.addSubCommand(this.cmdSaveAll);
		this.addSubCommand(this.cmdSeeChunk);
		this.addSubCommand(this.cmdSethome);
		this.addSubCommand(this.cmdSetwarp);
		this.addSubCommand(this.cmdShow);
		this.addSubCommand(this.cmdShowPerms);
		this.addSubCommand(this.cmdShowRoles);
		this.addSubCommand(this.cmdTag);
		this.addSubCommand(this.cmdTitle);
		this.addSubCommand(this.cmdUnclaim);
		this.addSubCommand(this.cmdUnclaimall);
		this.addSubCommand(this.cmdVersion);
		this.addSubCommand(this.cmdWarp);
		this.addSubCommand(this.cmdWarunclaimall);
		this.addSubCommand(this.cmdClean);
		this.addSubCommand(this.cmdInitiate);
		this.addSubCommand(this.cmdOutpost);
		this.addSubCommand(this.cmdSetoutpost);
		this.addSubCommand(this.cmdGold);
		this.addSubCommand(this.cmdMotd);
	}

	@Override
	public void perform()
	{
		this.commandChain.add(this);
		this.cmdHelp.execute(this.sender, this.args, this.commandChain);
	}
}