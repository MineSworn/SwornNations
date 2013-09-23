package me.t7seven7t.factions.util;

import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.TypeAdapter;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonReader;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonToken;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;

import com.massivecraft.factions.util.MaterialUtil;

public class MyMaterialTypeAdapter extends TypeAdapter<MyMaterial>
{
	@Override
	public MyMaterial read(JsonReader reader) throws IOException
	{
		if (reader.peek() == JsonToken.NULL)
		{
			reader.nextNull();
			return null;
		}
		
		String[] item = reader.nextString().split(":");
		
		Material mat = MaterialUtil.getMaterial(item[0]);
		short data = Short.parseShort(item[1]);

		return new MyMaterial(mat, data);
	}

	@Override
	public void write(JsonWriter writer, MyMaterial value) throws IOException
	{
		if (value == null)
		{
			writer.nullValue();
			return;
		}

		String item = value.getMaterial();
		
		writer.value(item);
	}
}