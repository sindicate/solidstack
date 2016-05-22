package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.Types;
import solidstack.classgen.Types.VMTYPE;
import solidstack.classgen.constants.CMethodref;

public class CallMethod implements Expression
{
	private CMethodref methodref;
	private Expression instance;
	private Expression[] parameters;


	public CallMethod( Expression instance, CMethodref methodref, Expression... parameters )
	{
		this.instance = instance;
		this.methodref = methodref;
		this.parameters = parameters;
	}

	@Override
	public VMTYPE vmType()
	{
		return Types.methodDescriptorToVMType( this.methodref.type().value() );
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		this.instance.toByteCode( bytes );
		for( Expression parameter : this.parameters )
			parameter.toByteCode( bytes );
		bytes.writeByte( 0xB6 ); // invokevirtual
		bytes.writeShort( this.methodref.index() );
	}
}
