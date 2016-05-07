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

/**
 * A source reader that reads from a string. Advantage is that fragments can be returned with the recorder that take no space in memory.
 *
 * @author René M. de Bloois
 */
public class StringSourceReader implements SourceReader
{
	/**
	 * The string to read from.
	 */
	private String string;

	/**
	 * The current position of the reader.
	 */
	private int pos;

	/**
	 * The current location.
	 */
	private SourceLocation location;

	/**
	 * The last location.
	 */
	private SourceLocation lastLocation;

	/**
	 * Marks a position so that it can be restored.
	 */
	private int mark = -1;

	/**
	 * Marks the start of the recording.
	 */
	private int recorder = -1;


	/**
	 * @param reader The reader to read from.
	 */
	public StringSourceReader( String string )
	{
		this( string, new SourceLocation( null, 1 ) );
	}

	/**
	 * @param reader The reader to read from.
	 * @param location The location.
	 */
	public StringSourceReader( String string, SourceLocation location )
	{
		this.string = string;
		this.location = location;
	}

	/**
	 * Close any resources.
	 */
	@Override
	public void close()
	{
	}

	@Override
	public String readLine()
	{
		record();

		while( true )
			switch( read() )
			{
				case -1:
					String result = getRecorded();
					if( result.length() == 0 )
						return null;
					this.location = this.location.nextLine(); // Not incremented by read(), so do it here
					return result;

				case '\n':
					rewind();
					result = getRecorded();
					read();
					return result;
			}
	}

	@Override
	public int getLineNumber()
	{
		return this.location.getLineNumber();
	}

	@Override
	public int read()
	{
		int result = readRaw();
		switch( result )
		{
			case '\r':
				result = readRaw();
				if( result != '\n' )
					this.pos--;
				//$FALL-THROUGH$
			case '\n':
				return '\n';
			default:
				return result;
		}
	}

	@Override
	public int readRaw()
	{
		this.lastLocation = this.location;

		if( this.pos >= this.string.length() )
			return -1;

		int result = this.string.charAt( this.pos++ );
		switch( result )
		{
			case '\r':
				this.location = this.location.nextLine();
				break;
			case '\n':
				if( this.pos < 2 || this.string.charAt( this.pos - 2 ) != '\r' )
					this.location = this.location.nextLine();
		}
		return result;
	}

	@Override
	public void rewind()
	{
		switch( this.string.charAt( --this.pos ) )
		{
			case '\n':
				if( this.string.charAt( this.pos - 1 ) == '\r' )
					this.pos--;
				//$FALL-THROUGH$
			case '\r':
				this.location = this.location.previousLine();
		}
	}

	@Override
	public void mark()
	{
		this.mark = this.pos;
	}

	@Override
	public void reset()
	{
		if( this.mark < 0 )
			throw new FatalIOException( "Mark not set" );
		this.pos = this.mark;
	}

	@Override
	public void record()
	{
		if( this.recorder >= 0 )
			throw new FatalIOException( "Already recording" );
		this.recorder = this.pos;
	}

	@Override
	public String getRecorded()
	{
		if( this.recorder < 0 )
			throw new FatalIOException( "Not recording" );
		String result = this.string.substring( this.recorder, this.pos );
		this.recorder = -1;
		return result;
	}

	@Override
	public Resource getResource()
	{
		return this.location.getResource();
	}

	@Override
	public String getEncoding()
	{
		return null;
	}

	@Override
	public SourceLocation getLocation()
	{
		return this.location;
	}

	@Override
	public SourceLocation getLastLocation()
	{
		if( this.lastLocation == null )
			throw new IllegalStateException( "No character read yet" );
		// TODO How are we going to do this?
//		if( this.buffer.hasRemaining() )
//			throw new IllegalStateException( "Last location is not valid, pushed back a character" );
		return this.lastLocation;
	}
}
