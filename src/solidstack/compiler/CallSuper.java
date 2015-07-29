package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

public class CallSuper extends Statement
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
		String descriptor = Types.toDescriptor( null );
		this.methodref = pool.add( new ConstantMethodref( pool, "java.lang.Object", "<init>", descriptor ) );
	}

	@Override
	public void getByteCode( DataOutputStream out ) throws IOException
	{
		out.writeByte( 0x19 ); // aload
		out.writeByte( 0 ); // 'this'
		out.writeByte( 0xB7 ); // invokespecial
		out.writeShort( this.methodref.index() );
		out.writeByte( 0xB1 ); // return
	}
}
