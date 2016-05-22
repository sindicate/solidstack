package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.Types.VMTYPE;

public class LiteralInt implements Expression
{
	private int value;

	public LiteralInt( int value )
	{
		this.value = value;
	}

	@Override
	public VMTYPE vmType()
	{
		return VMTYPE.INT;
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		ByteCode.iconst( bytes, this.value );
	}
}
