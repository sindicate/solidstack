package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.Types.VMTYPE;
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
	public VMTYPE vmType()
	{
		return VMTYPE.REF; // I think there won't be primitive casts
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		this.value.toByteCode( bytes );
		bytes.writeByte( 0xC0 );
		bytes.writeShort( this.classInfo.index() );
	}
}
