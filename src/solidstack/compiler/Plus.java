package solidstack.compiler;

public class Plus implements Expression
{
	private Expression left;
	private Expression right;

	public Plus( Expression left, Expression right )
	{
		this.left = left;
		this.right = right;
	}

	@Override
	public ConstantClass classInfo()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void getByteCode( Bytes bytes )
	{
		this.left.getByteCode( bytes );
		this.right.getByteCode( bytes );
		bytes.writeByte( 0x60 ); // iadd
	}

	@Override
	public String getFieldDescriptor()
	{
		throw new UnsupportedOperationException();
	}
}
