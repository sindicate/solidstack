package solidstack.script.scopes;

import funny.Symbol;

public class Variable extends Value
{
	Variable( Symbol symbol, Object value )
	{
		super( symbol, value );
	}

	@Override
	public void set( Object value )
	{
		this.value = value;
	}
}