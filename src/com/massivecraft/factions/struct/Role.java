package com.massivecraft.factions.struct;

import com.massivecraft.factions.Conf;

public enum Role
{
	ADMIN(4, "admin"),
	COADMIN(3, "co-admin"),
	MODERATOR(2, "moderator"),
	OFFICER(1, "officer"),
	NORMAL(0, "normal member"),
	INITIATE(-1, "initiate");
	
	public final int value;
	public final String nicename;
	
	private Role(final int value, final String nicename)
	{
		this.value = value;
		this.nicename = nicename;
	}
	
	public boolean isAtLeast(Role role)
	{
		return this.value >= role.value;
	}
	
	public boolean isAtMost(Role role)
	{
		return this.value <= role.value;
	}

	@Override
	public String toString()
	{
		return this.nicename;
	}
	
	public String getPrefix()
	{
		if (this == Role.ADMIN)
		{
			return Conf.prefixAdmin;
		} 
		
		if (this == Role.COADMIN) 
		{
			return Conf.prefixCoadmin;
		}
		
		if (this == Role.MODERATOR)
		{
			return Conf.prefixMod;
		}
		
		if (this == Role.OFFICER) 
		{
			return Conf.prefixOfficer;
		}
		
		if (this == Role.INITIATE)
		{
			return Conf.prefixInitiate;
		}
		
		return "";
	}
	
	public static Role get(int id) 
	{
		for (Role r : Role.values())
		{
			if (r.value == id)
				return r;
		}
		return null;
	}
	
	public static Role match(String s)
	{
		for (Role r : Role.values())
		{
			if (s.equalsIgnoreCase(r.name()))
				return r;
		}
		return null;
	}
}