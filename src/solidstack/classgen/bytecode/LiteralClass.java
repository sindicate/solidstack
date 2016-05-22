package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.Types.VMTYPE;
import solidstack.classgen.constants.CClass;

public class LiteralClass implements Expression
{
	private CClass value;

	public LiteralClass( CClass value )
	{
		this.value = value;
	}

	@Override
	public VMTYPE vmType()
	{
		return VMTYPE.REF;
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		int i = this.value.index();
		if( i > 255 )
			throw new UnsupportedOperationException();

		bytes.writeByte( 0x12 ); // ldc
		bytes.writeByte( i );
	}
}
