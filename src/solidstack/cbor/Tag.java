package solidstack.cbor;

import solidstack.script.java.DefaultClassExtensions;

public class Tag
{
	long value;
	Token[] parameters;

	public Tag( long value )
	{
		this.value = value;
	}

	public void setParameters( Token[] parameters )
	{
		this.parameters = parameters;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder( "TAG 0x" );
		SimpleToken.appendHex( result, this.value );
		if( this.parameters != null )
			result.append( DefaultClassExtensions.mkString( this.parameters, "(", ", ", ")" ) );
		return result.toString();
	}
}
