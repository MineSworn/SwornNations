package com.massivecraft.factions.persist;

import java.util.List;

import net.dmulloy2.util.Util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public abstract class PlayerEntity extends Entity
{
	public final Player getPlayer()
	{
		String id = getId();
		return id != null ? Util.matchPlayer(id) : null;
	}

	public final OfflinePlayer getOfflinePlayer()
	{
		String id = getId();
		return id != null ? Util.matchOfflinePlayer(id) : null;
	}

	// -------------------------------------------- //
	// Visibility
	// -------------------------------------------- //

	public boolean isOnline()
	{
		Player player = getPlayer();
		return player != null && player.isOnline();
	}

	public boolean isOffline()
	{
		return ! isOnline();
	}

	public boolean isOnlineAndVisibleTo(Player player)
	{
		Player me = getPlayer();
		return me != null && me.isOnline() && (player == null || player.canSee(me));
	}

	// -------------------------------------------- //
	// Message Sending
	// -------------------------------------------- //

	public void sendMessage(String msg)
	{
		Player player = getPlayer();
		if (player == null)
			return;

		player.sendMessage(msg);
	}

	public void sendMessage(List<String> msgs)
	{
		Player player = getPlayer();
		if (player == null)
			return;

		for (String msg : msgs)
			player.sendMessage(msg);
	}

	public abstract String getUniqueId();

	public abstract String getName();
}