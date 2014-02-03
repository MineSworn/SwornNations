package com.massivecraft.factions.cmd;

import net.dmulloy2.swornnations.SwornNations;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;

public class CmdMoneyTransferPf extends FCommand
{
	public CmdMoneyTransferPf()
	{
		this.aliases.add("pf");

		this.requiredArgs.add("amount");
		this.requiredArgs.add("player");
		this.requiredArgs.add("faction");

		// this.optionalArgs.put("", "");

		this.permission = Permission.MONEY_P2F.node;
		this.setHelpShort("transfer p -> f");

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform()
	{
		double amount = this.argAsDouble(0, 0d);
		EconomyParticipator from = this.argAsBestFPlayerMatch(1);
		if (from == null)
			return;
		EconomyParticipator to = this.argAsFaction(2);
		if (to == null)
			return;

		boolean success = Econ.transferMoney(fme, from, to, amount);

		if (success && Conf.logMoneyTransactions)
			SwornNations.get().log(ChatColor.stripColor(SwornNations.get().txt.parse("%s transferred %s from the player \"%s\" to the faction \"%s\"", fme.getName(),
					Econ.moneyString(amount), from.describeTo(null), to.describeTo(null))));
	}
}
