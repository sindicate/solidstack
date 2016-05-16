package solidstack.compiler;

public class AccessLocal implements Expression
{
	static public enum TYPE { INT, REF }

	private int local;
	private TYPE type;


	public AccessLocal( int local, TYPE type )
	{
		if( local < 0 )
			throw new IllegalArgumentException();
		this.local = local;
		this.type = type;
	}

	@Override
	public ConstantClass classInfo()
	{
//		if( this.local == 0 ) // TODO And non static
//			return this.method.classBuilder().classInfo();
		throw new UnsupportedOperationException();
	}

	@Override
	public void getByteCode( Bytes bytes )
	{
		if( this.local >= 256 )
			throw new UnsupportedOperationException(); // TODO Wide

		switch( this.type )
		{
			case INT:
				if( this.local < 4 )
				{
					bytes.writeByte( 0x1A + this.local ); // iload_0
					return;
				}
				bytes.writeByte( 0x15 ); // iload
				bytes.writeByte( this.local );
				return;

			case REF:
				if( this.local < 4 )
				{
					bytes.writeByte( 0x2A + this.local ); // aload_0
					return;
				}
				bytes.writeByte( 0x19 ); // aload
				bytes.writeByte( this.local );
				return;
		}
	}

	@Override
	public String getFieldDescriptor()
	{
		throw new UnsupportedOperationException();
	}
}
