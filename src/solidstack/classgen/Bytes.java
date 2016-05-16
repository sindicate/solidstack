package solidstack.classgen;

public class Bytes
{
	private byte[] bytes = new byte[ 4096 ];
	private int pos;


	public byte[] toByteArray()
	{
		byte[] result = new byte[ this.pos ];
		System.arraycopy( this.bytes, 0, result, 0, this.pos );
		return result;
	}

	public int size()
	{
		return this.pos;
	}

    public void writeByte( int b )
	{
		if( b < -128 || b > 255 )
			throw new IllegalArgumentException( Integer.toString( b ) );
    	if( this.pos >= this.bytes.length )
    	{
    		byte[] old = this.bytes;
    		this.bytes = new byte[ old.length * 2 ];
    		System.arraycopy( old, 0, this.bytes, 0, old.length );
    	}
    	this.bytes[ this.pos++ ] = (byte)b;
 	}

	public void writeShort( int s )
	{
		if( s < -32768 || s > 32767 )
			throw new IllegalArgumentException( Integer.toString( s ) );
		writeByte( s >>> 8 & 0xFF ); // >>> has precedence
		writeByte( s & 0xFF );
	}

	public void writeShortAt( int pos, int s )
	{
		if( s < -32768 || s > 32767 )
			throw new IllegalArgumentException( Integer.toString( s ) );
    	this.bytes[ pos++ ] = (byte)( s >>> 8 & 0xFF ); // >>> has precedence
    	this.bytes[ pos++ ] = (byte)( s & 0xFF );
	}

    public void writeInt( int i )
	{
		writeByte( i >>> 24 & 0xFF ); // >>> has precedence
		writeByte( i >>> 16 & 0xFF );
		writeByte( i >>> 8 & 0xFF );
		writeByte( i & 0xFF );
 	}

	public void write( byte[] code )
	{
		for( byte b : code )
			writeByte( b );
	}

	public void write( Bytes b )
	{
		byte[] s = b.bytes;
		int len = b.pos;
		for( int i = 0; i < len; i++ )
			writeByte( s[ i ] );
	}
}
