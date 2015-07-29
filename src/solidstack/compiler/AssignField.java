package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

public class AssignField extends Expression implements Statement
{
	private ClassBuilder cls;
	private Expression instance;
	private String name;
	private String descriptor;
	private Expression value;

	private ConstantFieldref fieldref;

	public AssignField( ClassBuilder cls, Expression instance, String name, String descriptor, Expression value )
	{
		this.cls = cls;
		this.instance = instance;
		this.name = name;
		this.descriptor = descriptor;
		this.value = value;
	}

	@Override
	public void getByteCode( DataOutputStream out ) throws IOException
	{
		this.instance.getByteCode( out );
		this.value.getByteCode( out );
		out.writeByte( 0xB5 ); // putfield
		out.writeShort( this.fieldref.index() );
	}

	@Override
	public void collectConstants( ConstantPool pool )
	{
		this.instance.collectConstants( pool );
		this.value.collectConstants( pool );

		ConstantUtf8 name = pool.add( new ConstantUtf8( this.name ) );
		ConstantClass cls = this.instance.classInfo();
		ConstantUtf8 descriptor = pool.add( new ConstantUtf8( this.descriptor ) );
		this.fieldref = pool.add( new ConstantFieldref( pool, cls, name, descriptor ) );
	}

	@Override
	public ConstantClass classInfo()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getFieldDescriptor()
	{
		throw new UnsupportedOperationException();
	}
}
