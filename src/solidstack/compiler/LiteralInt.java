package solidstack.compiler;

public class LiteralInt implements Expression
{
	private int value;

	public LiteralInt( int value )
	{
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
		ByteCode.iconst( bytes, this.value );
	}

	@Override
	public String getFieldDescriptor()
	{
		throw new UnsupportedOperationException();
	}
}
