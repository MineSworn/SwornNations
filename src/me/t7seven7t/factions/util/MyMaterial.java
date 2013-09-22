package me.t7seven7t.factions.util;

import org.bukkit.Material;

// TODO: Fix deprecation
public class MyMaterial
{
	@Deprecated
	private int typeId;
	
	private Material mat;
	private byte data;

	public MyMaterial(int typeId, byte data)
	{
		this.typeId = typeId;
		this.data = data;
	}

	public MyMaterial(int typeId, int data)
	{
		this.typeId = typeId;
		this.data = (byte) data;
	}

	public MyMaterial(Material mat)
	{
		this.mat = mat;
		this.data = 0;
	}
	
	public MyMaterial(Material mat, byte data)
	{
		this.mat = mat;
		this.data = data;
	}

	public MyMaterial(int typeId)
	{
		this.typeId = typeId;
		this.data = 0;
	}

	public String getMaterial()
	{
		return typeId + ":" + data;
	}
	
	@SuppressWarnings("deprecation")
	public Material getType()
	{
		return mat != null ? mat : Material.getMaterial(typeId);
	}

	@Deprecated
	public int getTypeId()
	{
		return typeId;
	}

	public byte getData()
	{
		return data;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + data;
		result = prime * result + typeId;
		return result;
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
		if (typeId != other.typeId)
			return false;
		return true;
	}

}
