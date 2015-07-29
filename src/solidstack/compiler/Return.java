package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

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
	public void collectConstants( ConstantPool pool )
	{
		if( this.expression != null )
			this.expression.collectConstants( pool );
	}

	@Override
	public void getByteCode( DataOutputStream out ) throws IOException
	{
		if( this.expression != null )
		{
			this.expression.getByteCode( out );
			out.writeByte( 0xAC ); // ireturn
		}
		else if( this.value == null )
			out.writeByte( 0xB1 ); // return
		else
		{
			out.writeByte( 0x03 ); // iconst_0
			out.writeByte( 0xAC ); // ireturn
		}
	}
}
