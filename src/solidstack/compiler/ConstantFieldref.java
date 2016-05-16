package solidstack.compiler;

import java.io.IOException;

public class ConstantFieldref extends Constant
{
	private ConstantClass classInfo;
	private ConstantNameAndType nameAndType;

	public ConstantFieldref( ConstantClass cls, ConstantNameAndType nameAndType )
	{
		this.classInfo = cls;
		this.nameAndType = nameAndType;
	}

	@Override
	public void write( Bytes bytes ) throws IOException
	{
		bytes.writeByte( 9 );
		bytes.writeShort( this.classInfo.index() );
		bytes.writeShort( this.nameAndType.index() );
	}

	@Override
	public boolean equals( Object obj )
	{
		if( obj != null )
			if( obj.getClass() == ConstantFieldref.class )
			{
				ConstantFieldref other = (ConstantFieldref)obj;
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
}
