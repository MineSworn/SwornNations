package com.massivecraft.factions.zcore.persist;

import java.util.List;

import org.bukkit.entity.Player;

import com.massivecraft.factions.P;

public class PlayerEntity extends Entity
{
	public Player getPlayer()
	{
		return P.p.getServer().getPlayer(this.getId());
	}

	public boolean isOnline()
	{
		Player player = getPlayer();
		if (player != null)
		{
			return player.isOnline();
		}

		return false;
	}

	// make sure target player should be able to detect that this player is
	// online
	public boolean isOnlineAndVisibleTo(Player player)
	{
		if (player != null)
		{
			Player p = getPlayer();
			return p != null && player.canSee(p);
		}

		return true;
	}

	public boolean isOffline()
	{
		return !isOnline();
	}

	// -------------------------------------------- //
	// Message Sending Helpers
	// -------------------------------------------- //

	public void sendMessage(String msg)
	{
		Player player = this.getPlayer();
		if (player == null)
			return;
		player.sendMessage(msg);
	}

	public void sendMessage(List<String> msgs)
	{
		for (String msg : msgs)
		{
			this.sendMessage(msg);
		}
	}
}