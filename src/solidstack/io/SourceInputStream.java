package solidstack.io;

import java.io.IOException;
import java.io.InputStream;

public class SourceInputStream extends InputStream
{
	private InputStream in;
	private Resource resource;

	private long pos;


	public SourceInputStream( InputStream in, Resource resource, long pos )
	{
		this.in = in;
		this.resource = resource;
		this.pos = pos;
	}

	@Override
	public void close()
	{
		try
		{
			this.in.close();
		}
		catch( IOException e )
		{
			throw new FatalIOException( e );
		}
	}

	public Resource getResource()
	{
		return this.resource;
	}

	public long getPos()
	{
		return this.pos;
	}

	@Override
	public int read() throws IOException
	{
		int result = this.in.read();
		if( result >= 0 )
			this.pos++;
		return result;
	}

	@Override
	public int read( byte[] b, int off, int len ) throws IOException
	{
		int read = this.in.read( b, off, len );
		if( read > 0 )
			this.pos += read;
		return read;
	}
}
