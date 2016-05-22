package solidstack.classgen.constants;

import solidstack.classgen.Bytes;

public class CNameAndType extends Constant
{
	private CUtf8 name;
	private CUtf8 descriptor;

	public CNameAndType( CUtf8 name, CUtf8 descriptor )
	{
		this.name = name;
		this.descriptor = descriptor;
	}

	@Override
	public void write( Bytes bytes )
	{
		bytes.writeByte( 12 );
		bytes.writeShort( this.name.index() );
		bytes.writeShort( this.descriptor.index() );
	}

	@Override
	public boolean equals( Object obj )
	{
		if( obj != null )
			if( obj.getClass() == CNameAndType.class )
			{
				CNameAndType other = (CNameAndType)obj;
				return other.name == this.name && other.descriptor == this.descriptor;
			}
		return false;
	}

	public String name()
	{
		return this.name.value();
	}

	public int nameIndex()
	{
		return this.name.index();
	}

	public int typeIndex()
	{
		return this.descriptor.index();
	}

	public CUtf8 type()
	{
		return this.descriptor;
	}
}
