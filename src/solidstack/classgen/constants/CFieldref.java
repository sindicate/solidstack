package solidstack.classgen.constants;

import solidstack.classgen.Bytes;

public class CFieldref extends Constant
{
	private CClass classInfo;
	private CNameAndType nameAndType;

	public CFieldref( CClass cls, CNameAndType nameAndType )
	{
		this.classInfo = cls;
		this.nameAndType = nameAndType;
	}

	@Override
	public void write( Bytes bytes )
	{
		bytes.writeByte( 9 );
		bytes.writeShort( this.classInfo.index() );
		bytes.writeShort( this.nameAndType.index() );
	}

	@Override
	public boolean equals( Object obj )
	{
		if( obj != null )
			if( obj.getClass() == CFieldref.class )
			{
				CFieldref other = (CFieldref)obj;
				return other.classInfo == this.classInfo && other.nameAndType == this.nameAndType;
			}
		return false;
	}

	public String name()
	{
		return this.nameAndType.name();
	}

	public int nameIndex()
	{
		return this.nameAndType.nameIndex();
	}

	public int typeIndex()
	{
		return this.nameAndType.typeIndex();
	}

	public CUtf8 type()
	{
		return this.nameAndType.type();
	}
}
