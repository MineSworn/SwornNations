package net.dmulloy2.swornnations.types;

public enum NPermission
{
	ALLY,
	BREAK,
	BUILD,
	CHEST,
	CLAIM,
	DESCRIPTION,
	ENEMY,
	INVITE,
	KICK,
	MODERATOR,
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
	WARP;

	public static NPermission match(String s)
	{
		for (NPermission perm : NPermission.values())
		{
			if (s.equalsIgnoreCase(perm.name()))
				return perm;
		}

		return null;
	}
}