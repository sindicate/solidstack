package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

public class CallSuper implements Statement
{
	private Expression instance;
	private String name;

	private ConstantMethodref methodref;

	public CallSuper( Expression instance, String name )
	{
		this.instance = instance;
		this.name = name;
	}

	@Override
	public void collectConstants( ConstantPool pool )
	{
		ConstantUtf8 descriptor = pool.add( new ConstantUtf8( Types.toMethodDescriptor( null ) ) );
		ConstantUtf8 name = pool.add( new ConstantUtf8( this.name ) );
		ConstantClass cls = pool.add( new ConstantClass( pool, "java.lang.Object" ) );
		this.methodref = pool.add( new ConstantMethodref( pool, cls, name, descriptor ) );
	}

	@Override
	public void getByteCode( DataOutputStream out ) throws IOException
	{
		this.instance.getByteCode( out );
		out.writeByte( 0xB7 ); // invokespecial
		out.writeShort( this.methodref.index() );
	}
}
