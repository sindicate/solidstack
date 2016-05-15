package solidstack.compiler;

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

	public void write( Bytes bytes ) throws IOException
	{
		bytes.writeShort( 0x1001 ); // public & synthetic
		bytes.writeShort( this.nameInfo.index() ); // name_index
		bytes.writeShort( this.descriptor.index() ); // descriptor_index

		// attributes
		bytes.writeShort( 1 ); // attributes_count

		// Code
		Bytes code = getCode();
		bytes.writeShort( this.codeAttribute.index() );
		bytes.writeInt( code.size() );
		bytes.write( code );
	}

	private Bytes getCode() throws IOException
	{
		Bytes bytes = new Bytes();

		Bytes byteCode = getByteCode();

		bytes.writeShort( 10 ); // TODO max_stack
		bytes.writeShort( 10 ); // TODO max_locals
		bytes.writeInt( byteCode.size() ); // code_length
		bytes.write( byteCode );
		bytes.writeShort( 0 ); // TODO exception_table_length
		bytes.writeShort( 0 ); // TODO attributes_length

		return bytes;
	}

	private Bytes getByteCode() throws IOException
	{
		Bytes bytes = new Bytes();

		for( Statement stat : this.statements )
			stat.getByteCode( bytes );

		return bytes;
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

	public void for_( Statement initialization, Conditional termination, Statement increment, Statement... statements )
	{
		this.statements.add( new For( initialization, termination, increment, statements ) );
	}
}
