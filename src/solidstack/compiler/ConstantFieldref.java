package solidstack.compiler;

import java.io.IOException;

public class ConstantFieldref extends Constant
{
	private ConstantClass classInfo;
	private ConstantNameAndType nameAndType;

	public ConstantFieldref( ConstantPool pool, ConstantClass cls, ConstantUtf8 name, ConstantUtf8 descriptor )
	{
		this.classInfo = cls;
		this.nameAndType = pool.add( new ConstantNameAndType( pool, name, descriptor ) );
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
}
