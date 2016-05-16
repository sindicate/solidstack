package solidstack.classgen.constants;

import java.nio.charset.Charset;

import solidstack.classgen.Bytes;

public class CUtf8 extends Constant
{
	static public Charset UTF8 = Charset.forName( "UTF-8" );

	private String value;

	public CUtf8( String value )
	{
		this.value = value;
	}

	@Override
	public void write( Bytes out )
	{
		out.writeByte( 1 );
		byte[] bytes = this.value.getBytes( UTF8 );
		out.writeShort( bytes.length );
		out.write( bytes );
	}

	@Override
	public boolean equals( Object obj )
	{
		if( obj != null )
			if( obj.getClass() == CUtf8.class )
			{
				CUtf8 other = (CUtf8)obj;
				return other.value.equals( this.value );
			}
		return false;
	}

	public String value()
	{
		return this.value;
	}
}
