package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.constants.CClass;
import solidstack.classgen.constants.CMethodref;

public class CallStaticMethod implements Expression
{
	private CMethodref methodref;
	private Expression[] parameters;


	public CallStaticMethod( CMethodref methodref, Expression... parameters )
	{
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
		for( Expression parameter : this.parameters )
			parameter.getByteCode( bytes );
		bytes.writeByte( 0xB8 ); // invokestatic
		bytes.writeShort( this.methodref.index() );
	}

	@Override
	public String getFieldDescriptor()
	{
		throw new UnsupportedOperationException();
	}
}
