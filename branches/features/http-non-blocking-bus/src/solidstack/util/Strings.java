package solidstack.util;


/**
 * Support for strings.
 *
 * @author Ren� de Bloois
 */
public class Strings extends Objects
{
	/**
	 * @param s1 The first string.
	 * @param s2 The second string.
	 * @return True if both strings are null or equal ignoring the case, false otherwise.
	 */
	static public boolean equalsIgnoreCase( String s1, String s2 )
	{
		if( s1 == s2 )
			return true;
		if( s1 == null )
			return false;
		return s1.equalsIgnoreCase( s2 );
	}
}
