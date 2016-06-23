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

package solidstack.cbor;

import java.util.Arrays;


public class ByteString
{
	private boolean utf8;
	private byte[] bytes;


	public ByteString( boolean utf8, byte[] bytes )
	{
		this.utf8 = utf8;
		this.bytes = bytes;
	}

	@Override
	public int hashCode()
	{
		return 31 * Arrays.hashCode( this.bytes ) + ( this.utf8 ? 1 : 0 );
	}

	@Override
	public boolean equals( Object other )
	{
		if( other == this )
			return true;
		if( !( other instanceof ByteString ) )
			return false;
		ByteString that = (ByteString)other;
		if( that.utf8 != this.utf8 )
			return false;
		return Arrays.equals( that.bytes, this.bytes );
	}

	public int length()
	{
		return this.bytes.length;
	}

	public byte[] unwrap()
	{
		return this.bytes;
	}

	public Object toJava()
	{
		if( this.utf8 )
			return new String( this.bytes, CBORWriter.UTF8 );
		return this.bytes;
	}

	@Override
	public String toString()
	{
		if( this.utf8 )
			return (String)toJava();
		return super.toString();
	}
}
