package solidstack.script.scopes;

import java.util.Map;

import funny.Symbol;

public class MapScope extends AbstractScope
{
	private Map<Object, Object> map;

	public MapScope( Map<Object, Object> map )
	{
		this.map = map;
	}

	@Override
	public Object get( Symbol symbol )
	{
		Object result = this.map.get( symbol.toString() );
		if( result != null )
			return result;
		if( this.map.containsKey( symbol.toString() ) )
			return null;
		throw new UndefinedException();
	}

	@Override
	protected void set0( Symbol symbol, Object value )
	{
		if( !this.map.containsKey( symbol.toString() ) )
			throw new UndefinedException();
		this.map.put( symbol.toString(), value );
	}

//	@Override
//	public Ref findRef( Symbol symbol )
//	{
//		return new MapRef( symbol.toString() );
//	}

	@Override
	public Variable var( Symbol symbol, Object value )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Value val( Symbol symbol, Object value )
	{
		throw new UnsupportedOperationException();
	}

	public class MapRef implements Ref
	{
		private String key;

		public MapRef( String key )
		{
			this.key = key;
		}

		public Object get()
		{
			return MapScope.this.map.get( this.key );
		}

		public void set( Object value )
		{
			MapScope.this.map.put( this.key, value );
		}

		public boolean isUndefined()
		{
			return MapScope.this.map.containsKey( this.key );
		}

		public Symbol getKey()
		{
			return Symbol.apply( this.key );
		}
	}
}
