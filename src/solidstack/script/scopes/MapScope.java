package solidstack.script.scopes;

import java.util.Map;

import funny.Symbol;

public class MapScope implements Scope
{
	private Scope parent;
	private Map<Object, Object> map;


	public MapScope( Map<Object, Object> map )
	{
		this.map = map;
	}

	public MapScope( Map<Object, Object> map, Scope parent )
	{
		this.map = map;
		this.parent = parent;
	}

	@Override
	public void setOrCreate( Symbol symbol, Object value )
	{
		try
		{
			set( symbol, value );
		}
		catch( UndefinedException e )
		{
			var( symbol, value );
		}
	}

	@Override
	public void set( Symbol symbol, Object value )
	{
		if( this.map.containsKey( symbol.toString() ) )
			this.map.put( symbol.toString(), value );
		else
		{
			if( this.parent == null )
				throw new UndefinedException();
			this.parent.set( symbol, value );
		}
	}

	@Override
	public void var( Symbol symbol, Object value )
	{
		this.map.put( symbol.toString(), value );
	}

	@Override
	public void val( Symbol symbol, Object value )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get( Symbol symbol )
	{
		if( this.map.containsKey( symbol.toString() ) )
			return this.map.get( symbol.toString() );
		if( this.parent == null )
			throw new UndefinedException();
		return this.parent.get( symbol );
	}

	public Object apply( Symbol symbol, Object... args )
	{
		return DefaultScope.apply( get( symbol ), args );
	}

	public Object apply( Symbol symbol, Map args )
	{
		return DefaultScope.apply( get( symbol ), args );
	}
}
