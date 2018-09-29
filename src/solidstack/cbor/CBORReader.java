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

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;

import solidstack.cbor.Token.TYPE;
import solidstack.io.DiskBuffer;
import solidstack.io.SourceInputStream;
import solidstack.io.SourceLocation;
import solidstack.json.JSONArray;
import solidstack.json.JSONObject;


public class CBORReader
{
	static private enum STATE { IBYTES, ITEXT, ARRAYMAP }

	private CBORParser in;

	private Token buffer;

	// TODO Lazy init
	private DiskBuffer disk = new DiskBuffer( "cborbuffer" );


	public CBORReader( SourceInputStream in )
	{
		this.in = new CBORParser( in );
	}

	public void close()
	{
		this.in.close();
		this.disk.close();
	}

	public SourceLocation getLocation()
	{
		return this.in.getLocation();
	}

	public Token get()
	{
		if( this.buffer != null )
		{
			Token result = this.buffer;
			this.buffer = null;
			return result;
		}
		return this.in.get();
	}

	public void push( Token token )
	{
		// TODO Check null
		this.buffer = token;
	}

	public Token peek()
	{
		if( this.buffer != null )
			return this.buffer;
		return this.buffer = this.in.get();
	}

	public Object read()
	{
		CBORParser in = this.in;
		SourceLocation loc = in.getLocation();
		Token t = get();
		TYPE type = t.type();
		switch( type )
		{
			case MAP:
				int len = t.length();
				JSONObject object = new JSONObject();
				for( int i = 0; i < len; i++ )
					object.set( (String)read(), read() ); // TODO Throw better exception for the cast?
				return object;

			case ARRAY:
				len = t.length();
				JSONArray array = new JSONArray();
				for( int i = 0; i < len; i++ )
					array.add( read() );
				return array;

			case IARRAY:
				array = new JSONArray();
				for( Object o = read(); o != null; o = read() )
					array.add( o );
				return array;

			case BYTES:
				byte[] bytes = new byte[ t.length() ];
				this.in.readBytes( bytes );
				return bytes;

			case TEXT:
				len = t.length();
				return this.in.readString( len );

			case ITEXT:
				// TODO If only 1 TSTRING then return normal String
				return new InputStreamReader( this.disk.buffer( this.in.getInputStream() ), Charset.forName( "UTF-8" ) );

			case IBYTES:
				// TODO If only 1 BSTRING then return normal byte[]
				return this.disk.buffer( this.in.getInputStream() );

			case UINT:
				if( t.hasTag( 0x19 ) )
					return this.in.getFromNamespace( t.length(), loc );
				//$FALL-THROUGH$
			case NINT:
				if( t.hasTag( 0x01 ) ) // TODO And also float major 7:25/26/27
					return new Date( t.longValue() * 1000 );
				return t.longValue();

			case DFLOAT:
				return t.doubleValue();

			case BOOL:
				return t.booleanValue();

			case NULL:
			case BREAK:
			case EOF:
				return null;

			default:
				throw new UnsupportedOperationException( "Unexpected token: " + t );
		}
	}
}
