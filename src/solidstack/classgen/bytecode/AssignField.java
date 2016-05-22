package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.constants.CFieldref;

public class AssignField implements Statement
{
	private Expression instance;
	private String name;
	private String descriptor;
	private Expression value;

	private CFieldref fieldref;

	public AssignField( Expression instance, CFieldref fieldref, Expression value )
	{
		this.instance = instance;
		this.fieldref = fieldref;
		this.value = value;
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		this.instance.toByteCode( bytes );
		this.value.toByteCode( bytes );
		bytes.writeByte( 0xB5 ); // putfield
		bytes.writeShort( this.fieldref.index() );
	}
}
