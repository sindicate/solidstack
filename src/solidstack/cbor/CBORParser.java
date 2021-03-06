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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import solidstack.cbor.Token.TYPE;
import solidstack.io.Resource;
import solidstack.io.SourceException;
import solidstack.io.SourceInputStream;
import solidstack.io.SourceLocation;


public class CBORParser
{
	static private enum STATE { IBYTES, ITEXT, IARRAYMAP, BYTES, TEXT, ARRAYMAP }

	CBORScanner in;

	private ReverseByteStringIndex index;
	private STATE state;
	private Stack<StateItem> states = new Stack<>();

	private long remaining;


	public CBORParser( SourceInputStream in )
	{
		this.in = new CBORScanner( in );
	}

	public void close()
	{
		this.in.close();
	}

	public CBORScanner getScanner()
	{
		return this.in;
	}

	public Resource getResource()
	{
		return this.in.getResource();
	}

	public SourceLocation getLocation()
	{
		return this.in.getLocation();
	}

	public Object getFromNamespace( int index, SourceLocation loc )
	{
		ByteString result = this.index.get( index );
		if( result == null )
			throw new SourceException( "Illegal string ref: " + index, loc );
		return result.toJava();
	}

	private Token get0()
	{
		CBORScanner in = this.in;

		SimpleToken t = in.get();
		if( !t.isTag() )
			return t;

		List<Tag> tags = new ArrayList<>();
		while( t.isTag() )
		{
			long value = t.value;
			Tag tag = new Tag( value );
			if( value == 0x102 )
			{
				List<Token> parameters = new ArrayList<>();
				SourceLocation loc = in.getLocation();
				t = in.get();
				if( t.type != TYPE.UINT )
					throw new SourceException( "Expected an UINT, not: " + t.type, loc );
				parameters.add( t );
				loc = in.getLocation();
				t = in.get();
				if( t.type != TYPE.UINT && t.type != TYPE.NULL )
					throw new SourceException( "Expected an UINT or NULL, not: " + t.type, loc );
				parameters.add( t );
				tag.setParameters( parameters.toArray( new Token[ parameters.size() ] ) );
			}
			tags.add( tag );
			t = in.get();
		}

		return t.withTags( tags.toArray( new Tag[ tags.size() ] ) );
	}

	public Token get()
	{
		if( this.state == STATE.BYTES || this.state == STATE.TEXT )
			throw new IllegalStateException( "Bytes or text is waiting to be read" );

		SourceLocation loc = this.in.getLocation();

		Token t = get0();
		switch( t.type() )
		{
			case MAP:
			case ARRAY:
				checkNotIString( t, loc );
				decRemaining();
				pushState( STATE.ARRAYMAP );
				this.remaining = t.longValue() * ( t.type() == TYPE.MAP ? 2 : 1 );
				newNamespace( t );
				return t;

			case EOF:
				if( this.state != null )
					throw newSourceException( t, loc );
				return t;

			case IARRAY:
			case IMAP:
				// TODO Must count even amount of items if MAP
				checkNotIString( t, loc );
				decRemaining();
				pushState( STATE.IARRAYMAP );
				newNamespace( t );
				return t;

			case BYTES:
				if( this.state == STATE.ITEXT )
					throw newSourceException( t, loc );
				decRemaining();
				pushState( STATE.BYTES );
				this.remaining = t.longValue();
				newNamespace( t ); // Could be that they want to exclude this string from the current namespace
				return t;

			case TEXT:
				if( this.state == STATE.IBYTES )
					throw newSourceException( t, loc );
				decRemaining();
				pushState( STATE.TEXT );
				this.remaining = t.longValue();
				newNamespace( t ); // Could be that they want to exclude this string from the current namespace
				return t;

			case IBYTES:
				checkNotIString( t, loc );
				decRemaining();
				pushState( STATE.IBYTES );
				return t;

			case ITEXT:
				checkNotIString( t, loc );
				decRemaining();
				pushState( STATE.ITEXT );
				return t;

			case UINT:
			case NINT:
			case DFLOAT:
			case BOOL:
			case NULL:
				checkNotIString( t, loc );
				decRemaining();
				return t;

			case BREAK:
				decRemaining();
				if( this.state != STATE.IARRAYMAP && this.state != STATE.IBYTES && this.state != STATE.ITEXT )
					throw newSourceException( t, loc );
				popState();
				return t;

			default:
				throw new UnsupportedOperationException( "Unexpected token: " + t );
		}
	}

	private void checkNotIString( Token token, SourceLocation loc )
	{
		if( this.state == STATE.ITEXT || this.state == STATE.IBYTES )
			throw newSourceException( token, loc );
	}

	private SourceException newSourceException( Token token, SourceLocation loc )
	{
		return new SourceException( "Unexpected " + token + " in parser state: " + this.state, loc );
	}

	private void decRemaining()
	{
		while( this.state == STATE.ARRAYMAP )
			if( this.remaining <= 0 )
				popState();
			else
			{
				this.remaining--;
				return;
			}
	}

	private void newNamespace( Token t )
	{
		Tag tag;
		if( ( tag = t.getTag( 0x102 ) ) != null )
		{
			Token[] pars = tag.parameters;
			this.index = new SlidingReverseByteStringIndex( pars[ 0 ].length(), pars[ 1 ].length() );
		}
		else if( t.getTag( 0x100 ) != null )
			this.index = new StandardReverseByteStringIndex();
	}

	public void readBytes( byte[] bytes )
	{
		if( this.state != STATE.BYTES )
			throw new SourceException( "Can't read bytes in parser state: " + this.state, this.in.getLocation() );
		this.in.readBytes( bytes );
		// TODO Check remaining
		popState();
		if( this.index != null )
			this.index.put( new ByteString( false, bytes ) );
	}

	public String readString( int len )
	{
		if( this.state != STATE.TEXT )
			throw new SourceException( "Can't read text in parser state: " + this.state, this.in.getLocation() );
		byte[] bytes = new byte[ len ];
		this.in.readBytes( bytes );
		// TODO Check remaining
		popState();
		if( this.index != null )
			this.index.put( new ByteString( true, bytes ) );
		return new String( bytes, CBORWriter.UTF8 );
	}

	void readBytesForStream( byte[] bytes )
	{
		this.in.readBytes( bytes );
		// TODO Check remaining
		popState();
	}

	public InputStream getInputStream()
	{
		if( this.state != STATE.ITEXT && this.state != STATE.IBYTES )
			throw new SourceException( "Can't stream bytes in parser state: " + this.state, this.in.getLocation() );
		return new BytesInputStream();
	}

	public Reader getReader()
	{
		if( this.state != STATE.ITEXT )
			throw new SourceException( "Can't stream text in parser state: " + this.state, this.in.getLocation() );
		return new InputStreamReader( new BytesInputStream(), CBORWriter.UTF8 );
	}

	private void pushState( STATE state )
	{
		this.states.push( new StateItem( this.state, this.index, this.remaining ) );
		this.state = state;
	}

	private void popState()
	{
		StateItem state = this.states.pop();
		if( state.state == STATE.ARRAYMAP && state.remaining == 0 )
			popState(); // again
		else
		{
			this.state = state.state;
			this.index = state.index;
			this.remaining = state.remaining;
		}
	}


	static private class StateItem
	{
		STATE state;
		ReverseByteStringIndex index;
		long remaining;

		StateItem( STATE state, ReverseByteStringIndex index, long remaining )
		{
			this.state = state;
			this.index = index;
			this.remaining = remaining;
		}
	}


	public class BytesInputStream extends InputStream
	{
		private byte[] buffer;
		private int pos;
		private boolean end;


		@Override
		public int read() throws IOException
		{
			if( this.end )
				return -1;

			int l;
			if( this.buffer == null || ( l = this.buffer.length - this.pos ) <= 0 )
				do
					l = fillBuffer();
				while( l == 0 );

			if( l < 0 )
				return -1;

			return this.buffer[ this.pos++ ];
		}

		@Override
		public int read( byte[] b, int off, int len ) throws IOException
		{
			if( this.end )
				return -1;

			int l;
			if( this.buffer == null || ( l = this.buffer.length - this.pos ) <= 0 )
				l = fillBuffer();

			if( l < 0 )
				return -1;

			if( len > l )
				len = l;
			System.arraycopy( this.buffer, this.pos, b, off, len );
			this.pos += len;
			return len;
		}

		private int fillBuffer()
		{
			Token t = get();
			if( t.type() == TYPE.BREAK )
			{
				this.end = true;
				return -1;
			}

			if( t.type() != TYPE.BYTES && t.type() != TYPE.TEXT ) // TODO Add the type to constructor
				throw new IllegalStateException( "Only byte or text strings allowed, not " + t.type() );

			this.buffer = new byte[ t.length() ];
			readBytesForStream( this.buffer );
			this.pos = 0;
			return t.length();
		}
	}
}
