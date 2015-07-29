package solidstack.compiler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Method
{
	private String name;
	private Class<?> ret;
	private Class<?>[] parameters;

	private List<Statement> statements = new ArrayList<Statement>();

	private ConstantUtf8 nameInfo;
	private ConstantUtf8 descriptor;
	private ConstantUtf8 codeAttribute;

	public Method( String name, java.lang.Class<?> ret, java.lang.Class<?>... parameters )
	{
		this.name = name;
		this.ret = ret;
		this.parameters = parameters;
	}

	public void collectConstants( ConstantPool pool )
	{
		this.nameInfo = pool.add( new ConstantUtf8( this.name ) );

		StringBuilder descriptor = new StringBuilder();
		descriptor.append( '(' );
		for( int i = 0; i < this.parameters.length; i++ )
			descriptor.append( Types.toFieldType( this.parameters[ i ] ) );
		descriptor.append( ')' );
		if( this.ret == null )
			descriptor.append( 'V' );
		else
			descriptor.append( Types.toFieldType( this.ret ) );

		this.descriptor = pool.add( new ConstantUtf8( descriptor.toString() ) );

		this.codeAttribute = pool.add( new ConstantUtf8( "Code" ) );
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
}
