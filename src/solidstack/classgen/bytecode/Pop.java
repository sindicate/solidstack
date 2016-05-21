package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.Types.TYPE;

public class Pop implements Statement
{
	private Expression expression;
	private TYPE type;

	public Pop( Expression value, TYPE type )
	{
		this.expression = value;
		this.type = type;
	}

	@Override
	public void getByteCode( Bytes bytes )
	{
		this.expression.getByteCode( bytes );
		int b;
		switch( this.type )
		{
			case REF: case INT: case FLOAT: b = 0x57; break; // pop
			case LONG: case DOUBLE: b = 0x58; break; // pop2
			default: throw new UnsupportedOperationException( this.type.toString() );
		}
		bytes.writeByte( b );
	}
}
