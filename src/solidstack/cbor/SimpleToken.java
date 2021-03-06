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


public class SimpleToken implements Token
{
	final TYPE type;
	long value;
	boolean bool;
	float flot;
	double doble;


	static public SimpleToken forType( TYPE type, long value )
	{
		SimpleToken result = new SimpleToken( type );
		result.value = value;
		return result;
	}

	static public SimpleToken forFloatS( float flot )
	{
		SimpleToken result = new SimpleToken( TYPE.SFLOAT );
		result.flot = flot;
		return result;
	}

	static public SimpleToken forFloatD( double doble )
	{
		SimpleToken result = new SimpleToken( TYPE.DFLOAT );
		result.doble = doble;
		return result;
	}

	SimpleToken( TYPE type )
	{
		this.type = type;
	}

	SimpleToken( boolean value )
	{
		this.type = TYPE.BOOL;
		this.bool = value;
	}

	Token withTags( Tag[] tags )
	{
		TaggedToken result = new TaggedToken( this.type );
		result.bool = this.bool;
		result.doble = this.doble;
		result.flot = this.flot;
		result.value = this.value;
		result.tags = tags;
		return result;
	}

	@Override
	public boolean isTag()
	{
		return this.type == TYPE.TAG;
	}

	@Override
	public TYPE type()
	{
		return this.type;
	}

	@Override
	public long longValue()
	{
		if( this.type == TYPE.NINT )
			return -( this.value + 1 ); // TODO overflow exception?
		return this.value;
	}

	@Override
	public double doubleValue()
	{
		return this.doble;
	}

	@Override
	public boolean booleanValue()
	{
		return this.bool;
	}

	@Override
	public int length()
	{
		// TODO Check for UINT
		if( this.value < 0 || this.value > Integer.MAX_VALUE )
			throw new CBORException( "Invalid length: " + this.value );
		return (int)this.value;
	}

	@Override
	public boolean hasTag( long value )
	{
		return false;
	}

	@Override
	public Tag getTag( long value )
	{
		return null;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder( this.type.toString() );
		switch( this.type )
		{
			case UINT:
			case BYTES:
			case TEXT:
			case ARRAY:
			case MAP:
				result.append( ' ' ); appendUnsigned( result, this.value );
				break;
			case TAG:
				result.append( " 0x" ); appendHex( result, this.value );
				break;
			case NINT:
				result.append( ' ' ); appendNegative( result, this.value );
				break;
			case BOOL:
				result.append( ' ' ).append( this.bool );
				break;
			case IBYTES:
			case ITEXT:
			case IARRAY:
			case IMAP:
			case NULL:
			case UNDEF:
			case BREAK:
			case EOF:
				break;
			case HFLOAT:
			case SFLOAT:
				result.append( ' ' ).append( Float.toString( this.flot ) );
				break;
			case DFLOAT:
				result.append( ' ' ).append( Double.toString( this.doble ) );
				break;
			default:
				throw new CBORException( "Unexpected type: " + this.type );
		}
		return result.toString();
	}

	private void appendUnsigned( StringBuilder out, long value )
	{
		// TODO In java 8 we have a Long.toUnsignedString()
		if( value < 0 )
		{
            long quotient = ( value >>> 1 ) / 5; // Unsigned long divide by 10 by first doing an unsigned shift right
            long remainder = value - quotient * 10; // This is actually a completely unsigned operation because of overflow
            out.append( quotient ).append( remainder );
		}
		else
			out.append( value );
	}

	// 0 --> -1
	// 1 --> -2
	// 0xFFFF --> -0x10000
	// 0xFFFFFFFFFFFFFFFF (-1) --> -0x10000000000000000
	private void appendNegative( StringBuilder out, long value )
	{
		if( value == -1 )
			out.append( "-18446744073709551616" );
		else
		{
			out.append( '-' );
			appendUnsigned( out, value + 1 );
		}
	}

	static void appendHex( StringBuilder out, long value )
	{
		String hex = Long.toHexString( value ).toUpperCase();
		if( hex.length() % 2 == 1 )
			out.append( '0' );
		out.append( hex );
	}
}
