package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantClass extends Constant
{
	private ConstantUtf8 utf8;

	public ConstantClass( ConstantPool pool, String name )
	{
		this.utf8 = pool.add( new ConstantUtf8( name.replace( '.', '/' ) ) );
	}

	@Override
	public void write( DataOutputStream out ) throws IOException
	{
		out.writeByte( 7 );
		out.writeShort( this.utf8.index() );
	}

	@Override
	public boolean equals( Object obj )
	{
		if( obj != null )
			if( obj.getClass() == ConstantClass.class )
			{
				ConstantClass other = (ConstantClass)obj;
				return other.utf8 == this.utf8;
			}
		return false;
	}
}
