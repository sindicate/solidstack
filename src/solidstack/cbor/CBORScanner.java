/*--
 * Copyright 2016 Ren� M. de Bloois
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

import java.io.IOException;

import solidstack.cbor.Token.TYPE;
import solidstack.io.FatalIOException;
import solidstack.io.Resource;
import solidstack.io.SourceException;
import solidstack.io.SourceInputStream;
import solidstack.io.SourceLocation;


public class CBORScanner
{
	private SourceInputStream in;


	public CBORScanner( SourceInputStream in )
	{
		this.in = in;
	}

	public void close()
	{
		this.in.close();
	}

	public Resource getResource()
	{
		return this.in.getResource();
	}

	public SourceLocation getLocation()
	{
		return this.in.getLocation();
	}

	public SimpleToken get()
	{
		SourceInputStream in = this.in;
		try
		{
			SourceLocation loc = in.getLocation();

			int b = in.read();
			if( b == -1 )
				return Token.EOF;

			int major = b >>> 5;
			int minor = b & 0x1F;

			if( minor == 31 )
				switch( major )
				{
					case 2: return Token.IBSTRING;
					case 3: return Token.ITSTRING;
					case 4: return Token.IARRAY;
					case 5: return Token.IMAP;
					case 7: return Token.BREAK;
					default:
						throw new SourceException( "Unsupported additional info 31 for major type: " + major, loc );
				}

			switch( major )
			{
				case 0: return readUInt( in, loc, minor, TYPE.UINT );
				case 1: return readUInt( in, loc, minor, TYPE.NINT );
				case 2: return readUInt( in, loc, minor, TYPE.BYTES );
				case 3: return readUInt( in, loc, minor, TYPE.TEXT );
				case 4: return readUInt( in, loc, minor, TYPE.ARRAY );
				case 5: return readUInt( in, loc, minor, TYPE.MAP );
				case 6: return readUInt( in, loc, minor, TYPE.TAG );
				case 7: return readSimple( in, loc, minor, TYPE.TAG );
				default:
					throw new SourceException( "Unsupported major type: " + major, loc );
			}
		}
		catch( IOException e )
		{
			throw new FatalIOException( e );
		}
	}

	public void readBytes( byte[] bytes )
	{
		SourceInputStream in = this.in;
		int remaining = bytes.length;
		try
		{
			int read = in.read( bytes );
			while( read < remaining )
			{
				if( read < 0 )
					throw new SourceException( "Unexpected EOF", in.getLocation() );
				if( read == 0 )
					throw new SourceException( "Zero bytes read", in.getLocation() );
				remaining -= read;
				read = in.read( bytes, bytes.length - remaining, remaining );
			}
		}
		catch( IOException e )
		{
			throw new FatalIOException( e );
		}
	}

	public String readString( int len )
	{
		byte[] result = new byte[ len ];
		readBytes( result );
		return new String( result, CBORWriter.UTF8 );
	}

	static private SimpleToken readSimple( SourceInputStream in, SourceLocation loc, int minor, TYPE type ) throws IOException
	{
		switch( minor )
		{
			case 20: return Token.FALSE;
			case 21: return Token.TRUE;
			case 22: return Token.NULL;
			case 23: return Token.UNDEF;
			case 25: throw new UnsupportedOperationException( "Half precision float not supported" );
			case 26: return SimpleToken.forFloatS( Float.intBitsToFloat( readUInt4( in ) ) );
			case 27: return SimpleToken.forFloatD( Double.longBitsToDouble( readUInt8( in ) ) );
			case 31: return Token.BREAK;
			default:
				throw new SourceException( "Unsupported additional info: " + minor, loc );
		}
	}

	static private SimpleToken readUInt( SourceInputStream in, SourceLocation loc, int minor, TYPE type ) throws IOException
	{
		return SimpleToken.forType( type, readUInt( in, loc, minor ) );
	}

	static private long readUInt( SourceInputStream in, SourceLocation loc, int minor ) throws IOException
	{
		switch( minor )
		{
			case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9:
			case 10: case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19:
			case 20: case 21: case 22: case 23:
				return minor;
			case 24: return readUInt1( in );
			case 25: return readUInt2( in );
			case 26: return readUInt4( in );
			case 27: return readUInt8( in );
			default:
				throw new SourceException( "Unsupported additional info: " + minor, loc );
		}
	}

	static private int readUInt1( SourceInputStream in ) throws IOException
	{
		int result = in.read();
		if( result == -1 )
			throw new SourceException( "Unexpected EOF", in.getLocation() );
		return result;
	}

	static private int readUInt2( SourceInputStream in ) throws IOException
	{
		return readUInt1( in ) << 8 | readUInt1( in );
	}

	static private int readUInt4( SourceInputStream in ) throws IOException
	{
		return readUInt2( in ) << 16 | readUInt2( in );
	}

	static private long readUInt8( SourceInputStream in ) throws IOException
	{
		return (long)readUInt4( in ) << 32 | readUInt4( in );
	}
}
