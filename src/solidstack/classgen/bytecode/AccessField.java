package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.Types;
import solidstack.classgen.Types.VMTYPE;
import solidstack.classgen.constants.CFieldref;

public class AccessField implements Expression
{
	private Expression instance;
	private CFieldref fieldref;


	public AccessField( Expression instance, CFieldref fieldref )
	{
		this.instance = instance;
		this.fieldref = fieldref;
	}

	@Override
	public VMTYPE vmType()
	{
		return Types.fieldDescriptorToVMType( this.fieldref.type().value() );
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		this.instance.toByteCode( bytes );
		bytes.writeByte( 0xB4 );
		bytes.writeShort( this.fieldref.index() );
	}
}
