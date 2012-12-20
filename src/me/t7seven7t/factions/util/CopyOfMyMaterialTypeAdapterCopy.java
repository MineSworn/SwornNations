package me.t7seven7t.factions.util;

import java.lang.reflect.Type;
import java.util.logging.Level;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;

import com.massivecraft.factions.P;

public class CopyOfMyMaterialTypeAdapterCopy implements JsonDeserializer<MyMaterial>, JsonSerializer<MyMaterial> {

	private static final String TYPEID = "i";
	
	@Override
	public JsonElement serialize(MyMaterial src, Type typeOfSrc, JsonSerializationContext arg2) {
		JsonObject obj = new JsonObject();
		
		try
		{
			obj.addProperty(TYPEID, src.getTypeId() + ":" + src.getData());

			return obj;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			P.p.log(Level.WARNING, "Error encountered while serializing a Material.");
			return obj;
		}
	}

	@Override
	public MyMaterial deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		try
		{
			JsonObject obj = json.getAsJsonObject();

			String s = obj.get(TYPEID).getAsString();
			
			int typeId = Integer.parseInt(s.split(":")[0]);
			byte data = Byte.parseByte(s.split(":")[1]);
		
			return new MyMaterial(typeId, data);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			P.p.log(Level.WARNING, "Error encountered while deserializing a LazyLocation.");
			return null;
		}
	}

}
