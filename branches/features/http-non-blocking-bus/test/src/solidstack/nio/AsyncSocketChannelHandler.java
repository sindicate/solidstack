package solidstack.nio;

import java.nio.channels.SelectionKey;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import solidstack.httpserver.ApplicationContext;
import solidstack.lang.Assert;


/**
 * Thread that handles an incoming connection.
 *
 * @author Ren� M. de Bloois
 */
public class AsyncSocketChannelHandler extends SocketChannelHandler implements Runnable
{
	private ReadListener listener;
	private AtomicBoolean running = new AtomicBoolean();
	private AtomicInteger latch = new AtomicInteger( 0 );

	/**
	 * Constructor.
	 *
	 * @param socket The incoming connection.
	 * @param applicationContext The {@link ApplicationContext}.
	 */
	public AsyncSocketChannelHandler( Dispatcher dispatcher )
	{
		super( dispatcher );
	}

	public void setListener( ReadListener listener )
	{
		this.listener = listener;
	}

	protected ReadListener getListener()
	{
		return this.listener;
	}

	protected boolean isRunningAndSet()
	{
		return !this.running.compareAndSet( false, true );
	}

	protected void endOfRunning()
	{
		this.running.set( false );
	}

	public void doubleAcquire()
	{
		Assert.isTrue( this.latch.compareAndSet( 0, 2 ) );
		this.latch.set( 2 );
	}

	public void release()
	{
		int l = this.latch.decrementAndGet();
		if( l == 0 )
			returnToPool();
		else
			Assert.isTrue( l == 1 );
	}

	@Override
	public void dataIsReady()
	{
		// Not running -> not waiting, no notify needed
		if( !isRunningAndSet() )
		{
			getDispatcher().execute( this ); // TODO Also for write
			Loggers.nio.trace( "Channel ({}) Started thread", getDebugId() );
		}
		else
		{
			super.dataIsReady();
			Loggers.nio.trace( "Channel ({}) Signalled inputstream", getDebugId() );
		}
	}

	public void run()
	{
		boolean complete = false;
		try
		{
			Loggers.nio.trace( "Channel ({}) Thread started", getDebugId() );

			SelectionKey key = getKey();
			while( true )
			{
				getListener().incoming( this );

				if( isOpen() )
				{
					if( getInputStream().available() == 0 )
					{
						getDispatcher().listenRead( key );
						complete = true;
						return;
					}
					Assert.fail( "Channel (" + getDebugId() + ") Shouldn't come here (yet): available = " + getInputStream().available() );
				}
				else
				{
					complete = true;
					return;
				}
			}
		}
		catch( Throwable t ) // TODO Exception, not Throwable
		{
			Loggers.nio.debug( "Unhandled exception", t );
		}
		finally
		{
			endOfRunning(); // FIXME Also do this for the ServerSocketChannelHandler
			if( !complete )
			{
				close();
				Loggers.nio.trace( "Channel ({}) Thread aborted", getDebugId() );
			}
			else
			{
				release();
				Loggers.nio.trace( "Channel ({}) Thread complete", getDebugId() );
			}
		}
	}
}
