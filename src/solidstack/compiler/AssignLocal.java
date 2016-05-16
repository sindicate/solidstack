package solidstack.compiler;

public class AssignLocal implements Statement
{
	private int local;
	private Expression value;

	private ConstantFieldref fieldref;


	public AssignLocal( int local, Expression value )
	{
		if( local < 0 )
			throw new IllegalArgumentException();
		this.local = local;
		this.value = value;
	}

	@Override
	public void getByteCode( Bytes bytes )
	{
		this.value.getByteCode( bytes );
		if( this.local < 4 )
		{
			bytes.writeByte( 0x3B + this.local ); // istore_0
			return;
		}
		if( this.local >= 256 )
			throw new UnsupportedOperationException(); // TODO Wide
		bytes.writeByte( 0x36 ); // istore
		bytes.writeByte( this.local );
	}
}
