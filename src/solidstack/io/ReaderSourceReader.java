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

import solidstack.util.IntWindowBuffer;


/**
 * A source reader that reads from a reader.
 *
 * @author René M. de Bloois
 */
public class ReaderSourceReader implements SourceReader
{
	/**
	 * The reader to read from.
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
	 * Buffer that contains the last characters that have been read.
	 */
	private IntWindowBuffer buffer = new IntWindowBuffer( 6 ); // 3 times /r/n

	/**
	 * To record the characters that have been read.
	 */
	private StringBuilder recorder;

	/**
	 * The character encoding of the resource.
	 */
	private String encoding;

	private boolean dontEnd;


	/**
	 * @param reader The reader to read from.
	 */
	public ReaderSourceReader( Reader reader )
	{
		this( reader, SourceLocation.forText( null, 1 ), null );
	}

	/**
	 * @param reader The reader to read from.
	 * @param location The location and line number of the resource.
	 */
	public ReaderSourceReader( Reader reader, SourceLocation location )
	{
		this( reader, location, null );
	}

	/**
	 * @param reader The reader to read from.
	 * @param location The location and line number of the resource.
	 * @param encoding The encoding of the resource.
	 */
	public ReaderSourceReader( Reader reader, SourceLocation location, String encoding )
	{
		this.reader = reader;
		this.location = location;
		this.encoding = encoding;
	}

	/**
	 * Close any resources.
	 */
	@Override
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
		if( this.reader == null )
			throw new IllegalStateException( "Closed" );
		return this.location.getLineNumber();
	}

	@Override
	public int read()
	{
		int result = readRaw();
		if( result == '\r' )
		{
			result = readRaw();
			if( result != '\n' )
			{
				this.buffer.rewind();
				result = '\n';
			}
		}
		return result;
	}

	@Override
	public int readRaw()
	{
		if( this.reader == null )
			throw new FatalIOException( "Closed" );

		this.lastLocation = this.location;
		try
		{
			int result;
			if( this.buffer.hasRemaining() )
				result = this.buffer.get();
			else
				this.buffer.put( result = this.reader.read() );

			switch( result )
			{
				case -1:
					if( this.dontEnd )
						throw new SourceException( "Unexpected end of input", this.lastLocation );
					return result;
				case '\r':
					this.location = this.location.nextLine();
					break;
				case '\n':
					if( this.buffer.beforeLast() != '\r' ) // If there is no beforeLast(), it will be 0
						this.location = this.location.nextLine();
			}

			if( this.recorder != null )
				this.recorder.append( (char)result );

			return result;
		}
		catch( IOException e )
		{
			throw new FatalIOException( e );
		}
	}

	@Override
	public void rewind()
	{
		switch( this.buffer.rewind() )
		{
			case -1:
				return;
			case '\n':
				if( this.buffer.rewind() != '\r' )
					this.buffer.get();
				else
					if( this.recorder != null )
						this.recorder.setLength( this.recorder.length() - 1 );
				//$FALL-THROUGH$
			case '\r':
				this.location = this.location.previousLine();
		}

		if( this.recorder != null )
			this.recorder.setLength( this.recorder.length() - 1 );
	}

	@Override
	public void mark()
	{
		this.buffer.mark();
	}

	@Override
	public void reset()
	{
		int delta = this.buffer.reset();
		if( this.recorder != null )
			this.recorder.setLength( this.recorder.length() - delta );
	}

	@Override
	public void record()
	{
		if( this.recorder != null )
			throw new FatalIOException( "Already recording" );
		this.recorder = new StringBuilder();
	}

	@Override
	public String getRecorded()
	{
		if( this.recorder == null )
			throw new FatalIOException( "Not recording" );
		String result = this.recorder.toString();
		this.recorder = null; // TODO Or keep the string builder?
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
		return this.encoding;
	}

	@Override
	public SourceLocation getLocation()
	{
		if( this.reader == null )
			throw new IllegalStateException( "Closed" );
		return this.location;
	}

	@Override
	public SourceLocation getLastLocation()
	{
		if( this.reader == null )
			throw new IllegalStateException( "Closed" );
		if( this.lastLocation == null )
			throw new IllegalStateException( "No character read yet" );
		if( this.buffer.hasRemaining() )
			throw new IllegalStateException( "Last location is not valid, pushed back a character" );
		return this.lastLocation;
	}

	@Override
	public boolean dontEnd( boolean dontEnd )
	{
		boolean old = this.dontEnd;
		this.dontEnd = dontEnd;
		return old;
	}
}
