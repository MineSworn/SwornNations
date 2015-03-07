package net.dmulloy2.swornnations.adapters;

import java.io.IOException;

import net.dmulloy2.types.MyMaterial;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

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

		return MyMaterial.fromString(reader.nextString());
	}

	@Override
	public void write(JsonWriter writer, MyMaterial value) throws IOException
	{
		if (value == null)
		{
			writer.nullValue();
			return;
		}

		writer.value(value.serialize());
	}
}