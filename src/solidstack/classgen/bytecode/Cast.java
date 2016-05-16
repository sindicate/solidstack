package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.constants.CClass;

public class Cast implements Expression
{
	private CClass classInfo;
	private Expression value;

	public Cast( Expression value, CClass type )
	{
		this.classInfo = type;
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
		this.value.getByteCode( bytes );
		bytes.writeByte( 0xC0 );
		bytes.writeShort( this.classInfo.index() );
	}

	@Override
	public String getFieldDescriptor()
	{
		throw new UnsupportedOperationException();
//		return Types.classNameToFieldDescriptor( this.type );
	}
}
