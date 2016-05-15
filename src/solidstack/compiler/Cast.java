package solidstack.compiler;

public class Cast implements Expression
{
	private Expression value;
	private String type;

	private ConstantClass classInfo;

	public Cast( Expression value, String type )
	{
		this.value = value;
		this.type = type;
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
	public void collectConstants( ConstantPool pool )
	{
		this.value.collectConstants( pool );

		this.classInfo = pool.add( new ConstantClass( pool, this.type ) );
	}

	@Override
	public String getFieldDescriptor()
	{
		return Types.classNameToFieldDescriptor( this.type );
	}
}
