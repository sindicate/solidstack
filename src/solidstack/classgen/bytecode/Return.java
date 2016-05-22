package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;

public class Return implements Statement
{
	private Expression expression;

	public Return()
	{
	}

	public Return( Expression value )
	{
		this.expression = value;
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		if( this.expression != null )
		{
			this.expression.toByteCode( bytes );
			int b;
			switch( this.expression.vmType() ) // TODO Or we could return the type from the getByteCode
			{
				case INT: b = 0xAC; break; // ireturn
				case LONG: b = 0xAD; break; // lreturn
				case FLOAT: b = 0xAE; break; // freturn
				case DOUBLE: b = 0xAF; break; // dreturn
				case REF: b = 0xB0; break; // areturn
				default:
					throw new UnsupportedOperationException( this.expression.vmType().toString() );
			}
			bytes.writeByte( b );
		}
		else
			bytes.writeByte( 0xB1 ); // return
	}
}
