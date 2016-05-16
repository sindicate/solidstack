package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.constants.CClass;

public class LessThan implements Conditional
{
	private Expression left;
	private Expression right;

	public LessThan( Expression left, Expression right )
	{
		this.left = left;
		this.right = right;
	}

	@Override
	public CClass classInfo()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void getByteCode( Bytes bytes )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Jump getJump( Bytes bytes )
	{
		this.left.getByteCode( bytes );
		this.right.getByteCode( bytes );
		int pc = bytes.size();
		bytes.writeByte( 0xA2 ); // if_icmpge
		bytes.writeShort( 0 );
		return new Jump( bytes, pc );
	}

	@Override
	public String getFieldDescriptor()
	{
		throw new UnsupportedOperationException();
	}
}
