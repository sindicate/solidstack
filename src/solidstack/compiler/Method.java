package solidstack.compiler;

import java.util.ArrayList;
import java.util.List;

public class Method
{
	private ClassBuilder cls;
	private String name;

	private List<Statement> statements = new ArrayList<Statement>();

	private ConstantMethodref methodref;


	public Method( ClassBuilder classFile, ConstantMethodref methodref )
	{
		this.cls = classFile;
		this.methodref = methodref;
	}

	public ClassBuilder classBuilder()
	{
		return this.cls;
	}

	public void write( Bytes bytes )
	{
		bytes.writeShort( 0x1001 ); // public & synthetic
		bytes.writeShort( this.methodref.nameIndex() ); // name_index
		bytes.writeShort( this.methodref.typeIndex() ); // descriptor_index

		// attributes
		bytes.writeShort( 1 ); // attributes_count

		ConstantUtf8 codeAttribute = this.cls.addConstantUtf8( "Code" );

		// Code
		Bytes code = getCode();
		bytes.writeShort( codeAttribute.index() );
		bytes.writeInt( code.size() );
		bytes.write( code );
	}

	private Bytes getCode()
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

	private Bytes getByteCode()
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

	public void for_( Statement initialization, Conditional termination, Statement increment, Statement... statements )
	{
		this.statements.add( new For( initialization, termination, increment, statements ) );
	}
}
