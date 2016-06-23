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

package solidstack.classgen;

public class Bytes
{
	private byte[] bytes = new byte[ 4096 ];
	private int pos;


	public byte[] toByteArray()
	{
		byte[] result = new byte[ this.pos ];
		System.arraycopy( this.bytes, 0, result, 0, this.pos );
		return result;
	}

	public int size()
	{
		return this.pos;
	}

    public void writeByte( int b )
	{
		if( b < -128 || b > 255 )
			throw new IllegalArgumentException( Integer.toString( b ) );
    	if( this.pos >= this.bytes.length )
    	{
    		byte[] old = this.bytes;
    		this.bytes = new byte[ old.length * 2 ];
    		System.arraycopy( old, 0, this.bytes, 0, old.length );
    	}
    	this.bytes[ this.pos++ ] = (byte)b;
 	}

	public void writeShort( int s )
	{
		if( s < -0x8000 || s >= 0x8000 )
			throw new IllegalArgumentException( Integer.toString( s ) );
		writeByte( s >>> 8 & 0xFF ); // >>> has precedence
		writeByte( s & 0xFF );
	}

	public void writeShortAt( int pos, int s )
	{
		if( s < -0x8000 || s >= 0x8000 )
			throw new IllegalArgumentException( Integer.toString( s ) );
    	this.bytes[ pos++ ] = (byte)( s >>> 8 & 0xFF ); // >>> has precedence
    	this.bytes[ pos++ ] = (byte)( s & 0xFF );
	}

    public void writeInt( int i )
	{
		writeByte( i >>> 24 & 0xFF ); // >>> has precedence
		writeByte( i >>> 16 & 0xFF );
		writeByte( i >>> 8 & 0xFF );
		writeByte( i & 0xFF );
 	}

	public void write( byte[] code )
	{
		for( byte b : code )
			writeByte( b );
	}

	public void write( Bytes b )
	{
		byte[] s = b.bytes;
		int len = b.pos;
		for( int i = 0; i < len; i++ )
			writeByte( s[ i ] );
	}
}
