package solidstack.classgen.constants;

import solidstack.classgen.Bytes;

public class CMethodref extends Constant
{
	private CClass classInfo;
	private CNameAndType nameAndType;

	public CMethodref( CClass classInfo, CNameAndType nameAndType )
	{
		this.classInfo = classInfo;
		this.nameAndType = nameAndType;
	}

	@Override
	public void write( Bytes bytes )
	{
		bytes.writeByte( 10 );
		bytes.writeShort( this.classInfo.index() );
		bytes.writeShort( this.nameAndType.index() );
	}

	@Override
	public boolean equals( Object obj )
	{
		if( obj != null )
			if( obj.getClass() == CMethodref.class )
			{
				CMethodref other = (CMethodref)obj;
				return other.classInfo == this.classInfo && other.nameAndType == this.nameAndType;
			}
		return false;
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
