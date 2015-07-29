package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantNameAndType extends Constant
{
	private ConstantUtf8 name;
	private ConstantUtf8 descriptor;

	public ConstantNameAndType( ConstantPool pool, ConstantUtf8 name, ConstantUtf8 descriptor )
	{
		this.name = name;
		this.descriptor = descriptor;
	}

	@Override
	public void write( DataOutputStream out ) throws IOException
	{
		out.writeByte( 12 );
		out.writeShort( this.name.index() );
		out.writeShort( this.descriptor.index() );
	}

	@Override
	public boolean equals( Object obj )
	{
		if( obj != null )
			if( obj.getClass() == ConstantNameAndType.class )
			{
				ConstantNameAndType other = (ConstantNameAndType)obj;
				return other.name == this.name && other.descriptor == this.descriptor;
			}
		return false;
	}
}
