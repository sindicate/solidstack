package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;

public class Pop implements Statement
{
	private Expression expression;

	public Pop( Expression value )
	{
		this.expression = value;
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		this.expression.toByteCode( bytes );
		int b;
		switch( this.expression.vmType() ) // TODO Or we could return the type from the getByteCode
		{
			case REF: case INT: case FLOAT: b = 0x57; break; // pop
			case LONG: case DOUBLE: b = 0x58; break; // pop2
			default: throw new UnsupportedOperationException( this.expression.vmType().toString() );
		}
		bytes.writeByte( b );
	}
}
