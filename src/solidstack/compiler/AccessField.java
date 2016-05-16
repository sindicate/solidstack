package solidstack.compiler;

public class AccessField implements Expression
{
	private Expression instance;
	private String name;
	private String descriptor;

	private ConstantFieldref fieldref;

	public AccessField( Expression instance, ConstantFieldref fieldref )
	{
		this.instance = instance;
		this.fieldref = fieldref;
	}

	@Override
	public ConstantClass classInfo()
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
