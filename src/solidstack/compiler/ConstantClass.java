package solidstack.compiler;

import java.io.IOException;

public class ConstantClass extends Constant
{
	private ConstantUtf8 utf8;

	public ConstantClass( ConstantUtf8 utf8 )
	{
		this.utf8 = utf8;
	}

	@Override
	public void write( Bytes bytes ) throws IOException
	{
		bytes.writeByte( 7 );
		bytes.writeShort( this.utf8.index() );
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

	public String name()
	{
		return this.utf8.value();
	}
}
