package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.Types.VMTYPE;


public class AccessLocal implements Expression
{
	private int local;
	private VMTYPE type;


	public AccessLocal( int local, VMTYPE type )
	{
		if( local < 0 )
			throw new IllegalArgumentException();
		this.local = local;
		this.type = type;
	}

	@Override
	public VMTYPE vmType()
	{
		return this.type;
	}

	@Override
	public void toByteCode( Bytes bytes )
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
}
