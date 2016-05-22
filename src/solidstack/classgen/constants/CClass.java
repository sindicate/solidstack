package solidstack.classgen.constants;

import solidstack.classgen.Bytes;

public class CClass extends Constant
{
	private CUtf8 utf8;

	public CClass( CUtf8 utf8 )
	{
		this.utf8 = utf8;
	}

	@Override
	public void write( Bytes bytes )
	{
		bytes.writeByte( 7 );
		bytes.writeShort( this.utf8.index() );
	}

	@Override
	public boolean equals( Object obj )
	{
		if( obj != null )
			if( obj.getClass() == CClass.class )
			{
				CClass other = (CClass)obj;
				return other.utf8 == this.utf8;
			}
		return false;
	}

	public String name()
	{
		return this.utf8.value();
	}
}
