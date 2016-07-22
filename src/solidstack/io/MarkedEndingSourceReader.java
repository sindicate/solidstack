package solidstack.io;

public class MarkedEndingSourceReader implements SourceReader
{
	private SourceReader in;
	private char[] marker;
	private boolean ended;
	private boolean dontEnd;

	public MarkedEndingSourceReader( SourceReader in, String marker )
	{
		this.in = in;
		this.marker = marker.toCharArray();
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
		if( this.dontEnd )
		{
			if( ch == -1 )
				throw new SourceException( "Unexpected end of input", this.in.getLastLocation() );
			return ch;
		}
		if( ch == this.marker[ 0 ] )
		{
			this.in.mark();
			int len = this.marker.length;
			for( int i = 1; i < len; i++ )
				if( this.in.read() != this.marker[ i ] )
				{
					this.in.reset();
					return ch;
				}
			return -1;
		}
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
