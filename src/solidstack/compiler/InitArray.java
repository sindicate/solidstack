package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

public class InitArray extends Expression
{
	private String type;
	private Expression[] values;

	private ConstantClass classInfo;

	public InitArray( String type, Expression... values )
	{
		this.type = type;
		this.values = values;
	}

	@Override
	public ConstantClass classInfo()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void getByteCode( DataOutputStream out ) throws IOException
	{
		ByteCode.iconst( out, this.values.length );
		out.writeByte( 0xBD ); // anewarray
		out.writeShort( this.classInfo.index() );
		int i = 0;
		for( Expression value : this.values )
		{
			out.writeByte( 0x59 ); // dup
			ByteCode.iconst( out, i++ );
			value.getByteCode( out );
			out.writeByte( 0x53 ); // aastore
		}
	}

	@Override
	public void collectConstants( ConstantPool pool )
	{
		for( Expression value : this.values )
			value.collectConstants( pool );
		this.classInfo = pool.add( new ConstantClass( pool, this.type ) );
	}

	@Override
	public String getFieldDescriptor()
	{
		throw new UnsupportedOperationException();
	}
}
