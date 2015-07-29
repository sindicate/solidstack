package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantUtf8 extends Constant
{
	private String value;

	public ConstantUtf8( String value )
	{
		this.value = value;
	}

	@Override
	public void write( DataOutputStream out ) throws IOException
	{
		out.writeByte( 1 );
		byte[] bytes = this.value.getBytes( "UTF-8" );
		out.writeShort( bytes.length );
		out.write( bytes );
	}

	@Override
	public boolean equals( Object obj )
	{
		if( obj != null )
			if( obj.getClass() == ConstantUtf8.class )
			{
				ConstantUtf8 other = (ConstantUtf8)obj;
				return other.value.equals( this.value );
			}
		return false;
	}
}
