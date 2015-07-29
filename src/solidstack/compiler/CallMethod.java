package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

public class CallMethod extends Expression
{
	private Expression instance;
	private String name;
	private String descriptor;
	private Expression[] parameters;

	private ConstantMethodref methodref;

	public CallMethod( Expression instance, String name, String descriptor, Expression... parameter )
	{
		this.instance = instance;
		this.name = name;
		this.descriptor = descriptor;
		this.parameters = parameter;
	}

	@Override
	public ConstantClass classInfo()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void collectConstants( ConstantPool pool )
	{
		this.instance.collectConstants( pool );
		for( Expression parameter : this.parameters )
			parameter.collectConstants( pool );

		ConstantUtf8 name = pool.add( new ConstantUtf8( this.name ) );
		String fieldDescriptor = this.instance.getFieldDescriptor();
		String className = Types.fieldDescriptorToClassName( fieldDescriptor );
		ConstantClass cls = pool.add( new ConstantClass( pool, className ) );
		ConstantUtf8 descriptor = pool.add( new ConstantUtf8( this.descriptor ) );
		this.methodref = pool.add( new ConstantMethodref( pool, cls, name, descriptor ) );
	}

	@Override
	public void getByteCode( DataOutputStream out ) throws IOException
	{
		this.instance.getByteCode( out );
		for( Expression parameter : this.parameters )
			parameter.getByteCode( out );
		out.writeByte( 0xB6 );
		out.writeShort( this.methodref.index() );
	}

	@Override
	public String getFieldDescriptor()
	{
		throw new UnsupportedOperationException();
	}
}
