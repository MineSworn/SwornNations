package me.t7seven7t.factions.util;

import java.io.IOException;

import org.bukkit.craftbukkit.libs.com.google.gson.TypeAdapter;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonReader;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonToken;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;

public class MyMaterialTypeAdapter extends TypeAdapter<MyMaterial> {

	@Override
	public MyMaterial read(JsonReader reader) throws IOException {
		if (reader.peek() == JsonToken.NULL) {
			reader.nextNull();
			return null;
		}
		String[] item = reader.nextString().split(":");
		int id = Integer.parseInt(item[0]);
		int data = Integer.parseInt(item[1]);
		return new MyMaterial(id, data);
	}

	@Override
	public void write(JsonWriter writer, MyMaterial value) throws IOException {
		if (value == null) {
			writer.nullValue();
			return;
		}
		String item = value.getTypeId() + ":" + value.getData();
		writer.value(item);
	}

}
