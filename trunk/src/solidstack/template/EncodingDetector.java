/*--
 * Copyright 2012 Ren� M. de Bloois
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package solidstack.template;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import solidstack.lang.SystemException;


/**
 * An encoding detector for templates.
 *
 * @author Ren� de Bloois
 */
// FIXME This does not work yet with the version="1.0" in the directive.
public class EncodingDetector implements solidstack.io.EncodingDetector
{
	static final private Pattern ENCODING_PATTERN = Pattern.compile( "^<%@[ \t]*template[ \t]+encoding[ \t]*=\"([^\"]*)\".*", Pattern.CASE_INSENSITIVE ); // TODO Improve, case sensitive?

	/**
	 * Constant for the UTF character set.
	 */
	static final public String CHARSET_UTF = "UTF";

	/**
	 * The singleton instance of this encoding detector.
	 */
	static final public EncodingDetector INSTANCE = new EncodingDetector();


	private EncodingDetector()
	{
		// This is a singleton
	}

	public String detect( byte[] bytes )
	{
		String result = CHARSET_UTF; // Default

		String first = toAscii( bytes );
		Matcher matcher = ENCODING_PATTERN.matcher( first );
		if( matcher.matches() )
			result = matcher.group( 1 );

		// TODO When UTF-8, test the the JVM skips the optional byte order mark. Also for UTF-16BE/LE.

		if( !CHARSET_UTF.equals( result ) )
			return result;

		return detectUTF( bytes );
	}

	// Only works when first 2 characters are ascii
	static private String detectUTF( byte[] bytes  )
	{
		// xx             UTF-8
		// xx xx          UTF-8
		// xx 00          UTF-16LE
		// xx 00 xx (00)  UTF-16LE
		// xx 00 00 (00)  UTF-32LE
		// 00 xx          UTF-16BE (default for UTF-16)
		// 00 00 (00 xx)  UTF-32BE

		// BOM is only read by the JVM when UTF-8, UTF-16 or UTF-32. TODO Test
		// specifying BE or LE means the BOM is not removed when there is one. TODO Test
		// The BOM is a Zero-width non-breaking space (ZWNBSP), but is deprecated as a real character, so that it can be used as a BOM.
		// TODO Maybe we should use UTF-16 and UTF-32 to make sure that any BOM is removed from the file.

		if( bytes.length <= 1 )
			return CHARSET_UTF_8;
		if( bytes[ 0 ] != 0 )
		{
			if( bytes[ 1 ] != 0 )
				return CHARSET_UTF_8;
			if( bytes.length == 2 )
				return CHARSET_UTF_16LE;
			if( bytes[ 2 ] != 0 )
				return CHARSET_UTF_16LE; // TODO Throw undetectable when length < 4
			return CHARSET_UTF_32LE; // TODO Throw undetectable when length < 4
		}
		if( bytes[ 1 ] != 0 )
			return CHARSET_UTF_16BE;
		return CHARSET_UTF_32BE; // TODO Throw undetectable when length < 4
	}

	static private String toAscii( byte[] chars )
	{
		int len = chars.length;
		int j = 0;
		byte[] result = new byte[ len ];
		for( int i = 0; i < len; i++ )
		{
			byte ch = chars[ i ];
			if( ch > 0 && ch < 128 )
				result[ j++ ] = ch;
		}
		try
		{
			return new String( result, 0, j, "ISO-8859-1" );
		}
		catch( UnsupportedEncodingException e )
		{
			throw new SystemException( e );
		}
	}
}
