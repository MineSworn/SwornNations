package com.massivecraft.factions.cmd;

import me.t7seven7t.swornnations.npermissions.NPermission;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.tasks.SpiralTask;


public class CmdClaim extends FCommand
{
	
	public CmdClaim()
	{
		super();
		this.aliases.add("claim");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction", "your");
		this.optionalArgs.put("radius", "1");
		
		this.permission = Permission.CLAIM.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		senderMustHaveNPermission = NPermission.CLAIM;
		commandNotNeedFaction = true;
	}
	
	@Override
	public void perform()
	{
		if (!fme.hasFaction() && args.size() == 0) {
			msg("<b>You cannot claim land for no faction.");
			return;
		}
		// Read and validate input
		final Faction forFaction = this.argAsFaction(0, myFaction);
		int radius = this.argAsInt(1, 1);

		if (radius < 1)
		{
			msg("<b>If you specify a radius, it must be at least 1.");
			return;
		}

		if (radius < 2)
		{
			// single chunk
			fme.attemptClaim(forFaction, me.getLocation(), true);
		}
		else
		{
			// radius claim
			new SpiralTask(new FLocation(me), radius)
			{
				private int failCount = 0;
				private final int limit = Conf.radiusClaimFailureLimit - 1;

				@Override
				public boolean work()
				{
					boolean success = fme.attemptClaim(forFaction, this.currentLocation(), true);
					if (success)
						failCount = 0;
					else if ( ! success && failCount++ >= limit)
					{
						this.stop();
						return false;
					}

					return true;
				}
			};
		}
	}
	
}
