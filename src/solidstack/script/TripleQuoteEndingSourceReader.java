package solidstack.script;

import solidstack.io.Resource;
import solidstack.io.SourceException;
import solidstack.io.SourceLocation;
import solidstack.io.SourceReader;

public class TripleQuoteEndingSourceReader implements SourceReader
{
	private SourceReader in;
	private boolean ended;
	private boolean dontEnd;

	public TripleQuoteEndingSourceReader( SourceReader in )
	{
		this.in = in;
	}

	@Override
	public void close()
	{
		this.in.close();
	}

	@Override
	public String readLine()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int getLineNumber()
	{
		return this.in.getLineNumber();
	}

	@Override
	public int read()
	{
		if( this.ended )
			return -1;

		int ch = this.in.read();
		if( ch == -1 )
			throw new SourceException( "Unexpected end of input", this.in.getLastLocation() );

		if( this.dontEnd || ch != '"' )
			return ch;

		this.in.mark();
		if( this.in.read() == '"' )
			if( this.in.read() == '"' )
				if( this.in.read() != '"' )
				{
					this.in.rewind();
					return -1;
				}

		this.in.reset();
		return ch;
	}

	@Override
	public int readRaw()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void rewind()
	{
		this.in.rewind();
	}

	@Override
	public void mark()
	{
		this.in.mark();
	}

	@Override
	public void reset()
	{
		this.in.reset();
	}

	@Override
	public void record()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRecorded()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Resource getResource()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public SourceLocation getLocation()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public SourceLocation getLastLocation()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEncoding()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean dontEnd( boolean dontEnd )
	{
		boolean old = this.dontEnd;
		this.dontEnd = dontEnd;
		return old;
	}
}
