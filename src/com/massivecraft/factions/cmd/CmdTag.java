package com.massivecraft.factions.cmd;

import java.util.ArrayList;

import me.t7seven7t.swornnations.npermissions.NPermission;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FactionRenameEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.MiscUtil;

public class CmdTag extends FCommand
{
	public CmdTag()
	{
		this.aliases.add("tag");
		
		this.requiredArgs.add("faction tag");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.TAG.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		senderMustHaveNPermission = NPermission.TAG;
	}
	
	@Override
	public void perform()
	{
		String tag = this.argAsString(0);
		
		// TODO does not first test cover selfcase?
		if (Factions.i.isTagTaken(tag) && ! MiscUtil.getComparisonString(tag).equals(myFaction.getComparisonTag()))
		{
			msg("<b>That tag is already taken");
			return;
		}
		
		if (!fme.isAdminBypassing()) {
			for (String name : Conf.bannedFactionNames) {
				if (name.equalsIgnoreCase(tag)) {
					msg("<b>You cannot use that tag.");
					return;
				}
			}
		}
		
		ArrayList<String> errors = new ArrayList<String>();
		errors.addAll(Factions.validateTag(tag));
		if (errors.size() > 0)
		{
			sendMessage(errors);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
		if ( ! canAffordCommand(Conf.econCostTag, "to change the faction tag")) return;

		// trigger the faction rename event (cancellable)
		FactionRenameEvent renameEvent = new FactionRenameEvent(fme, tag);
		P.p.getServer().getPluginManager().callEvent(renameEvent);
		if(renameEvent.isCancelled()) return;

		// then make 'em pay (if applicable)
		if ( ! payForCommand(Conf.econCostTag, "to change the faction tag", "for changing the faction tag")) return;

		String oldtag = myFaction.getTag();
		myFaction.setTag(tag);
		
		// Inform
		myFaction.msg("%s<i> changed your faction tag to %s<i>.", fme.describeTo(myFaction, true), myFaction.getTag(myFaction));
		for (Faction faction : Factions.i.get())
		{
			if (faction == myFaction)
			{
				continue;
			}
			faction.msg("<i>The faction %s<i> changed their name to %s<i>.", fme.getColorTo(faction)+oldtag, myFaction.getTag(faction));
		}
	}
	
}
