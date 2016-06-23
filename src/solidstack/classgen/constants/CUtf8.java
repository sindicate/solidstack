/*--
 * Copyright 2016 René M. de Bloois
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

package solidstack.classgen.constants;

import java.nio.charset.Charset;

import solidstack.classgen.Bytes;

public class CUtf8 extends Constant
{
	static public Charset UTF8 = Charset.forName( "UTF-8" );

	private String value;

	public CUtf8( String value )
	{
		this.value = value;
	}

	@Override
	public void write( Bytes out )
	{
		out.writeByte( 1 );
		byte[] bytes = this.value.getBytes( UTF8 );
		out.writeShort( bytes.length );
		out.write( bytes );
	}

	@Override
	public boolean equals( Object obj )
	{
		if( obj != null )
			if( obj.getClass() == CUtf8.class )
			{
				CUtf8 other = (CUtf8)obj;
				return other.value.equals( this.value );
			}
		return false;
	}

	public String value()
	{
		return this.value;
	}
}
