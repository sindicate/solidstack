package solidstack.compiler;

public class AssignField implements Statement
{
	private Expression instance;
	private String name;
	private String descriptor;
	private Expression value;

	private ConstantFieldref fieldref;

	public AssignField( Expression instance, ConstantFieldref fieldref, Expression value )
	{
		this.instance = instance;
		this.fieldref = fieldref;
		this.value = value;
	}

	@Override
	public void getByteCode( Bytes bytes )
	{
		this.instance.getByteCode( bytes );
		this.value.getByteCode( bytes );
		bytes.writeByte( 0xB5 ); // putfield
		bytes.writeShort( this.fieldref.index() );
	}
}
