package net.dmulloy2.swornnations.adapters;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.dmulloy2.io.UUIDFetcher;
import net.dmulloy2.swornnations.SwornNations;
import net.dmulloy2.swornnations.types.NPermission;
import net.dmulloy2.swornnations.types.NPermissionManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
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

			Map<String, HashMap<NPermission, Boolean>> playerPerms = new HashMap<String, HashMap<NPermission, Boolean>>();
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
				String id = s[0];
				HashMap<NPermission, Boolean> perms = new HashMap<NPermission, Boolean>();
				for (int i = 1; i < s.length; i++)
				{
					String[] ss = s[i].split(":");

					perms.put(NPermission.match(ss[0]), (ss[1].equalsIgnoreCase("t")) ? true : false);
				}

				// Make sure id is a UUID
				if (id.length() != 36)
					id = UUIDFetcher.getUUID(id).toString();

				playerPerms.put(id, perms);
			}

			return new NPermissionManager(playerPerms, rankPerms);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			SwornNations.get().log(Level.WARNING, "Error encountered while deserializing a NPermissionManager.");
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
			for (Entry<String, HashMap<NPermission, Boolean>> entry : src.getPlayerPerms().entrySet())
			{
				StringBuilder ret = new StringBuilder();
				ret.append(entry.getKey());
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
			SwornNations.get().log(Level.WARNING, "Error encountered while serializing NPermissionManager.");
			return obj;
		}
	}

}
