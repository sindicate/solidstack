/*--
 * Copyright 2010 René M. de Bloois
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

import java.io.IOException;
import java.io.Reader;


/**
 * A source reader that reads from a reader.
 *
 * @author René M. de Bloois
 */
public class ReaderSourceReader implements SourceReader
{
	/**
	 * The reader used to read from.
	 */
	private Reader reader;

	/**
	 * The current location.
	 */
	private SourceLocation location;

	/**
	 * The last location.
	 */
	private SourceLocation lastLocation;

	/**
	 * Buffer to contain a character that has been read by mistake.
	 */
	private int buffer = -1;

	/**
	 * The last character read.
	 */
	private int last = -1;

	/**
	 * The before last character read.
	 */
	private int lastlast = -1;

	/**
	 * Buffer to contain the line that is being read.
	 */
	private StringBuilder line;

	/**
	 * The character encoding of the resource.
	 */
	private String encoding;


	/**
	 * @param reader The reader to read from.
	 */
	public ReaderSourceReader( Reader reader )
	{
		this( reader, new SourceLocation( null, 1 ), null );
	}

	/**
	 * @param reader The reader to read from.
	 * @param location The location.
	 */
	public ReaderSourceReader( Reader reader, SourceLocation location )
	{
		this( reader, location, null );
	}

	/**
	 * @param reader The reader to read from.
	 * @param location The location.
	 * @param encoding The encoding used.
	 */
	public ReaderSourceReader( Reader reader, SourceLocation location, String encoding )
	{
		this.reader = reader;
		this.location = location;
		this.encoding = encoding;
	}

	/**
	 * Close the reader and the underlying reader.
	 */
	public void close()
	{
		if( this.reader == null )
			return;

		try
		{
			this.reader.close();
		}
		catch( IOException e )
		{
			throw new FatalIOException( e );
		}

		this.reader = null;
	}

	public String readLine()
	{
		if( this.line == null )
			this.line = new StringBuilder();
		this.line.setLength( 0 ); // TODO Maybe dump the stringbuilder if it is becoming too big

		int ch;
		while( true )
			switch( ch = read() )
			{
				case -1:
					if( this.line.length() == 0 )
						return null;
					this.location = this.location.nextLine(); // Not incremented by read(), so do it here
					//$FALL-THROUGH$
				case '\n':
					return this.line.toString();
				default:
					this.line.append( (char)ch );
			}
	}

	public int getLineNumber()
	{
		if( this.reader == null )
			throw new IllegalStateException( "Closed" );
		return this.location.getLineNumber();
	}

	public int read()
	{
		int result = readRaw();
		switch( result )
		{
			case '\r':
				result = readRaw();
				if( result != '\n' )
					this.buffer = result;
				//$FALL-THROUGH$
			case '\n':
				return '\n';
			default:
				return result;
		}
	}

	public int readRaw()
	{
		if( this.reader == null )
			throw new IllegalStateException( "Closed" );

		this.lastLocation = this.location;
		try
		{
			int result;
			if( this.buffer >= 0 )
			{
				result = this.buffer;
				this.buffer = -1;
			}
			else
				result = this.reader.read();

			switch( result )
			{
				case '\r':
					this.location = this.location.nextLine();
					this.lastlast = this.last;
					this.last = result;
					return result;
				case '\n':
					if( this.last != '\r' )
						this.location = this.location.nextLine();
					//$FALL-THROUGH$
				default:
					this.last = result;
					this.lastlast = this.last;
					return result;
			}
		}
		catch( IOException e )
		{
			throw new FatalIOException( e );
		}
	}

	public void rewind()
	{
		if( this.buffer >= 0 )
			throw new IllegalStateException( "Rewind called twice" );
		this.buffer = this.last;
		this.last = this.lastlast;
		this.lastlast = -1;
		switch( this.buffer )
		{
			case '\r':
				this.location = this.location.previousLine();
				break;
			case '\n':
				if( this.last != '\r' )
					this.location = this.location.previousLine();
		}
	}

	public Resource getResource()
	{
		return this.location.getResource();
	}

	public String getEncoding()
	{
		return this.encoding;
	}

	public SourceLocation getLocation()
	{
		if( this.reader == null )
			throw new IllegalStateException( "Closed" );
		return this.location;
	}

	public SourceLocation getLastLocation()
	{
		if( this.reader == null )
			throw new IllegalStateException( "Closed" );
		if( this.lastLocation == null )
			throw new IllegalStateException( "No character read yet" );
		if( this.buffer >= 0 )
			throw new IllegalStateException( "Last location is not valid, pushed back a character" );
		return this.lastLocation;
	}

	// This is only used to push back the first character (for byte order mark detection)
	void push( int ch )
	{
		if( this.buffer != -1 )
			throw new IllegalStateException( "Buffer is not empty" );
		this.buffer = ch;
	}
}
