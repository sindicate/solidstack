package solidstack.template.java.scope;

import java.util.HashMap;
import java.util.Map;


public class DefaultScope implements Scope
{
	private Map<String, Object> values;

	public DefaultScope()
	{
		this.values = new HashMap<String, Object>();
	}

	public DefaultScope( Map<String, Object> values )
	{
		this.values = values;
	}

	@Override
	public <T> T find( String key )
	{
		return (T)this.values.get( key );
	}

	@Override
	public <T> T get( String key )
	{
		if( !this.values.containsKey( key ) )
			throw new UndefinedException( "Undefined property: " + key );
		return find( key );
	}

	@Override
	public void set( String k, Object value )
	{
		throw new UnsupportedOperationException();
	}
}
