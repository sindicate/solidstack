package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

public class AssignField extends Expression implements Statement
{
	private ClassBuilder cls;
	private Expression instance;
	private String name;
	private Expression value;

	private ConstantFieldref fieldref;

	public AssignField( ClassBuilder cls, Expression instance, String name, Expression value )
	{
		this.cls = cls;
		this.instance = instance;
		this.name = name;
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
		this.fieldref = this.cls.fieldref( this.name );
	}

	@Override
	public ConstantClass classInfo()
	{
		throw new UnsupportedOperationException();
	}
}
