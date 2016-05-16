package solidstack.compiler;

public class CallMethod implements Expression
{
	private Expression instance;
	private String name;
	private String descriptor;
	private Expression[] parameters;

	private ConstantMethodref methodref;

	public CallMethod( Expression instance, ConstantMethodref methodref, Expression... parameters )
	{
		this.instance = instance;
		this.methodref = methodref;
		this.parameters = parameters;
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
		for( Expression parameter : this.parameters )
			parameter.getByteCode( bytes );
		bytes.writeByte( 0xB6 );
		bytes.writeShort( this.methodref.index() );
	}

	@Override
	public String getFieldDescriptor()
	{
		throw new UnsupportedOperationException();
	}
}
