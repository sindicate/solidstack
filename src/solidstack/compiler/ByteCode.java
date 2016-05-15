package solidstack.compiler;

public class ByteCode
{
	static public void iconst( Bytes bytes, int i )
	{
		if( i >= -1 && i <= 5 )
		{
			bytes.writeByte( 0x03 + i ); // iconst_0 + the value
			return;
		}
		if( i >= -128 && i < 128 )
		{
			bytes.writeByte( 0x10 ); // bipush
			bytes.writeByte( i );
			return;
		}
		if( i >= -32768 && i < 32768 )
		{
			bytes.writeByte( 0x11 ); // sipush
			bytes.writeShort( i );
			return;
		}
		throw new UnsupportedOperationException();
	}
}
