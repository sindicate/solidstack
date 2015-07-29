package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

public class Return extends Statement
{
	private int value;

	public Return( int value )
	{
		this.value = value; // TODO
	}

	@Override
	public void getByteCode( DataOutputStream out ) throws IOException
	{
		out.writeByte( 0x03 ); // iconst_0
		out.writeByte( 0xAC ); // ireturn
	}
}
