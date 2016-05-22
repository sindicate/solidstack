package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.constants.CMethodref;

public class CallSuper implements Statement
{
	private Expression instance;
	private String name;

	private CMethodref methodref;

	public CallSuper( Expression instance, CMethodref methodref )
	{
		this.instance = instance;
		this.methodref = methodref;
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		this.instance.toByteCode( bytes );
		bytes.writeByte( 0xB7 ); // invokespecial
		bytes.writeShort( this.methodref.index() );
	}
}
