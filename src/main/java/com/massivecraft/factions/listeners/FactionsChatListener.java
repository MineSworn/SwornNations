package com.massivecraft.factions.listeners;

import java.util.UnknownFormatConversionException;
import java.util.logging.Level;

import net.dmulloy2.swornnations.SwornNations;

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
import com.massivecraft.factions.types.ChatMode;
import com.massivecraft.factions.types.Relation;

public class FactionsChatListener implements Listener
{
	// This is for handling slashless command usage
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
		
		// Slashless factions commands need to be handled here if the user isn't in public chat mode
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

		// Is it a faction chat message?
		if (chat == ChatMode.FACTION)
		{
			Faction myFaction = me.getFaction();
			String message = String.format(Conf.factionChatFormat, me.describeTo(myFaction), msg);

			// Send message to all applicable players
			for (Player listeningPlayer : event.getRecipients())
			{
				FPlayer you = FPlayers.i.get(listeningPlayer);
				if (you.getFaction() == myFaction)
				{
					you.sendMessage(message);
				}
			}

			// Send message to our own faction
			// myFaction.sendMessage(message);

			// Send to any players who are spying chat
			for (FPlayer fplayer : FPlayers.i.getOnline())
			{
				if (fplayer.isSpyingChat() && fplayer.getFaction() != myFaction)
					fplayer.sendMessage("[FCspy] " + myFaction.getTag() + ": " + message);
			}

			SwornNations.get().getServer().getLogger().info(ChatColor.stripColor("[Faction Chat] " + myFaction.getTag() + ": " + message));
			// SwornNations.get().log(ChatColor.stripColor("FactionChat " + myFaction.getTag() + ": " + message));

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
				if (you.getFaction() == myFaction || myFaction.getRelationTo(you) == Relation.ALLY
						|| myFaction.getRelationTo(you) == Relation.NATION)
				{
					you.sendMessage(message);
				}
			}

			// Send message to our own faction
			// myFaction.sendMessage(message);

			// Send mesage to anyone spying chat
			for (FPlayer fplayer : FPlayers.i.getOnline())
			{
				// if (myFaction.getRelationTo(fplayer) == Relation.ALLY || myFaction.getRelationTo(fplayer) == Relation.NATION)
				//	fplayer.sendMessage(message);

				if (fplayer.isSpyingChat() && fplayer.getFaction() != myFaction)
					fplayer.sendMessage("[ACspy] " + message);
			}

			SwornNations.get().getServer().getLogger().info(ChatColor.stripColor("[Alliance Chat] " + message));
			// SwornNations.get().log(ChatColor.stripColor("AllianceChat: " + message));

			event.setCancelled(true);
			return;
		}
		else if (chat == ChatMode.NATION)
		{
			Faction myFaction = me.getFaction();
			String message = String.format(Conf.nationChatFormat, ChatColor.stripColor(me.getNameAndTag()), msg);

			// Send message to our own faction
			// myFaction.sendMessage(message);

			// Send message to all applicable players
			for (Player listeningPlayer : event.getRecipients())
			{
				FPlayer you = FPlayers.i.get(listeningPlayer);
				if (you.getFaction() == myFaction || myFaction.getRelationTo(you) == Relation.NATION)
				{
					you.sendMessage(message);
				}
			}

			// Send to chat spys
			for (FPlayer fplayer : FPlayers.i.getOnline())
			{
				// if (myFaction.getRelationTo(fplayer) == Relation.NATION)
				//	fplayer.sendMessage(message);

				if (fplayer.isSpyingChat() && fplayer.getFaction() != myFaction)
					fplayer.sendMessage("[NCspy] " + message);
			}

			SwornNations.get().getServer().getLogger().info(ChatColor.stripColor("[Nation Chat] " + message));
			// SwornNations.get().log(ChatColor.stripColor("NationChat: " + message));

			event.setCancelled(true);
			return;
		}

		// Are we to insert the Faction tag into the format?
		// If we are not to insert it - we are done.
		if (! Conf.chatTagEnabled || Conf.chatTagHandledByAnotherPlugin)
			return;

		int InsertIndex = 0;

		if (! Conf.chatTagReplaceString.isEmpty() && eventFormat.contains(Conf.chatTagReplaceString))
		{
			// we're using the "replace" method of inserting the faction tags
			// if they stuck "[FACTION_TITLE]" in there, go ahead and do it too
			if (eventFormat.contains("[FACTION_TITLE]"))
			{
				eventFormat = eventFormat.replace("[FACTION_TITLE]", me.getTitle());
			}

			InsertIndex = eventFormat.indexOf(Conf.chatTagReplaceString);
			eventFormat = eventFormat.replace(Conf.chatTagReplaceString, "");
			Conf.chatTagPadAfter = false;
			Conf.chatTagPadBefore = false;
		}
		else if (! Conf.chatTagInsertAfterString.isEmpty() && eventFormat.contains(Conf.chatTagInsertAfterString))
		{
			// we're using the "insert after string" method
			InsertIndex = eventFormat.indexOf(Conf.chatTagInsertAfterString) + Conf.chatTagInsertAfterString.length();
		}
		else if (! Conf.chatTagInsertBeforeString.isEmpty() && eventFormat.contains(Conf.chatTagInsertBeforeString))
		{
			// we're using the "insert before string" method
			InsertIndex = eventFormat.indexOf(Conf.chatTagInsertBeforeString);
		}
		else
		{
			// we'll fall back to using the index place method
			InsertIndex = Conf.chatTagInsertIndex;
			if (InsertIndex > eventFormat.length())
				return;
		}

		String formatStart = eventFormat.substring(0, InsertIndex) + ((Conf.chatTagPadBefore && !me.getChatTag().isEmpty()) ? " " : "");
		String formatEnd = ((Conf.chatTagPadAfter && !me.getChatTag().isEmpty()) ? " " : "") + eventFormat.substring(InsertIndex);

		String nonColoredMsgFormat = formatStart + me.getChatTag().trim() + formatEnd;

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
				String yourFormat = formatStart + me.getChatTag(you).trim() + formatEnd;
				try
				{
					listeningPlayer.sendMessage(String.format(yourFormat, talkingPlayer.getDisplayName(), msg));
				}
				catch (UnknownFormatConversionException ex)
				{
					Conf.chatTagInsertIndex = 0;
					SwornNations.get().log(Level.SEVERE, "Critical error in chat message formatting!");
					SwornNations.get().log(Level.SEVERE, "NOTE: This has been automatically fixed right now by setting chatTagInsertIndex to 0.");
					SwornNations.get().log(Level.SEVERE,
							"For a more proper fix, please read this regarding chat configuration: http://massivecraft.com/plugins/factions/config#Chat_configuration");
					return;
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