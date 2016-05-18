package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;

public class Pop implements Statement
{
	public Pop()
	{
	}

	@Override
	public void getByteCode( Bytes bytes )
	{
		bytes.writeByte( 0x57 ); // pop
		// TODO pop2
	}
}
