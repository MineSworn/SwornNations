package me.t7seven7t.factions.util;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import me.t7seven7t.swornnations.npermissions.NPermission;
import me.t7seven7t.swornnations.npermissions.NPermissionManager;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonArray;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonPrimitive;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Role;

public class NPermissionManagerTypeAdapter implements JsonDeserializer<NPermissionManager>, JsonSerializer<NPermissionManager>
{

	private static String PLAYERS = "players";
	private static String ROLES = "roles";

	@Override
	public NPermissionManager deserialize(JsonElement json, Type t, JsonDeserializationContext context)
	{
		try
		{
			JsonObject obj = json.getAsJsonObject();

			JsonArray roles = obj.getAsJsonArray(ROLES);
			JsonArray players = obj.getAsJsonArray(PLAYERS);

			Map<FPlayer, HashMap<NPermission, Boolean>> playerPerms = new HashMap<FPlayer, HashMap<NPermission, Boolean>>();
			Map<Role, HashSet<NPermission>> rankPerms = new HashMap<Role, HashSet<NPermission>>();

			for (JsonElement e : roles)
			{
				String[] s = e.getAsString().split(",");
				Role r = Role.match(s[0]);
				HashSet<NPermission> perms = new HashSet<NPermission>();
				for (int i = 1; i < s.length; i++)
				{
					perms.add(NPermission.match(s[i]));
				}
				rankPerms.put(r, perms);
			}

			for (JsonElement e : players)
			{
				String[] s = e.getAsString().split(",");
				FPlayer p = FPlayers.i.get(s[0]);
				HashMap<NPermission, Boolean> perms = new HashMap<NPermission, Boolean>();
				for (int i = 1; i < s.length; i++)
				{
					String[] ss = s[i].split(":");

					perms.put(NPermission.match(ss[0]), (ss[1].equalsIgnoreCase("t")) ? true : false);
				}
				playerPerms.put(p, perms);
			}

			return new NPermissionManager(playerPerms, rankPerms);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			P.p.log(Level.WARNING, "Error encountered while deserializing a NPermissionManager.");
			return null;
		}
	}

	@Override
	public JsonElement serialize(NPermissionManager src, Type t, JsonSerializationContext context)
	{
		JsonObject obj = new JsonObject();

		try
		{
			JsonArray roles = new JsonArray();
			for (Entry<Role, HashSet<NPermission>> entry : src.getRankPerms().entrySet())
			{
				StringBuilder ret = new StringBuilder();
				ret.append(entry.getKey().name());
				for (NPermission perm : entry.getValue())
				{
					ret.append("," + perm.toString());
				}
				roles.add(new JsonPrimitive(ret.toString()));
			}
			obj.add(ROLES, roles);

			JsonArray players = new JsonArray();
			for (Entry<FPlayer, HashMap<NPermission, Boolean>> entry : src.getPlayerPerms().entrySet())
			{
				StringBuilder ret = new StringBuilder();
				ret.append(entry.getKey().getName());
				for (Entry<NPermission, Boolean> e : entry.getValue().entrySet())
				{
					ret.append("," + e.getKey().toString() + ":" + ((e.getValue()) ? "t" : "f"));
				}
				players.add(new JsonPrimitive(ret.toString()));
			}
			obj.add(PLAYERS, players);

			return obj;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			P.p.log(Level.WARNING, "Error encountered while serializing NPermissionManager.");
			return obj;
		}
	}

}
