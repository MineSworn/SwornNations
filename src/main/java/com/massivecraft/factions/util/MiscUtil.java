package com.massivecraft.factions.util;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.ChatColor;

public class MiscUtil
{
	// Inclusive range
	public static long[] range(long start, long end)
	{
		long[] values = new long[(int) Math.abs(end - start) + 1];

		if (end < start)
		{
			long oldstart = start;
			start = end;
			end = oldstart;
		}

		for (long i = start; i <= end; i++)
		{
			values[(int) (i - start)] = i;
		}

		return values;
	}

	// TODO: create tag whitelist!!
	public static HashSet<String> substanceChars = new HashSet<String>(Arrays.asList(new String[] { "0", "1", "2", "3", "4", "5", "6", "7",
			"8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
			"Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
			"y", "z" }));

	public static String getComparisonString(String str)
	{
		String ret = "";

		str = ChatColor.stripColor(str);
		str = str.toLowerCase();

		for (char c : str.toCharArray())
		{
			if (substanceChars.contains(String.valueOf(c)))
			{
				ret += c;
			}
		}

		return ret.toLowerCase();
	}

	/**
	 * Basically just a wrapper for {@link Integer#parseInt(String)}
	 * <p>
	 * Catches the {@link NumberFormatException} and returns -1
	 * 
	 * @param s
	 *        - String to attempt to parse into an Integer
	 */
	public static int parseInt(String s)
	{
		int ret = -1;

		try
		{
			ret = Integer.parseInt(s);
		}
		catch (Exception e)
		{
			// Return -1, move on
		}

		return ret;
	}

	/**
	 * Returns whether or not a String can be parsed as an Integer
	 * 
	 * @param string
	 *        - String to check
	 * @return Whether or not a String can be parsed as an Integer
	 */
	public static boolean isInteger(String s)
	{
		return parseInt(s) != -1;
	}
}