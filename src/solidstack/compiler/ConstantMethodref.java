package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantMethodref extends Constant
{
	private ConstantClass classInfo;
	private ConstantNameAndType nameAndType;

	public ConstantMethodref( ConstantPool pool, String cls, String name, String descriptor )
	{
		this.classInfo = pool.add( new ConstantClass( pool, cls ) );
		this.nameAndType = pool.add( new ConstantNameAndType( pool, name, descriptor ) );
	}

	@Override
	public void write( DataOutputStream out ) throws IOException
	{
		out.writeByte( 10 );
		out.writeShort( this.classInfo.index() );
		out.writeShort( this.nameAndType.index() );
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
