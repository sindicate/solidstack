package solidstack.compiler;

import java.io.IOException;

public class ConstantMethodref extends Constant
{
	private ConstantClass classInfo;
	private ConstantNameAndType nameAndType;

	public ConstantMethodref( ConstantPool pool, ConstantClass cls, ConstantUtf8 name, ConstantUtf8 descriptor )
	{
		this.classInfo = cls;
		this.nameAndType = pool.add( new ConstantNameAndType( pool, name, descriptor ) );
	}

	@Override
	public void write( Bytes bytes ) throws IOException
	{
		bytes.writeByte( 10 );
		bytes.writeShort( this.classInfo.index() );
		bytes.writeShort( this.nameAndType.index() );
	}

	@Override
	public boolean equals( Object obj )
	{
		if( obj != null )
			if( obj.getClass() == ConstantMethodref.class )
			{
				ConstantMethodref other = (ConstantMethodref)obj;
				return other.classInfo == this.classInfo && other.nameAndType == this.nameAndType;
			}
		return false;
	}
}
