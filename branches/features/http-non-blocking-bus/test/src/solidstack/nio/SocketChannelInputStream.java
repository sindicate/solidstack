package solidstack.nio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import solidstack.httpserver.FatalSocketException;
import solidstack.lang.Assert;


public class SocketChannelInputStream extends InputStream
{
	private SocketChannelHandler handler;
	private ByteBuffer buffer;

	public SocketChannelInputStream( SocketChannelHandler handler )
	{
		this.handler = handler;
		this.buffer = ByteBuffer.allocate( 1024 );
		this.buffer.flip();
	}

	@Override
	public int read() throws IOException
	{
		if( !this.buffer.hasRemaining() )
		{
			if( this.handler == null )
				return -1;
			readChannel();
		}

		return this.buffer.get();
	}

	@Override
	public int read( byte[] b, int off, int len ) throws IOException
	{
		if( !this.buffer.hasRemaining() )
		{
			if( this.handler == null )
				return -1;
			readChannel();
		}

		if( len > this.buffer.remaining() )
			len = this.buffer.remaining();
		this.buffer.get( b, off, len );
		return len;
	}

	@Override
	public int available() throws IOException
	{
		return this.buffer.remaining();
	}

	// TODO What if it read too much? Like when 2 requests are chained. The handler needs to keep reading.
	protected void readChannel()
	{
		SocketChannel channel = this.handler.getChannel();
		int id = DebugId.getId( channel );

		Assert.isFalse( this.buffer.hasRemaining() );
		Assert.isTrue( channel.isOpen() );

		this.buffer.clear();

		try
		{
			int read = channel.read( this.buffer );
			System.out.println( "Channel (" + id + ") read #" + read + " bytes from channel (1)" );
			while( read == 0 )
			{
				this.handler.getDispatcher().read( this.handler.getKey() );
				try
				{
					synchronized( this )
					{
						wait();
					}
				}
				catch( InterruptedException e )
				{
					throw new FatalSocketException( e );
				}

				read = channel.read( this.buffer );
				System.out.println( "Channel (" + id + ") read #" + read + " bytes from channel (2)" );
			}

			if( read == -1 )
			{
				channel.close(); // TODO Should cancel all keys
				this.handler = null;
			}
			else
				this.buffer.flip();
		}
		catch( IOException e )
		{
			throw new FatalSocketException( e );
		}
	}
}
