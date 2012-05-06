package solidstack.nio;

import java.nio.channels.SelectionKey;
import java.util.concurrent.atomic.AtomicBoolean;

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

	@Override
	public void dataIsReady()
	{
		// Not running -> not waiting, no notify needed
		if( !isRunningAndSet() )
			getDispatcher().execute( this ); // TODO Also for write
		else
			super.dataIsReady();
	}

	public void run()
	{
		boolean complete = false;
		try
		{
			SelectionKey key = getKey();

			try
			{
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
						Assert.fail( "Channel (" + getId() + ") Shouldn't come here (yet): available = " + getInputStream().available() );
					}
					else
					{
						complete = true;
						return;
					}
				}
			}
			finally
			{
				if( !complete )
				{
					close();
					if( Loggers.nio.isDebugEnabled() )
						Loggers.nio.trace( "Channel ({}) task aborted", getId() );
				}
				else
					if( Loggers.nio.isDebugEnabled() )
						Loggers.nio.trace( "Channel ({}) task complete", getId() );

				endOfRunning();
			}
		}
		catch( Throwable t ) // TODO Exception, not Throwable
		{
			Loggers.nio.debug( "Unhandled exception", t );
		}
	}
}
