package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.constants.CClass;

public class LiteralClass implements Expression
{
	private CClass value;

	public LiteralClass( CClass value )
	{
		this.value = value;
	}

	@Override
	public CClass classInfo()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void getByteCode( Bytes bytes )
	{
		int i = this.value.index();
		if( i > 255 )
			throw new UnsupportedOperationException();

		bytes.writeByte( 0x12 ); // ldc
		bytes.writeByte( i );
	}

	@Override
	public String getFieldDescriptor()
	{
		throw new UnsupportedOperationException();
	}
}
