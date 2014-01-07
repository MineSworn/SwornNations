package com.massivecraft.factions.iface;

/**
 * @author dmulloy2
 */

public interface ChatParticipator
{
	public void msg(String str, Object... args);

	public void sendMessage(String str);
}