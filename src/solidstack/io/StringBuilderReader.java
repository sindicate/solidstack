package solidstack.io;

import java.io.IOException;
import java.io.Reader;

public class StringBuilderReader extends Reader
{
	private StringBuilder builder;
	private int pos;

	public StringBuilderReader( StringBuilder builder )
	{
		this.builder = builder;
	}

	@Override
	public int read() throws IOException
	{
		if( this.pos >= this.builder.length() )
			return -1;
		return this.builder.charAt( this.pos++ );
	}

	@Override
	public int read( char[] cbuf, int off, int len ) throws IOException
	{
		int l = this.builder.length();
		int p = this.pos;

		if( p >= l ) return -1;
		if( p + len > l ) len = l - p;

		this.builder.getChars( p, p + len, cbuf, off );
		this.pos = p + len;

		return len;
	}

	@Override
	public void close() throws IOException
	{
		// Nothing to do
	}
}
