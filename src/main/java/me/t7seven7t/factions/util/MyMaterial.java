package me.t7seven7t.factions.util;

import org.bukkit.Material;

public class MyMaterial
{
	private Material mat;
	private short data;

	public MyMaterial(Material mat, short data)
	{
		this.mat = mat;
		this.data = data;
	}
	
	public MyMaterial(Material mat)
	{
		this(mat, (short) 0);
	}

	public final Material getType()
	{
		return mat;
	}

	public final short getData()
	{
		return data;
	}

	@Override
	public String toString()
	{
		return mat.toString() + ":" + data;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyMaterial other = (MyMaterial) obj;
		if (data != other.data)
			return false;
		if (mat != other.mat)
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 1;

		hash *= mat.hashCode();
		hash *= data;

		return hash;
	}
}