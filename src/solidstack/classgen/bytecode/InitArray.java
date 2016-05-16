package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.constants.CClass;

public class InitArray implements Expression
{
	private CClass classInfo;
	private Expression[] values;


	public InitArray( CClass type, Expression... values )
	{
		this.classInfo = type;
		this.values = values;
	}

	@Override
	public CClass classInfo()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void getByteCode( Bytes bytes )
	{
		ByteCode.iconst( bytes, this.values.length );
		bytes.writeByte( 0xBD ); // anewarray
		bytes.writeShort( this.classInfo.index() );
		int i = 0;
		for( Expression value : this.values )
		{
			bytes.writeByte( 0x59 ); // dup
			ByteCode.iconst( bytes, i++ );
			value.getByteCode( bytes );
			bytes.writeByte( 0x53 ); // aastore
		}
	}

	@Override
	public String getFieldDescriptor()
	{
		throw new UnsupportedOperationException();
	}
}
