package solidstack.compiler;

public class Cast implements Expression
{
	private ConstantClass classInfo;
	private Expression value;

	public Cast( Expression value, ConstantClass type )
	{
		this.classInfo = type;
		this.value = value;
	}

	@Override
	public ConstantClass classInfo()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void getByteCode( Bytes bytes )
	{
		this.value.getByteCode( bytes );
		bytes.writeByte( 0xC0 );
		bytes.writeShort( this.classInfo.index() );
	}

	@Override
	public String getFieldDescriptor()
	{
		throw new UnsupportedOperationException();
//		return Types.classNameToFieldDescriptor( this.type );
	}
}
