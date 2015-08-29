package com.massivecraft.factions.listeners;

import java.util.UnknownFormatConversionException;
import java.util.logging.Level;

import net.dmulloy2.swornnations.SwornNations;
import net.dmulloy2.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.types.ChatMode;

public class FactionsChatListener implements Listener
{
	// This is for handling slashless commands
	// set at lowest priority so Factions gets to them first
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerEarlyChat(AsyncPlayerChatEvent event)
	{
		if (event.isCancelled())
			return;

		Player talkingPlayer = event.getPlayer();
		String msg = event.getMessage();
		FPlayer me = FPlayers.i.get(talkingPlayer);
		ChatMode chat = me.getChatMode();

		// Slashless factions commands need to be handled here if the user isn't
		// in public chat mode
		if (chat != ChatMode.PUBLIC && SwornNations.get().handleCommand(talkingPlayer, msg))
		{
			if (Conf.logPlayerCommands)
				SwornNations.get().log(talkingPlayer.getName() + " issued command: " + msg);
			event.setCancelled(true);
			return;
		}
	}

	// This is for handling insertion of the player's faction tag and faction chat,
	// set at highest priority to give other plugins a chance to modify chat first
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		if (event.isCancelled())
			return;

		Player talkingPlayer = event.getPlayer();
		String msg = event.getMessage();
		String eventFormat = event.getFormat();
		FPlayer me = FPlayers.i.get(talkingPlayer);
		ChatMode chat = me.getChatMode();

		// Handle factions chat channels
		if (chat == ChatMode.FACTION)
		{
			Faction myFaction = me.getFaction();
			String message = String.format(Conf.factionChatFormat, me.describeTo(myFaction), msg);

			// Send message to all applicable players
			for (Player listeningPlayer : event.getRecipients())
			{
				FPlayer you = FPlayers.i.get(listeningPlayer);
				if (you.getFaction() == myFaction)
					you.sendMessage(message);
				else if (you.isSpyingChat())
					you.sendMessage("[FCspy] " + myFaction.getTag() + ": " + message);
			}

			SwornNations.get().getServer().getLogger().info(ChatColor.stripColor("[Faction Chat] " + myFaction.getTag() + ": " + message));
			event.setCancelled(true);
			return;
		}
		else if (chat == ChatMode.ALLIANCE)
		{
			Faction myFaction = me.getFaction();
			String message = String.format(Conf.allianceChatFormat, ChatColor.stripColor(me.getNameAndTag()), msg);

			// Send message to all applicable players
			for (Player listeningPlayer : event.getRecipients())
			{
				FPlayer you = FPlayers.i.get(listeningPlayer);
				if (you.getFaction() == myFaction || myFaction.getRelationTo(you).isAtLeast(Relation.ALLY))
					you.sendMessage(message);
				else if (you.isSpyingChat())
					you.sendMessage("[ACspy] " + message);
			}

			SwornNations.get().getServer().getLogger().info(ChatColor.stripColor("[Alliance Chat] " + message));
			event.setCancelled(true);
			return;
		}
		else if (chat == ChatMode.NATION)
		{
			Faction myFaction = me.getFaction();
			String message = String.format(Conf.nationChatFormat, ChatColor.stripColor(me.getNameAndTag()), msg);

			// Send message to all applicable players
			for (Player listeningPlayer : event.getRecipients())
			{
				FPlayer you = FPlayers.i.get(listeningPlayer);
				if (you.getFaction() == myFaction || myFaction.getRelationTo(you) == Relation.NATION)
					you.sendMessage(message);
				else if (you.isSpyingChat())
					you.sendMessage("[NCspy] " + message);
			}

			SwornNations.get().getServer().getLogger().info(ChatColor.stripColor("[Nation Chat] " + message));
			event.setCancelled(true);
			return;
		}

		// Are we to insert the Faction tag into the format?
		// If we are not to insert it - we are done.
		if (! Conf.chatTagEnabled || Conf.chatTagHandledByAnotherPlugin)
			return;

		/*if (Conf.chatTagHandledByProtocolLib && ProtocolLibFeatures.isEnabled())
		{
			ProtocolLibFeatures.addMessage(ChatColor.stripColor(eventFormat), talkingPlayer);
			return;
		}*/

		if (Conf.chatTagReplaceString.isEmpty() || ! eventFormat.contains(Conf.chatTagReplaceString))
			return;

		String before = ! me.getChatTag().isEmpty() ? Conf.chatTagPrefix : "";
		String key = Conf.chatTagReplaceString;
		String after = ! me.getChatTag().isEmpty() ? Conf.chatTagSuffix : "";

		String nonColoredMsgFormat = eventFormat.replace(key, before + me.getChatTag().trim() + after);

		// Relation Colored?
		if (Conf.chatTagRelationColored)
		{
			// We must choke the standard message and send out individual
			// messages to all players
			// Why? Because the relations will differ.
			event.setCancelled(true);

			for (Player listeningPlayer : event.getRecipients())
			{
				FPlayer you = FPlayers.i.get(listeningPlayer);
				String yourFormat = eventFormat.replace(key, before + me.getChatTag(you).trim() + after);
				try
				{
					listeningPlayer.sendMessage(String.format(yourFormat, talkingPlayer.getDisplayName(), msg));
				}
				catch (UnknownFormatConversionException ex)
				{
					SwornNations.get().log(Level.SEVERE, Util.getUsefulStack(ex, "handling chat message"));
				}
			}

			// Write to the log... We will write the non colored message.
			String nonColoredMsg = ChatColor.stripColor(String.format(nonColoredMsgFormat, talkingPlayer.getDisplayName(), msg));
			SwornNations.get().getServer().getLogger().log(Level.INFO, nonColoredMsg);
		}
		else
		{
			// No relation color.
			event.setFormat(nonColoredMsgFormat);
		}
	}
}