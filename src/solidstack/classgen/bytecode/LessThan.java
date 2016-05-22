package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.Types.VMTYPE;

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
	public VMTYPE vmType()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Jump getJump( Bytes bytes )
	{
		this.left.toByteCode( bytes );
		this.right.toByteCode( bytes );
		Jump jump = new Jump( bytes, bytes.size() );
		bytes.writeByte( 0xA2 ); // if_icmpge
		bytes.writeShort( 0 );
		return jump;
	}
}
