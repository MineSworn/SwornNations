package net.dmulloy2.swornnations.types;

/**
 * @author dmulloy2
 */

public enum NPermission
{
	ALLY,
	BREAK,
	BUILD,
	CHEST,
	CLAIM,
	DESCRIPTION,
	ENEMY,
	INITIATE,
	INVITE,
	KICK,
	MODERATOR,
	MOTD,
	NATION,
	NEUTRAL,
	OFFICER,
	OUTPOST,
	OWNER,
	PERM,
	SETHOME,
	SETOUTPOST,
	SWITCH,
	TAG,
	TITLE,
	UNCLAIM,
	WARP,
	;

	public static NPermission match(String partial)
	{
		partial = partial.toLowerCase();
		for (NPermission perm : NPermission.values())
		{
			if (perm.name().toLowerCase().startsWith(partial))
				return perm;
		}

		return null;
	}
}