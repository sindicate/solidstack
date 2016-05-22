package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.Types;
import solidstack.classgen.Types.VMTYPE;
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
	public VMTYPE vmType()
	{
		return Types.methodDescriptorToVMType( this.methodref.type().value() );
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		for( Expression parameter : this.parameters )
			parameter.toByteCode( bytes );
		bytes.writeByte( 0xB8 ); // invokestatic
		bytes.writeShort( this.methodref.index() );
	}
}
