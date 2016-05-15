package solidstack.compiler;

public class InitArray implements Expression
{
	private String type;
	private Expression[] values;

	private ConstantClass classInfo;

	public InitArray( String type, Expression... values )
	{
		this.type = type;
		this.values = values;
	}

	@Override
	public ConstantClass classInfo()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void getByteCode( Bytes bytes )
	{
		ByteCode.iconst( bytes, this.values.length );
		bytes.writeByte( 0xBD ); // anewarray
		bytes.writeShort( this.classInfo.index() );
		int i = 0;
		for( Expression value : this.values )
		{
			bytes.writeByte( 0x59 ); // dup
			ByteCode.iconst( bytes, i++ );
			value.getByteCode( bytes );
			bytes.writeByte( 0x53 ); // aastore
		}
	}

	@Override
	public void collectConstants( ConstantPool pool )
	{
		for( Expression value : this.values )
			value.collectConstants( pool );
		this.classInfo = pool.add( new ConstantClass( pool, this.type ) );
	}

	@Override
	public String getFieldDescriptor()
	{
		throw new UnsupportedOperationException();
	}
}
