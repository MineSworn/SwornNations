package com.massivecraft.factions.types;

import net.dmulloy2.swornnations.SwornNations;

import org.bukkit.command.CommandSender;

public enum Permission
{
	ADMIN("admin"),
	ADMIN_ANY("admin.any"),
	AUTOCLAIM("autoclaim"),
	BYPASS("bypass"),
	CHAT("chat"),
	CHATSPY("chatspy"),
	CLAIM("claim"),
	CLEAN("clean"),
	COADMIN("coadmin"),
	COADMIN_ANY("coadmin.any"),
	CONFIG("config"),
	CREATE("create"),
	DEINVITE("deinvite"),
	DESCRIPTION("description"),
	DISBAND("disband"),
	DISBAND_ANY("disband.any"),
	HELP("help"),
	HOME("home"),
	INVITE("invite"), JOIN("join"),
	JOIN_ANY("join.any"),
	JOIN_OTHERS("join.others"),
	KICK("kick"),
	KICK_ANY("kick.any"),
	LEAVE("leave"),
	LIST("list"),
	LOCK("lock"),
	MANAGE_SAFE_ZONE("managesafezone"),
	MANAGE_WAR_ZONE("managewarzone"),
	MAP("map"),
	MOD("mod"),
	MOD_ANY("mod.any"),
	MONEY_BALANCE("money.balance"),
	MONEY_BALANCE_ANY("money.balance.any"),
	MONEY_DEPOSIT("money.deposit"),
	MONEY_F2F("money.f2f"),
	MONEY_F2P("money.f2p"),
	MONEY_P2F("money.p2f"),
	MONEY_WITHDRAW("money.withdraw"),
	MONEY_WITHDRAW_ANY("money.withdraw.any"),
	NO_BOOM("noboom"),
	OFFICER("officer"),
	OFFICER_ANY("officer.any"),
	OPEN("open"),
	OTHER_HOME("otherfhome"),
	OUTPOST("outpost"),
	OUTPOST_OTHERS("outpost.others"),
	OUTPOST_SET("outpost.set"),
	OUTPOST_SET_ANY("outpost.set.any"),
	OWNER("owner"),
	OWNERLIST("ownerlist"),
	OWNERSHIP_BYPASS("ownershipbypass"),
	PERM("perm"),
	PERM_SHOW("perm.show"),
	POWER("power"),
	POWER_ANY("power.any"),
	POWERBOOST("powerboost"),
	PRUNE("prune"),
	RELATION("relation"),
	RELATION_ALLY("relation.ally"),
	RELATION_ENEMY("relation.enemy"),
	RELATION_NATION("relation.nation"),
	RELOAD("reload"),
	SAVE("save"),
	SEE_CHUNK("seechunk"),
	SET_GOLD("setgold"),
	SET_PEACEFUL("setpeaceful"),
	SET_PERMANENT("setpermanent"),
	SET_PERMANENTPOWER("setpermanentpower"),
	SET_PERMANENTWAR("setpermanentwar"),
	SETHOME("sethome"),
	SETHOME_ANY("sethome.any"),
	SETWARP("setwarp"),
	SHOW("show"),
	SHOW_ROLES("showroles"),
	TAG("tag"),
	TITLE("title"),
	UNCLAIM("unclaim"),
	UNCLAIM_ALL("unclaimall"),
	VERSION("version"),
	WARP("warp");

	public final String node;

	Permission(final String node)
	{
		this.node = "factions." + node;
	}

	public boolean has(CommandSender sender)
	{
		return has(sender, false);
	}

	public boolean has(CommandSender sender, boolean informSenderIfNot)
	{
		return SwornNations.get().perm.has(sender, this.node, informSenderIfNot);
	}
}
