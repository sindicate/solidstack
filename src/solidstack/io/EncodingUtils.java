/*--
 * Copyright 2015 René M. de Bloois
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

package solidstack.io;

import java.io.UnsupportedEncodingException;

import solidstack.lang.SystemException;

public class EncodingUtils
{
	/**
	 * Filters out ASCII bytes smaller than 128 and returns the result as a string.
	 *
	 * @param chars The bytes to filter.
	 * @return The ASCII bytes smaller than 128.
	 */
	static public String filter7bit( byte[] chars )
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
