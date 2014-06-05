package net.dmulloy2.swornnations.types;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.types.Role;

public class NPermissionManager
{
	private Map<String, HashMap<NPermission, Boolean>> playerPerms = new HashMap<String, HashMap<NPermission, Boolean>>();
	private Map<Role, HashSet<NPermission>> rankPerms = new HashMap<Role, HashSet<NPermission>>();

	public NPermissionManager()
	{
		setDefaultPerms();
	}

	public NPermissionManager(Map<String, HashMap<NPermission, Boolean>> p, Map<Role, HashSet<NPermission>> r)
	{
		playerPerms = p;
		rankPerms = r;
	}

	public Map<String, HashMap<NPermission, Boolean>> getPlayerPerms()
	{
		return Collections.unmodifiableMap(playerPerms);
	}

	public Map<Role, HashSet<NPermission>> getRankPerms()
	{
		return Collections.unmodifiableMap(rankPerms);
	}

	public void setDefaultPerms()
	{
		rankPerms.put(Role.INITIATE, Conf.initiateNationPermissions);
		rankPerms.put(Role.NORMAL, Conf.defaultNationPermissions);
		rankPerms.put(Role.OFFICER, Conf.officerNationPermissions);
		rankPerms.put(Role.MODERATOR, Conf.moderatorNationPermissions);
		rankPerms.put(Role.COADMIN, Conf.coadminNationPermissions);
	}

	public void addPerm(FPlayer player, NPermission perm)
	{
		if (player.getRole() == Role.ADMIN)
			return;

		if (playerPerms.containsKey(player))
		{
			if (! playerPerms.get(player).containsKey(perm))
				playerPerms.get(player).put(perm, true);
			else
				replaceEntry(player, perm, true);
		}
		else
		{
			String id = player.getId();
			playerPerms.put(id, new HashMap<NPermission, Boolean>());
			playerPerms.get(id).put(perm, true);
		}
	}

	public void addPerm(Role rank, NPermission perm)
	{
		if (rank == Role.ADMIN)
			return;

		if (rankPerms.containsKey(rank))
		{
			if (! rankPerms.get(rank).contains(perm))
				rankPerms.get(rank).add(perm);
		}
		else
		{
			rankPerms.put(rank, new HashSet<NPermission>());
			rankPerms.get(rank).add(perm);
		}
	}

	public void removePerm(FPlayer player, NPermission perm)
	{
		if (player.getRole() == Role.ADMIN)
			return;

		replaceEntry(player, perm, false);
	}

	public void replaceEntry(FPlayer player, NPermission perm, boolean deny)
	{
		String id = player.getId();
		if (playerPerms.containsKey(id))
			if (playerPerms.get(id).containsKey(perm))
				playerPerms.get(id).remove(perm);

		if (! playerPerms.containsKey(id))
			playerPerms.put(id, new HashMap<NPermission, Boolean>());

		playerPerms.get(id).put(perm, deny);
	}

	public void removePerm(Role rank, NPermission perm)
	{
		if (rank == Role.ADMIN)
			return;

		if (hasPerm(rank, perm))
			rankPerms.get(rank).remove(perm);
	}

	public boolean hasPerm(FPlayer player, NPermission perm)
	{
		if (player.getRole() == Role.ADMIN || player.isAdminBypassing())
			return true;

		String id = player.getId();
		if (playerPerms.containsKey(id))
			if (playerPerms.get(id).containsKey(perm))
				if (playerPerms.get(id).get(perm))
					return true;

		return false;
	}

	public boolean isDeniedPerm(FPlayer player, NPermission perm)
	{
		if (player.getRole() == Role.ADMIN || player.isAdminBypassing())
			return false;

		String id = player.getId();
		if (playerPerms.containsKey(id))
			if (playerPerms.get(id).containsKey(perm))
				if (! playerPerms.get(id).get(perm))
					return true;

		return false;
	}

	public boolean hasPerm(Role rank, NPermission perm)
	{
		if (rank == Role.ADMIN)
			return true;

		if (rankPerms.containsKey(rank))
			if (rankPerms.get(rank).contains(perm))
				return true;
		return false;
	}
}