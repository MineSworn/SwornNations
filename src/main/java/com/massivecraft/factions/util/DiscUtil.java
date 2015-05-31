package com.massivecraft.factions.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;

import net.dmulloy2.swornnations.SwornNations;

public class DiscUtil
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //

	private final static String UTF8 = "UTF-8";

	// -------------------------------------------- //
	// BYTE
	// -------------------------------------------- //

	public static byte[] readBytes(File file) throws IOException
	{
		return Files.readAllBytes(file.toPath());
	}

	public static void writeBytes(File file, byte[] bytes) throws IOException
	{
		Files.write(file.toPath(), bytes);
	}

	// -------------------------------------------- //
	// STRING
	// -------------------------------------------- //

	public static void write(File file, String content) throws IOException
	{
		writeBytes(file, utf8(content));
	}

	public static String read(File file) throws IOException
	{
		return utf8(readBytes(file));
	}

	// -------------------------------------------- //
	// CATCH
	// -------------------------------------------- //

	public static boolean writeCatch(File file, String content)
	{
		try
		{
			write(file, content);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public static String readCatch(File file)
	{
		try
		{
			return read(file);
		}
		catch (IOException e)
		{
			return null;
		}
	}

	// -------------------------------------------- //
	// DOWNLOAD
	// -------------------------------------------- //

	@SuppressWarnings("resource")
	public static boolean downloadUrl(String urlstring, File file)
	{
		try
		{
			URL url = new URL(urlstring);
			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			FileOutputStream fos = new FileOutputStream(file);
			fos.getChannel().transferFrom(rbc, 0, 1 << 24);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public static boolean downloadUrl(String urlstring, String filename)
	{
		return downloadUrl(urlstring, new File(filename));
	}

	// -------------------------------------------- //
	// FILE DELETION
	// -------------------------------------------- //

	public static boolean deleteRecursive(File path) throws FileNotFoundException
	{
		if (! path.exists())
			throw new FileNotFoundException(path.getAbsolutePath());
		boolean ret = true;
		if (path.isDirectory())
		{
			for (File f : path.listFiles())
			{
				ret = ret && deleteRecursive(f);
			}
		}
		return ret && path.delete();
	}

	// -------------------------------------------- //
	// UTF8 ENCODE AND DECODE
	// -------------------------------------------- //

	public static byte[] utf8(String string)
	{
		try
		{
			return string.getBytes(UTF8);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static String utf8(byte[] bytes)
	{
		try
		{
			return new String(bytes, UTF8);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	// -------------------------------------------- //
	// DISK SPACE
	// -------------------------------------------- //

	private static long lastWarn;

	public static boolean checkDiskSpace()
	{
		// Free space in folder
		long freeSpace = SwornNations.get().getDataFolder().getFreeSpace();

		if (freeSpace == 0)
		{
			long now = System.currentTimeMillis();
			if (now - lastWarn > 18000L)
			{
				lastWarn = now;

				// Warn players
				SwornNations.get().getServer()
						.broadcastMessage(SwornNations.get().txt.parse("<b>[SEVERE] Factions has detected that disk space is low."));
				SwornNations.get().getServer()
						.broadcastMessage(SwornNations.get().txt.parse("<b>[SEVERE] Please make some space on your Disk!"));
				SwornNations.get().getServer()
						.broadcastMessage(SwornNations.get().txt.parse("<b>[SEVERE] This message will be displayed every 5 minutes."));

				// Lock the factions plugin
				SwornNations.get().setLocked(true);
				SwornNations.get().setLockReason("low disk space");
				return true;
			}
		}

		return false;
	}
}