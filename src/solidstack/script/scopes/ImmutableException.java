package solidstack.script.scopes;

import funny.Symbol;

public class ImmutableException extends RuntimeException
{
	public ImmutableException( Symbol key )
	{
		super( key.toString() );
	}
}
