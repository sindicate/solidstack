package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;

public class Return implements Statement
{
	private Integer value;
	private Expression expression;

	public Return()
	{
	}

	public Return( int value )
	{
		this.value = value; // TODO
	}

	public Return( Expression value )
	{
		this.expression = value;
	}

	@Override
	public void getByteCode( Bytes bytes )
	{
		if( this.expression != null )
		{
			this.expression.getByteCode( bytes );
			bytes.writeByte( 0xAC ); // ireturn
		}
		else if( this.value == null )
			bytes.writeByte( 0xB1 ); // return
		else
		{
			bytes.writeByte( 0x03 ); // iconst_0
			bytes.writeByte( 0xAC ); // ireturn
		}
	}
}
