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


/**
 * Encoding detector for UTF encoded files.
 *
 * @author René de Bloois
 */
public class UTFEncodingDetector implements EncodingDetector
{
	/**
	 * Constant for the UTF character set.
	 */
	static final public String CHARSET_UTF = "UTF";

	/**
	 * The singleton instance of this encoding detector.
	 */
	static final public EncodingDetector INSTANCE = new UTFEncodingDetector();


	private UTFEncodingDetector()
	{
		// This is a singleton
	}

	public String detect( byte[] bytes )
	{
		// TODO When UTF-8, test that the JVM skips the optional byte order mark. Also for UTF-16BE/LE.
		// FIXME Does this work when there is a BOM?

		// xx             UTF-8
		// xx xx          UTF-8
		// xx 00          UTF-16LE
		// xx 00 xx (00)  UTF-16LE
		// xx 00 00 (00)  UTF-32LE
		// 00 xx          UTF-16BE (default for UTF-16)
		// 00 00 (00 xx)  UTF-32BE

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
}
