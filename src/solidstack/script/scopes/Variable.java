package solidstack.script.scopes;

import funny.Symbol;

public class Variable<T> extends Value<T>
{
	Variable( Symbol symbol, T value )
	{
		super( symbol, value );
	}

	@Override
	public void set( T value )
	{
		this.value = value;
	}
}
