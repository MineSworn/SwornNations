package com.massivecraft.factions.struct;

import net.dmulloy2.swornnations.SwornNations;

import org.bukkit.command.CommandSender;

public enum Permission
{
	MANAGE_SAFE_ZONE("managesafezone"), 
	MANAGE_WAR_ZONE("managewarzone"), 
	OWNERSHIP_BYPASS("ownershipbypass"),
	ADMIN("admin"), 
	ADMIN_ANY("admin.any"),
	AUTOCLAIM("autoclaim"), 
	BYPASS("bypass"), 
	CHAT("chat"), 
	CHATSPY("chatspy"),
	CLAIM("claim"), 
	COADMIN("coadmin"),
	COADMIN_ANY("coadmin.any"), 
	CONFIG("config"),
	CREATE("create"),
	DEINVITE("deinvite"),
	DESCRIPTION("description"),
	DISBAND("disband"), 
	DISBAND_ANY("disband.any"), 
	HELP("help"), HOME("home"), 
	INVITE("invite"), 
	JOIN("join"),
	JOIN_ANY("join.any"),
	JOIN_OTHERS("join.others"), 
	KICK("kick"),
	KICK_ANY("kick.any"), 
	LEAVE("leave"),
	LIST("list"),
	LOCK("lock"),
	MAP("map"),
	MOD("mod"),
	MOD_ANY("mod.any"), 
	MONEY_BALANCE("money.balance"), 
	MONEY_BALANCE_ANY("money.balance.any"),
	MONEY_DEPOSIT("money.deposit"), 
	MONEY_WITHDRAW("money.withdraw"),
	MONEY_WITHDRAW_ANY("money.withdraw.any"), 
	MONEY_F2F("money.f2f"), 
	MONEY_F2P("money.f2p"),
	MONEY_P2F("money.p2f"),
	NO_BOOM("noboom"),
	OPEN("open"), 
	OWNER("owner"), 
	OWNERLIST("ownerlist"),
	SET_PEACEFUL("setpeaceful"),
	SET_PERMANENT("setpermanent"), 
	SET_PERMANENTWAR("setpermanentwar"), 
	SET_PERMANENTPOWER("setpermanentpower"), 
	POWERBOOST("powerboost"), 
	POWER("power"),
	POWER_ANY("power.any"), 
	RELATION("relation"), 
	RELATION_ENEMY("relation.enemy"),
	RELATION_ALLY("relation.ally"),
	RELATION_NATION("relation.nation"),
	RELOAD("reload"), 
	SAVE("save"), 
	SETHOME("sethome"), 
	SETHOME_ANY("sethome.any"),
	SETWARP("setwarp"), 
	SHOW("show"), 
	TAG("tag"),
	TITLE("title"),
	UNCLAIM("unclaim"),
	UNCLAIM_ALL("unclaimall"), 
	VERSION("version"), 
	WARP("warp"), 
	OFFICER("officer"),
	OFFICER_ANY("officer.any"),
	PERM("perm"), 
	PERM_SHOW("perm.show"),
	PRUNE("prune"),
	CLEAN("clean"),
	SHOW_ROLES("showroles"), 
	OTHER_HOME("otherfhome"), 
	OUTPOST("outpost"), 
	OUTPOST_OTHERS("outpost.others"), 
	OUTPOST_SET("outpost.set"),
	OUTPOST_SET_ANY("outpost.set.any");

	public final String node;

	Permission(final String node)
	{
		this.node = "factions." + node;
	}

	public boolean has(CommandSender sender, boolean informSenderIfNot)
	{
		return SwornNations.get().perm.has(sender, this.node, informSenderIfNot);
	}

	public boolean has(CommandSender sender)
	{
		return has(sender, false);
	}
}
