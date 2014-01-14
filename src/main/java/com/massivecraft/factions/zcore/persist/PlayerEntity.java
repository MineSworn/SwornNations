package com.massivecraft.factions.zcore.persist;

import java.util.List;

import org.bukkit.entity.Player;

import com.massivecraft.factions.P;

public class PlayerEntity extends Entity
{
	public Player getPlayer()
	{
		return P.p.getServer().getPlayer(getId());
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
		return isOnline() && (player == null || player.canSee(getPlayer()));
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
		for (String msg : msgs)
			sendMessage(msg);
	}
}