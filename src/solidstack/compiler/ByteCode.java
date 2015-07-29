package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

public class ByteCode
{
	static public void iconst( DataOutputStream out, int i ) throws IOException
	{
		switch( i )
		{
			case -1: out.writeByte( 0x02 ); return;
			case 0: out.writeByte( 0x03 ); return;
			case 1: out.writeByte( 0x04 ); return;
			case 2: out.writeByte( 0x05 ); return;
			case 3: out.writeByte( 0x06 ); return;
			case 4: out.writeByte( 0x07 ); return;
			case 5: out.writeByte( 0x08 ); return;
		}
		if( i >= -128 && i < 128 )
		{
			out.writeByte( 0x10 ); // bipush
			out.writeByte( i );
			return;
		}
		throw new UnsupportedOperationException();
	}
}
