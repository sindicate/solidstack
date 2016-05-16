package solidstack.compiler;

public class CallSuper implements Statement
{
	private Expression instance;
	private String name;

	private ConstantMethodref methodref;

	public CallSuper( Expression instance, ConstantMethodref methodref )
	{
		this.instance = instance;
		this.methodref = methodref;
	}

	@Override
	public void getByteCode( Bytes bytes )
	{
		this.instance.getByteCode( bytes );
		bytes.writeByte( 0xB7 ); // invokespecial
		bytes.writeShort( this.methodref.index() );
	}
}
