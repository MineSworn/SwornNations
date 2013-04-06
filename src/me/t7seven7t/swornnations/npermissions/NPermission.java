package me.t7seven7t.swornnations.npermissions;

public enum NPermission {
	ALLY,
	BUILD,
	BREAK,
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
	OWNER,
	PERM,
	SETHOME,
	SWITCH,
	TAG,
	TITLE,
	UNCLAIM,
	WARP;
	
	public static NPermission match(String s) {
		for (NPermission perm : NPermission.values()) {
			if (s.equalsIgnoreCase(perm.name()))
				return perm;
		}
		return null;
	}
	
}
