package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

public class Return implements Statement
{
	private Integer value;

	public Return()
	{
	}

	public Return( int value )
	{
		this.value = value; // TODO
	}

	@Override
	public void collectConstants( ConstantPool pool )
	{
	}

	@Override
	public void getByteCode( DataOutputStream out ) throws IOException
	{
		if( this.value == null )
			out.writeByte( 0xB1 ); // return
		else
		{
			out.writeByte( 0x03 ); // iconst_0
			out.writeByte( 0xAC ); // ireturn
		}
	}
}
