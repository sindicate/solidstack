package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

public class CallSuper implements Statement
{
	private String name;
	private ConstantMethodref methodref;

	public CallSuper( String name )
	{
		this.name = name;
	}

	@Override
	public void collectConstants( ConstantPool pool )
	{
		ConstantUtf8 descriptor = pool.add( new ConstantUtf8( Types.toMethodDescriptor( null ) ) );
		ConstantUtf8 name = pool.add( new ConstantUtf8( this.name ) );
		this.methodref = pool.add( new ConstantMethodref( pool, "java.lang.Object", name, descriptor ) );
	}

	@Override
	public void getByteCode( DataOutputStream out ) throws IOException
	{
		out.writeByte( 0x19 ); // aload
		out.writeByte( 0 ); // 'this'
		out.writeByte( 0xB7 ); // invokespecial
		out.writeShort( this.methodref.index() );
	}
}
