package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.constants.CClass;
import solidstack.classgen.constants.CMethodref;

public class CallMethod implements Expression
{
	private Expression instance;
	private String name;
	private String descriptor;
	private Expression[] parameters;

	private CMethodref methodref;

	public CallMethod( Expression instance, CMethodref methodref, Expression... parameters )
	{
		this.instance = instance;
		this.methodref = methodref;
		this.parameters = parameters;
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
		for( Expression parameter : this.parameters )
			parameter.getByteCode( bytes );
		bytes.writeByte( 0xB6 );
		bytes.writeShort( this.methodref.index() );
	}

	@Override
	public String getFieldDescriptor()
	{
		throw new UnsupportedOperationException();
	}
}
