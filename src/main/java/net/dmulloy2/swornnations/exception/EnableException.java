/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.swornnations.exception;

/**
 * @author dmulloy2
 */

public class EnableException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public EnableException(String message)
	{
		super(message);
	}

	public EnableException(String message, Throwable cause)
	{
		super(message, cause);
	}
}