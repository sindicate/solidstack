package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.constants.CClass;
import solidstack.classgen.constants.CFieldref;

public class AccessField implements Expression
{
	private Expression instance;
	private String name;
	private String descriptor;

	private CFieldref fieldref;

	public AccessField( Expression instance, CFieldref fieldref )
	{
		this.instance = instance;
		this.fieldref = fieldref;
	}

	@Override
	public CClass classInfo()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void getByteCode( Bytes bytes )
	{
		this.instance.getByteCode( bytes );
		bytes.writeByte( 0xB4 );
		bytes.writeShort( this.fieldref.index() );
	}

	@Override
	public String getFieldDescriptor()
	{
		return this.descriptor;
	}
}
