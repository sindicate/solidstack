package solidstack.compiler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Method
{
	private ClassBuilder cls;
	private String name;
	private Class<?> ret;
	private Class<?>[] parameters;

	private List<Statement> statements = new ArrayList<Statement>();

	private ConstantUtf8 nameInfo;
	private ConstantUtf8 descriptor;
	private ConstantUtf8 codeAttribute;

	public Method( ClassBuilder cls, String name, java.lang.Class<?> ret, java.lang.Class<?>... parameters )
	{
		this.cls = cls;
		this.name = name;
		this.ret = ret;
		this.parameters = parameters;
	}

	public ClassBuilder classBuilder()
	{
		return this.cls;
	}

	public void collectConstants( ConstantPool pool )
	{
		this.nameInfo = pool.add( new ConstantUtf8( this.name ) );

		String descriptor = Types.toMethodDescriptor( this.ret, this.parameters );
		this.descriptor = pool.add( new ConstantUtf8( descriptor.toString() ) );

		this.codeAttribute = pool.add( new ConstantUtf8( "Code" ) );

		for( Statement stat : this.statements )
			stat.collectConstants( pool );
	}

	public void write( DataOutputStream out ) throws IOException
	{
		out.writeShort( 0x1001 ); // public & synthetic
		out.writeShort( this.nameInfo.index() ); // name_index
		out.writeShort( this.descriptor.index() ); // descriptor_index

		// attributes
		out.writeShort( 1 ); // attributes_count

		// Code
		byte[] code = getCode();
		out.writeShort( this.codeAttribute.index() );
		out.writeInt( code.length );
		out.write( code );
	}

	private byte[] getCode() throws IOException
	{
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( bytes );

		byte[] byteCode = getByteCode();

		out.writeShort( 10 ); // TODO max_stack
		out.writeShort( 10 ); // TODO max_locals
		out.writeInt( byteCode.length ); // code_length
		out.write( byteCode );
		out.writeShort( 0 ); // TODO exception_table_length
		out.writeShort( 0 ); // TODO attributes_length

		out.flush();
		return bytes.toByteArray();
	}

	private byte[] getByteCode() throws IOException
	{
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( bytes );

		for( Statement stat : this.statements )
			stat.getByteCode( out );

		out.flush();
		return bytes.toByteArray();
	}

	public void return_( int i )
	{
		this.statements.add( new Return( i ) );
	}

	public void return_()
	{
		this.statements.add( new Return() );
	}

	public void return_( Expression value )
	{
		this.statements.add( new Return( value ) );
	}

	public void addStatement( Statement statement )
	{
		this.statements.add( statement );
	}

	public String fieldDescriptor( int local )
	{
		local--;
		return Types.toFieldDescriptor( this.parameters[ local ] );
	}
}
