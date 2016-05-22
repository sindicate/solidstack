package solidstack.script.scopes;

import funny.Symbol;
import solidstack.script.scopes.ValueMap.Entry;


public class Value extends Entry
{
	Object value;

	Value( Symbol symbol, Object value )
	{
		super( symbol );

//		Assert.notNull( value );
		this.value = value;
	}

	public Object get()
	{
		return this.value;
	}

	public void set( Object value )
	{
		throw new ImmutableException( getKey() );
	}

	public boolean isUndefined()
	{
		return false;
	}
}
