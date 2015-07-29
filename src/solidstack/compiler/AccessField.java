package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

public class AccessField extends Expression
{
	private Expression instance;
	private String name;
	private String descriptor;

	private ConstantFieldref fieldref;

	public AccessField( Expression instance, String name, String descriptor )
	{
		this.instance = instance;
		this.name = name;
		this.descriptor = descriptor;
	}

	@Override
	public ConstantClass classInfo()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void getByteCode( DataOutputStream out ) throws IOException
	{
		this.instance.getByteCode( out );
		out.writeByte( 0xB4 );
		out.writeShort( this.fieldref.index() );
	}

	@Override
	public void collectConstants( ConstantPool pool )
	{
		this.instance.collectConstants( pool );

		ConstantUtf8 name = pool.add( new ConstantUtf8( this.name ) );
		ConstantClass cls = this.instance.classInfo();
		ConstantUtf8 descriptor = pool.add( new ConstantUtf8( this.descriptor ) );
		this.fieldref = pool.add( new ConstantFieldref( pool, cls, name, descriptor ) );
	}

	@Override
	public String getFieldDescriptor()
	{
		return this.descriptor;
	}
}
