package solidstack.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;


public class HexInputStreamReader extends Reader
{
	static private final char[] HEX = "0123456789ABCDEF".toCharArray();

	private InputStream in;
	private String prefix;
	private int buffer = -1;


	public HexInputStreamReader( InputStream in )
	{
		this.in = in;
	}

	public HexInputStreamReader( String prefix, InputStream in )
	{
		this.prefix = prefix;
		this.in = in;
	}

	@Override
	public int read() throws IOException
	{
		if( this.buffer >= 0 )
		{
			int result = this.buffer;
			this.buffer = -1;
			return result;
		}
		if( this.prefix != null )
		{
			if( this.prefix.length() > 0 )
			{
				int ch = this.prefix.charAt( 0 );
				this.prefix = this.prefix.substring( 1, this.prefix.length() );
				return ch;
			}
			this.prefix = null;
		}
		int b = this.in.read();
		if( b == -1 )
			return -1;
		this.buffer = HEX[ b & 15 ];
		int result = HEX[ b >>> 4 ];
		return result;
	}

	@Override
	public int read( char[] cbuf, int off, int len ) throws IOException
	{
		int i;
		for( i = 0; i < len; i++ )
		{
			int ch = read();
			if( ch == -1 )
				return i == 0 ? -1 : i;
			cbuf[ off++ ] = (char)ch;
		}
		return i;
	}

	@Override
	public void close() throws IOException
	{
		this.in.close();
	}
}
