package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.Types.TYPE;

public class Return implements Statement
{
	private Expression expression;
	private TYPE type;

	public Return()
	{
	}

	public Return( Expression value, TYPE type )
	{
		this.expression = value;
		this.type = type;
	}

	@Override
	public void getByteCode( Bytes bytes )
	{
		if( this.expression != null )
		{
			this.expression.getByteCode( bytes );
			int b;
			switch( this.type )
			{
				case INT: b = 0xAC; break; // ireturn
				case LONG: b = 0xAD; break; // lreturn
				case FLOAT: b = 0xAE; break; // freturn
				case DOUBLE: b = 0xAF; break; // dreturn
				case REF: b = 0xB0; break; // areturn
				default:
					throw new UnsupportedOperationException( this.type.toString() );
			}
			bytes.writeByte( b );
		}
		else
			bytes.writeByte( 0xB1 ); // return
	}
}
