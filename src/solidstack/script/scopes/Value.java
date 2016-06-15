package solidstack.script.scopes;

import funny.Symbol;
import solidstack.script.scopes.ValueMap.Entry;


public class Value<T> extends Entry
{
	T value;

	Value( Symbol symbol, T value )
	{
		super( symbol );

//		Assert.notNull( value );
		this.value = value;
	}

	public T get()
	{
		return this.value;
	}

	public void set( T value )
	{
		throw new ImmutableException( getKey() );
	}

	public boolean isUndefined()
	{
		return false;
	}
}
