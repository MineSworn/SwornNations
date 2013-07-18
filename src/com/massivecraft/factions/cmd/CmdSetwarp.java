package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;

public class CmdSetwarp extends FCommand {

	public CmdSetwarp() {
		this.aliases.add("setwarp");
		
		this.permission = Permission.SETWARP.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = true;
	}
	
	@Override
	public void perform() {
		
		if (!Conf.warpsEnabled) {
			fme.msg("<b>Sorry, Faction warps are disabled on this server.");
			return;
		}
		
		Faction faction = this.argAsFaction(0, myFaction);
		if (faction == null) return;

		if (Conf.warpsMustBeInClaimedTerritory && Board.getFactionAt(new FLocation(me)) != faction) {
			fme.msg("<b>Sorry, your faction warp can only be set inside your own claimed territory.");
			return;
		}
		
		if (Conf.warpsNotInOtherTerritory && (!Board.getFactionAt(new FLocation(me)).isNone()) && !(Board.getFactionAt(new FLocation(me)) == faction)) {
			fme.msg("<b>Sorry,  your faction warp cannot be set in other claimed territory.");
			return;
		}
		
		if (!payPowerForCommandF(Conf.warpsPowerCostPerPlayerToSet)) return;

		faction.setWarp(me.getLocation());
		
		fme.msg("<i>You just set the warp for your faction. It cost your faction %s power for each player and will cost %s power each time you use it.", Conf.warpsPowerCostPerPlayerToSet, Conf.warpsPowerCostToUse);
		fme.msg("<i>You have %s minutes before the warp disappears.", Conf.warpsDecayTime);
		faction.msg("%s<i> set the warp for your faction. You can now use:", fme.describeTo(myFaction, true));
		faction.sendMessage(p.cmdBase.cmdWarp.getUseageTemplate());
	}

}
